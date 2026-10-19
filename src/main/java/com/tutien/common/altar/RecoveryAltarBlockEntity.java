package com.tutien.common.altar;

import com.tutien.common.config.TuTienConfig;
import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.currency.SpiritStoneHelper;
import com.tutien.common.registry.ModBlockEntities;
import com.tutien.common.registry.ModDataAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RecoveryAltarBlockEntity extends BlockEntity {
    public enum AltarTier {
        TOWN(1, "Tế Đàn Thị Trấn"),
        CANGYUN(2, "Tế Đàn Thương Vân Tông"),
        KUNLUN(3, "Tế Đàn Côn Luân Thánh Địa");

        private final int id;
        private final String name;

        AltarTier(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public String getName() { return name; }
    }

    private AltarTier tier = AltarTier.TOWN;
    private boolean isFixedInWorld = false;

    public RecoveryAltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.RECOVERY_ALTAR.get(), pos, blockState);
    }

    public AltarTier getTier() {
        return tier;
    }

    public void setTier(AltarTier tier) {
        this.tier = tier;
        setChanged();
    }

    public boolean isFixedInWorld() {
        return isFixedInWorld;
    }

    public void setFixedInWorld(boolean fixed) {
        this.isFixedInWorld = fixed;
        setChanged();
    }

    public long getRequiredCost() {
        return switch (tier) {
            case TOWN -> TuTienConfig.TOWN_ALTAR_COST.get();
            case CANGYUN -> TuTienConfig.CANGYUN_ALTAR_COST.get();
            case KUNLUN -> TuTienConfig.KUNLUN_ALTAR_COST.get();
        };
    }

    public void activateAltar(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        long cost = getRequiredCost();
        if (!SpiritStoneHelper.hasEnough(serverPlayer, cost)) {
            serverPlayer.sendSystemMessage(Component.literal("§c[Tế Đàn] Không đủ linh thạch để khởi động tế đàn! Yêu cầu: §e" + cost + " Hạ Phẩm Linh Thạch."));
            return;
        }

        PlayerCultivationData data = serverPlayer.getData(ModDataAttachments.CULTIVATION_DATA);
        if (data == null) return;

        // Deduct spirit stones safely
        if (SpiritStoneHelper.deductValue(serverPlayer, cost)) {
            // Restore Health
            serverPlayer.setHealth(serverPlayer.getMaxHealth());

            // Restore LingQi
            data.setLingQi(data.getMaxLingQi());

            // Clear harmful potion effects
            serverPlayer.removeAllEffects();

            // Sound and effects
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                        worldPosition.getX() + 0.5, worldPosition.getY() + 1.2, worldPosition.getZ() + 0.5,
                        30, 0.5, 0.5, 0.5, 0.2);
                serverLevel.sendParticles(ParticleTypes.FLASH,
                        worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5,
                        2, 0, 0, 0, 0);
            }
            level.playSound(null, worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.5f, 1.2f);
            level.playSound(null, worldPosition, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.8f, 1.0f);

            serverPlayer.sendSystemMessage(Component.literal("§a§l[Tế Đàn] Tế đàn đã khởi động! Tiêu hao: §e" + cost + " Hạ Phẩm Linh Thạch."));
            serverPlayer.sendSystemMessage(Component.literal("§bKhí huyết và linh khí đã được hồi phục hoàn toàn, toàn bộ tạp khí đã được tẩy trừ!"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Tier", tier.getId());
        tag.putBoolean("FixedInWorld", isFixedInWorld);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        int t = tag.getInt("Tier");
        this.tier = switch (t) {
            case 2 -> AltarTier.CANGYUN;
            case 3 -> AltarTier.KUNLUN;
            default -> AltarTier.TOWN;
        };
        this.isFixedInWorld = tag.getBoolean("FixedInWorld");
    }
}
