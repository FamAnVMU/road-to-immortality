package com.tutien.common.array;

import com.tutien.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CultivationArrayBlockEntity extends BlockEntity {
    // Array Multipliers by Tier (1 to 9)
    private static final double[] MULTIPLIERS = {1.5, 2.0, 2.5, 3.0, 4.0, 5.0, 6.5, 8.0, 10.0};
    private static final int[] RADII = {5, 6, 7, 8, 9, 10, 11, 12, 15};

    private int tier = 1; // 1 to 9
    private int tickCount = 0;

    // Static registry of active player multipliers: Player UUID -> Map of Array Pos -> Multiplier
    private static final Map<UUID, Map<BlockPos, Double>> ACTIVE_PLAYER_ARRAYS = new HashMap<>();

    public CultivationArrayBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CULTIVATION_ARRAY.get(), pos, blockState);
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = Math.max(1, Math.min(9, tier));
        setChanged();
    }

    public String getTierName() {
        return "Tụ Linh Trận Cấp " + tier;
    }

    public int getRadius() {
        return RADII[tier - 1];
    }

    public double getMultiplier() {
        return MULTIPLIERS[tier - 1];
    }

    public static double getMaxArrayMultiplierForPlayer(UUID playerId) {
        Map<BlockPos, Double> arrays = ACTIVE_PLAYER_ARRAYS.get(playerId);
        if (arrays == null || arrays.isEmpty()) {
            return 1.0;
        }
        double max = 1.0;
        for (double mult : arrays.values()) {
            if (mult > max) max = mult;
        }
        return max;
    }

    public static void cleanUpPlayer(UUID playerId) {
        ACTIVE_PLAYER_ARRAYS.remove(playerId);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        tickCount++;
        int radius = getRadius();
        double multiplier = getMultiplier();

        if (level instanceof ServerLevel serverLevel) {
            // Spawn ring particles every 10 ticks
            if (tickCount % 10 == 0) {
                double angle = (tickCount % 360) * Math.PI / 180.0;
                for (int i = 0; i < 8; i++) {
                    double theta = angle + i * (Math.PI / 4.0);
                    double px = pos.getX() + 0.5 + radius * Math.cos(theta);
                    double pz = pos.getZ() + 0.5 + radius * Math.sin(theta);
                    serverLevel.sendParticles(ParticleTypes.ENCHANT, px, pos.getY() + 0.2, pz, 1, 0, 0.1, 0, 0.05);
                }
            }

            // Check players in range every 20 ticks (1 second)
            if (tickCount % 20 == 0) {
                AABB bounds = new AABB(pos).inflate(radius);
                for (ServerPlayer player : serverLevel.players()) {
                    boolean inRange = bounds.contains(player.position());
                    UUID uuid = player.getUUID();
                    Map<BlockPos, Double> playerArrays = ACTIVE_PLAYER_ARRAYS.computeIfAbsent(uuid, k -> new HashMap<>());

                    if (inRange) {
                        playerArrays.put(pos, multiplier);
                    } else {
                        playerArrays.remove(pos);
                    }
                }
            }
        }
    }

    @Override
    public void setRemoved() {
        // Clean up this array from all player maps
        for (Map<BlockPos, Double> map : ACTIVE_PLAYER_ARRAYS.values()) {
            map.remove(worldPosition);
        }
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Tier", tier);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tier = Math.max(1, Math.min(9, tag.getInt("Tier")));
    }
}
