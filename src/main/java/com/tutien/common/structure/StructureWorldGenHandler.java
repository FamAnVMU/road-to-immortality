package com.tutien.common.structure;

import com.tutien.TuTienMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

@EventBusSubscriber(modid = TuTienMod.MOD_ID)
public class StructureWorldGenHandler {

    // Grid size in chunks (36 chunks = 576 blocks)
    private static final int REGION_SIZE_CHUNKS = 36;

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!event.isNewChunk()) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.dimension() != Level.OVERWORLD) return;

        ChunkPos chunkPos = event.getChunk().getPos();

        // Delay execution to server tick to avoid chunk-loading deadlocks
        level.getServer().execute(() -> {
            checkAndGenerateStructure(level, chunkPos);
        });
    }

    private static void checkAndGenerateStructure(ServerLevel level, ChunkPos chunkPos) {
        int cx = chunkPos.x;
        int cz = chunkPos.z;

        StructureTracker tracker = StructureTracker.get(level);

        // 1. Guaranteed Starter Town near world spawn (chunk 10, 10 ~ X=160, Z=160)
        if (tracker.getTotalStructureCount() == 0 && Math.abs(cx) <= 18 && Math.abs(cz) <= 18) {
            if (cx == 10 && cz == 10) {
                attemptSpawnStructure(level, "thitran", chunkPos.getMiddleBlockPosition(0));
                return;
            }
        }

        // 2. Regional grid check
        int regionX = Math.floorDiv(cx, REGION_SIZE_CHUNKS);
        int regionZ = Math.floorDiv(cz, REGION_SIZE_CHUNKS);

        long seed = level.getSeed();
        long regionSeed = seed ^ (regionX * 341873128712L + regionZ * 132897987541L);
        RandomSource regionRandom = RandomSource.create(regionSeed);

        // Candidate chunk offset within region [4, REGION_SIZE_CHUNKS - 5]
        int candOffsetX = 4 + regionRandom.nextInt(REGION_SIZE_CHUNKS - 8);
        int candOffsetZ = 4 + regionRandom.nextInt(REGION_SIZE_CHUNKS - 8);

        int targetChunkX = regionX * REGION_SIZE_CHUNKS + candOffsetX;
        int targetChunkZ = regionZ * REGION_SIZE_CHUNKS + candOffsetZ;

        if (cx == targetChunkX && cz == targetChunkZ) {
            // 75% chance for region to contain a cultivation structure
            if (regionRandom.nextFloat() > 0.75f) return;

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

            BlockPos originPos = chunkPos.getMiddleBlockPosition(0);
            attemptSpawnStructure(level, structureType, originPos);
        }
    }

    private static void attemptSpawnStructure(ServerLevel level, String structureType, BlockPos roughPos) {
        StructureTracker tracker = StructureTracker.get(level);
        if (tracker.hasAnyStructureNearby(roughPos, 400.0)) {
            return;
        }

        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, roughPos.getX(), roughPos.getZ());
        if (surfaceY < level.getSeaLevel()) {
            return; // Don't spawn submerged in ocean
        }

        BlockPos spawnPos = new BlockPos(roughPos.getX(), surfaceY, roughPos.getZ());
        StructureSpawner.spawnStructure(level, structureType, spawnPos);
    }
}
