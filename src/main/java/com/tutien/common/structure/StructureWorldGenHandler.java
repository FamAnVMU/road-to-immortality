package com.tutien.common.structure;

import com.tutien.TuTienMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.List;

@EventBusSubscriber(modid = TuTienMod.MOD_ID)
public class StructureWorldGenHandler {

    // Grid size in chunks (36 chunks = 576 blocks)
    public static final int REGION_SIZE_CHUNKS = 36;
    public static final int REGION_SIZE_BLOCKS = REGION_SIZE_CHUNKS * 16;

    public record StructureCandidate(String type, int chunkX, int chunkZ) {
        public BlockPos getCenterBlockPos() {
            return new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8);
        }
    }

    /**
     * Ticking check on the server thread (every 60 ticks = 3 seconds).
     * Never interferes with chunk loading to prevent deadlocks and worldgen freezes.
     */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % 60 != 0) return;

        ServerLevel level = event.getServer().overworld();
        if (level == null) return;

        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) return;

        StructureTracker tracker = StructureTracker.get(level);

        for (ServerPlayer player : players) {
            checkAndSpawnNearPlayer(level, player, tracker);
        }
    }

    private static void checkAndSpawnNearPlayer(ServerLevel level, ServerPlayer player, StructureTracker tracker) {
        BlockPos playerPos = player.blockPosition();
        int playerChunkX = playerPos.getX() >> 4;
        int playerChunkZ = playerPos.getZ() >> 4;

        int playerRegionX = Math.floorDiv(playerChunkX, REGION_SIZE_CHUNKS);
        int playerRegionZ = Math.floorDiv(playerChunkZ, REGION_SIZE_CHUNKS);

        long seed = level.getSeed();

        // Check 3x3 regions surrounding the player
        for (int rx = playerRegionX - 1; rx <= playerRegionX + 1; rx++) {
            for (int rz = playerRegionZ - 1; rz <= playerRegionZ + 1; rz++) {
                StructureCandidate candidate = getCandidateForRegion(seed, rx, rz);
                if (candidate == null) continue;

                BlockPos candPos = candidate.getCenterBlockPos();
                double distSqr = playerPos.distSqr(candPos);

                // Only generate if player is within 96 blocks (chunks around player are safely loaded)
                if (distSqr <= 96.0 * 96.0) {
                    // Check if already spawned
                    if (tracker.hasAnyStructureNearby(candPos, 100.0)) {
                        continue;
                    }

                    // Crucial safety check: Target chunk must already be loaded by the player
                    if (!level.hasChunk(candidate.chunkX(), candidate.chunkZ())) {
                        continue;
                    }

                    // Determine ground surface height safely
                    int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, candPos.getX(), candPos.getZ());
                    if (surfaceY < level.getMinBuildHeight() + 10 || surfaceY < level.getSeaLevel()) {
                        continue;
                    }

                    BlockPos spawnPos = new BlockPos(candPos.getX(), surfaceY, candPos.getZ());
                    StructureSpawner.spawnStructure(level, candidate.type(), spawnPos);
                    tracker.recordStructure(candidate.type(), spawnPos);
                }
            }
        }
    }

    /**
     * Deterministic structure candidate generation based on world seed.
     */
    public static StructureCandidate getCandidateForRegion(long seed, int regionX, int regionZ) {
        // 1. Guaranteed Starter Town near spawn: Region (0, 0) has Town at Chunk (10, 10)
        if (regionX == 0 && regionZ == 0) {
            return new StructureCandidate("thitran", 10, 10);
        }

        long regionSeed = seed ^ (regionX * 341873128712L + regionZ * 132897987541L);
        RandomSource regionRandom = RandomSource.create(regionSeed);

        // 75% chance for region to contain a cultivation structure
        if (regionRandom.nextFloat() > 0.75f) {
            return null;
        }

        // Ratio: 15 Thị Trấn : 5 Thương Vân Tông : 1 Côn Luân Thánh Địa (Total: 21)
        int roll = regionRandom.nextInt(21);
        String structureType;
        if (roll < 15) {
            structureType = "thitran";
        } else if (roll < 20) {
            structureType = "thuongvan";
        } else {
            structureType = "conluan";
        }

        int candOffsetX = 4 + regionRandom.nextInt(REGION_SIZE_CHUNKS - 8);
        int candOffsetZ = 4 + regionRandom.nextInt(REGION_SIZE_CHUNKS - 8);

        int targetChunkX = regionX * REGION_SIZE_CHUNKS + candOffsetX;
        int targetChunkZ = regionZ * REGION_SIZE_CHUNKS + candOffsetZ;

        return new StructureCandidate(structureType, targetChunkX, targetChunkZ);
    }

    /**
     * Finds the nearest predetermined candidate coordinates for a structure type.
     */
    public static BlockPos findNearestCandidate(ServerLevel level, String structureType, BlockPos currentPos) {
        int playerChunkX = currentPos.getX() >> 4;
        int playerChunkZ = currentPos.getZ() >> 4;
        int playerRegionX = Math.floorDiv(playerChunkX, REGION_SIZE_CHUNKS);
        int playerRegionZ = Math.floorDiv(playerChunkZ, REGION_SIZE_CHUNKS);

        long seed = level.getSeed();
        BlockPos nearest = null;
        double minDst = Double.MAX_VALUE;

        // Search in expanding rings up to radius 12 regions (~6900 blocks)
        for (int r = 0; r <= 12; r++) {
            for (int rx = playerRegionX - r; rx <= playerRegionX + r; rx++) {
                for (int rz = playerRegionZ - r; rz <= playerRegionZ + r; rz++) {
                    if (r > 0 && Math.abs(rx - playerRegionX) < r && Math.abs(rz - playerRegionZ) < r) {
                        continue;
                    }

                    StructureCandidate cand = getCandidateForRegion(seed, rx, rz);
                    if (cand != null && cand.type().equalsIgnoreCase(structureType)) {
                        BlockPos pos = cand.getCenterBlockPos();
                        double dst = pos.distSqr(currentPos);
                        if (dst < minDst) {
                            minDst = dst;
                            nearest = pos;
                        }
                    }
                }
            }
            if (nearest != null) {
                return nearest;
            }
        }
        return nearest;
    }
}
