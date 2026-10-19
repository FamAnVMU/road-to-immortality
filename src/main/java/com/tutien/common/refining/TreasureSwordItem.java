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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TreasureSwordItem extends SwordItem {
    public enum ArtifactTier {
        HOANG("Hoàng Cấp", "§e", 25.0f, 60, 15.0f),
        HUYEN("Huyền Cấp", "§9", 50.0f, 100, 30.0f),
        DIA("Địa Cấp", "§5", 100.0f, 160, 60.0f),
        THIEN("Thiên Cấp", "§6§l", 200.0f, 240, 120.0f);

        private final String displayName;
        private final String color;
        private final float qiCost;
        private final int cooldownTicks;
        private final float skillDamage;

        ArtifactTier(String displayName, String color, float qiCost, int cooldownTicks, float skillDamage) {
            this.displayName = displayName;
            this.color = color;
            this.qiCost = qiCost;
            this.cooldownTicks = cooldownTicks;
            this.skillDamage = skillDamage;
        }

        public String getColoredName() { return color + displayName; }
        public float getQiCost() { return qiCost; }
        public int getCooldownTicks() { return cooldownTicks; }
        public float getSkillDamage() { return skillDamage; }
    }

    private final ArtifactTier tier;
    private final String skillName;

    public TreasureSwordItem(TuTienItemTiers material, ArtifactTier tier, String skillName, Properties properties) {
        super(material, properties);
        this.tier = tier;
        this.skillName = skillName;
    }

    public ArtifactTier getArtifactTier() {
        return tier;
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

            if (data.getLingQi() < tier.getQiCost()) {
                serverPlayer.sendSystemMessage(Component.literal("§c[Tu Tiên] Linh khí không đủ để kích hoạt kỹ năng pháp bảo! (Cần " + (int)tier.getQiCost() + " Qi)"));
                return InteractionResultHolder.fail(stack);
            }

            data.consumeLingQi(tier.getQiCost());
            player.getCooldowns().addCooldown(this, tier.getCooldownTicks());

            executeSwordSkill((ServerLevel) level, serverPlayer);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private void executeSwordSkill(ServerLevel level, ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        Vec3 eyePos = player.getEyePosition();

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.2f, 1.2f);

        switch (tier) {
            case HOANG -> {
                // Thanh Phong Kiếm: Kiếm Khí trảm thẳng 10 khối
                for (int i = 1; i <= 10; i++) {
                    Vec3 pos = eyePos.add(look.scale(i));
                    level.sendParticles(ParticleTypes.SWEEP_ATTACK, pos.x, pos.y, pos.z, 2, 0.2, 0.2, 0.2, 0.0);
                }
                AABB box = player.getBoundingBox().expandTowards(look.scale(10)).inflate(1.5);
                level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive()).forEach(e -> {
                    e.hurt(player.damageSources().playerAttack(player), tier.getSkillDamage());
                    e.knockback(0.8, -look.x, -look.z);
                });
                player.sendSystemMessage(Component.literal("§e[Pháp Bảo] Thanh Phong Kiếm Khí trảm phá!"));
            }
            case HUYEN -> {
                // Huyền Thiết Kiếm: Huyền Thiết Kiếm Khí gây choáng và đẩy lùi
                for (int i = 1; i <= 14; i++) {
                    Vec3 pos = eyePos.add(look.scale(i));
                    level.sendParticles(ParticleTypes.CRIT, pos.x, pos.y, pos.z, 5, 0.3, 0.3, 0.3, 0.1);
                }
                AABB box = player.getBoundingBox().expandTowards(look.scale(14)).inflate(2.0);
                level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive()).forEach(e -> {
                    e.hurt(player.damageSources().playerAttack(player), tier.getSkillDamage());
                    e.knockback(1.5, -look.x, -look.z);
                });
                player.sendSystemMessage(Component.literal("§9[Pháp Bảo] Huyền Thiết Trảm Kích!"));
            }
            case DIA -> {
                // Xích Diệm Kiếm: Hỏa Diễm Kiếm Trảm thiêu đốt diện rộng
                for (int i = 1; i <= 16; i++) {
                    Vec3 pos = eyePos.add(look.scale(i));
                    level.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 8, 0.5, 0.5, 0.5, 0.05);
                }
                AABB box = player.getBoundingBox().expandTowards(look.scale(16)).inflate(3.0);
                level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive()).forEach(e -> {
                    e.hurt(player.damageSources().playerAttack(player), tier.getSkillDamage());
                    e.setRemainingFireTicks(120); // 6s fire
                });
                player.sendSystemMessage(Component.literal("§5[Pháp Bảo] Xích Diệm Liệt Hỏa Kiếm!"));
            }
            case THIEN -> {
                // Thái Hư Kiếm: Vạn Cổ Quy Tông - Thiên Tru sát thương cực đại quanh người chơi
                AABB box = player.getBoundingBox().inflate(15.0);
                level.sendParticles(ParticleTypes.DRAGON_BREATH, player.getX(), player.getY() + 1.0, player.getZ(), 100, 5.0, 1.0, 5.0, 0.1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 2.0f, 1.0f);
                level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive()).forEach(e -> {
                    e.hurt(player.damageSources().playerAttack(player), tier.getSkillDamage());
                    level.sendParticles(ParticleTypes.EXPLOSION, e.getX(), e.getY(), e.getZ(), 1, 0, 0, 0, 0);
                });
                player.sendSystemMessage(Component.literal("§6§l[Pháp Bảo] Thái Hư Kiếm Quyết - Vạn Kiếm Quy Tông!"));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§7Đẳng cấp: " + tier.getColoredName()));
        tooltipComponents.add(Component.literal("§7Kỹ năng pháp bảo: §f" + skillName));
        tooltipComponents.add(Component.literal("§7Tiêu hao: §b" + (int)tier.getQiCost() + " Linh khí"));
        tooltipComponents.add(Component.literal("§7Hồi chiêu: §e" + (tier.getCooldownTicks() / 20) + " giây"));
        tooltipComponents.add(Component.literal("§7Sát thương kỹ năng: §c" + (int)tier.getSkillDamage()));
        tooltipComponents.add(Component.literal("§aNhấn chuột phải để phóng thích kiếm khí"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
