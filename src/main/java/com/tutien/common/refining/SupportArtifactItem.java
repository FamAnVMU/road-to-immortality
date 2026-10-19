package com.tutien.common.refining;

import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.registry.ModDataAttachments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SupportArtifactItem extends Item {
    public enum ArtifactType {
        HO_TAM_KINH("Hộ Tâm Kính", "Địa Cấp", "§5", 40.0f, 200, "Tạo kết giới hộ thể, miễn nhiễm sát thương trong 5 giây"),
        CHIEU_YEU_KINH("Chiếu Yêu Kính", "Huyền Cấp", "§9", 25.0f, 160, "Soi rọi toàn bộ sinh vật xung quanh trong bán kính 30 ô"),
        PHIEN_THIEN_AN("Phiên Thiên Ấn", "Địa Cấp", "§5", 80.0f, 300, "Đập tan mặt đất, gây chấn động diện rộng và làm chậm"),
        CHU_TIEN_TRAN_DO("Tru Tiên Trận Đồ", "Thiên Cấp", "§6§l", 150.0f, 500, "Triệu hồi Tru Tiên Kiếm Trận giam cầm và tiêu diệt vạn vật");

        private final String name;
        private final String tier;
        private final String color;
        private final float qiCost;
        private final int cooldownTicks;
        private final String effectDescription;

        ArtifactType(String name, String tier, String color, float qiCost, int cooldownTicks, String effectDescription) {
            this.name = name;
            this.tier = tier;
            this.color = color;
            this.qiCost = qiCost;
            this.cooldownTicks = cooldownTicks;
            this.effectDescription = effectDescription;
        }

        public String getName() { return name; }
        public String getTier() { return tier; }
        public String getColor() { return color; }
        public float getQiCost() { return qiCost; }
        public int getCooldownTicks() { return cooldownTicks; }
        public String getEffectDescription() { return effectDescription; }
    }

    private final ArtifactType artifactType;

    public SupportArtifactItem(ArtifactType artifactType) {
        super(new Item.Properties().stacksTo(1));
        this.artifactType = artifactType;
    }

    public ArtifactType getArtifactType() {
        return artifactType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PlayerCultivationData data = serverPlayer.getData(ModDataAttachments.CULTIVATION_DATA);
            if (data == null) return InteractionResultHolder.pass(stack);

            if (data.getLingQi() < artifactType.getQiCost()) {
                serverPlayer.sendSystemMessage(Component.literal("§c[Tu Tiên] Linh khí không đủ! (Cần " + (int)artifactType.getQiCost() + " Qi)"));
                return InteractionResultHolder.fail(stack);
            }

            data.consumeLingQi(artifactType.getQiCost());
            player.getCooldowns().addCooldown(this, artifactType.getCooldownTicks());

            executeArtifactSkill((ServerLevel) level, serverPlayer);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private void executeArtifactSkill(ServerLevel level, ServerPlayer player) {
        switch (artifactType) {
            case HO_TAM_KINH -> {
                // Resistance & Absorption for 5 seconds
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 4));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 2));
                level.sendParticles(ParticleTypes.ENCHANTED_HIT, player.getX(), player.getY() + 1.0, player.getZ(), 50, 0.5, 0.5, 0.5, 0.1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.5f, 1.2f);
                player.sendSystemMessage(Component.literal("§5[Pháp Khí] Hộ Tâm Kính bảo vệ chân thân!"));
            }
            case CHIEU_YEU_KINH -> {
                // Glowing for all mobs in 30 blocks
                AABB box = player.getBoundingBox().inflate(30.0);
                level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player).forEach(e -> {
                    e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300, 0));
                });
                level.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.5, player.getZ(), 40, 1.0, 1.0, 1.0, 0.05);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.5f, 1.5f);
                player.sendSystemMessage(Component.literal("§9[Pháp Khí] Chiếu Yêu Kính phá tan mê chướng!"));
            }
            case PHIEN_THIEN_AN -> {
                // Shockwave AoE damage + Slowness
                AABB box = player.getBoundingBox().inflate(8.0);
                level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, player.getX(), player.getY(), player.getZ(), 1, 0, 0, 0, 0);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.5f, 0.8f);
                level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive()).forEach(e -> {
                    e.hurt(player.damageSources().magic(), 40.0f);
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 2));
                });
                player.sendSystemMessage(Component.literal("§5[Pháp Khí] Phiên Thiên Ấn trấn áp bát hoang!"));
            }
            case CHU_TIEN_TRAN_DO -> {
                // Giant sword formation
                AABB box = player.getBoundingBox().inflate(12.0);
                level.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1.0, player.getZ(), 5, 0.5, 0.5, 0.5, 0.0);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1.0f, 1.2f);
                level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive()).forEach(e -> {
                    e.hurt(player.damageSources().magic(), 75.0f);
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 160, 4));
                    e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 160, 2));
                });
                player.sendSystemMessage(Component.literal("§6§l[Pháp Khí] Tru Tiên Trận Đồ xuất thế, tru sát vạn giới!"));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§7Đẳng cấp: " + artifactType.getColor() + artifactType.getTier()));
        tooltipComponents.add(Component.literal("§7Tiêu hao: §b" + (int)artifactType.getQiCost() + " Linh khí"));
        tooltipComponents.add(Component.literal("§7Hồi chiêu: §e" + (artifactType.getCooldownTicks() / 20) + " giây"));
        tooltipComponents.add(Component.literal("§7Hiệu ứng: §f" + artifactType.getEffectDescription()));
        tooltipComponents.add(Component.literal("§aNhấn chuột phải để kích hoạt pháp bảo"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
