package com.tutien.common.network;

import com.tutien.TuTienMod;
import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.registry.ModDataAttachments;
import com.tutien.common.skill.MasteryLevel;
import com.tutien.common.skill.SkillInstance;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SCastSkillPayload(int slotIndex) implements CustomPacketPayload {
    public static final Type<C2SCastSkillPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TuTienMod.MOD_ID, "cast_skill"));

    public static final StreamCodec<ByteBuf, C2SCastSkillPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    C2SCastSkillPayload::slotIndex,
                    C2SCastSkillPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SCastSkillPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                PlayerCultivationData data = player.getData(ModDataAttachments.CULTIVATION_DATA);
                if (data == null || !player.isAlive()) return;

                int slot = payload.slotIndex();
                if (slot < 0 || slot >= 4) return;

                String skillId = data.getEquippedSkills()[slot];
                if (skillId == null || skillId.isEmpty()) {
                    player.sendSystemMessage(Component.translatable("tutien.message.skill_slot_unequipped", (slot + 1)));
                    return;
                }

                if (data.getCooldown(skillId) > 0) {
                    player.sendSystemMessage(Component.translatable("tutien.message.skill_on_cooldown"));
                    return;
                }

                SkillInstance instance = data.getLearnedSkills().get(skillId);
                MasteryLevel mastery = instance != null ? instance.getMastery() : MasteryLevel.TIEU_THANH;

                float baseQiCost = getBaseQiCost(skillId);
                float qiCost = baseQiCost * mastery.getQiCostMultiplier();

                if (data.getLingQi() < qiCost) {
                    player.sendSystemMessage(Component.translatable("tutien.message.qi_insufficient", (int)qiCost));
                    return;
                }

                // Consume Qi & Set Cooldown
                data.consumeLingQi(qiCost);
                int cdTicks = getCooldownTicks(skillId);
                data.setCooldown(skillId, cdTicks);

                // Add proficiency exp
                if (instance != null) {
                    instance.addExp(15);
                }

                // Execute Skill Effects
                executeSkill(skillId, (ServerLevel) player.level(), player, mastery);

                ModPayloads.syncToPlayer(player);
            }
        });
    }

    private static float getBaseQiCost(String skillId) {
        return switch (skillId) {
            case "thanh_phong_kiem" -> 20.0f;
            case "lang_ba_vi_bo" -> 30.0f;
            case "liet_hoa_chuong" -> 25.0f;
            case "huyen_bang_kiem" -> 70.0f;
            case "hu_khong_bo" -> 60.0f;
            case "thien_loi_quyet" -> 80.0f;
            case "van_kiem_quy_tong" -> 200.0f;
            case "thai_hu_than_loi" -> 250.0f;
            default -> 20.0f;
        };
    }

    private static int getCooldownTicks(String skillId) {
        return switch (skillId) {
            case "thanh_phong_kiem" -> 100; // 5s
            case "lang_ba_vi_bo" -> 80;   // 4s
            case "liet_hoa_chuong" -> 120; // 6s
            case "hu_khong_bo" -> 160;     // 8s
            case "thien_loi_quyet" -> 240; // 12s
            case "huyen_bang_kiem" -> 300; // 15s
            case "van_kiem_quy_tong" -> 600; // 30s
            case "thai_hu_than_loi" -> 800; // 40s
            default -> 100;
        };
    }

    private static void executeSkill(String skillId, ServerLevel level, ServerPlayer player, MasteryLevel mastery) {
        Vec3 look = player.getLookAngle();
        Vec3 eyePos = player.getEyePosition();
        float dmgMult = mastery.getDamageMultiplier();

        switch (skillId) {
            case "thanh_phong_kiem" -> {
                // Thanh Phong Kiếm: Kiếm Khí trảm thẳng 10 khối
                for (int i = 1; i <= 10; i++) {
                    Vec3 pos = eyePos.add(look.scale(i));
                    level.sendParticles(ParticleTypes.SWEEP_ATTACK, pos.x, pos.y, pos.z, 2, 0.2, 0.2, 0.2, 0.0);
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.2f, 1.2f);
                AABB box = player.getBoundingBox().expandTowards(look.scale(10)).inflate(1.5);
                level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive()).forEach(e -> {
                    e.hurt(player.damageSources().playerAttack(player), 20.0f * dmgMult);
                    e.knockback(0.8, -look.x, -look.z);
                });
                player.sendSystemMessage(Component.translatable("tutien.skill.cast.thanh_phong_kiem"));
            }
            case "lang_ba_vi_bo" -> {
                // Lăng Ba Vi Bộ: Lướt nhanh về phía trước + Tốc độ
                Vec3 dash = look.scale(1.8).add(0, 0.2, 0);
                player.setDeltaMovement(dash);
                player.hurtMarked = true;
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 2));
                level.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 20, 0.3, 0.1, 0.3, 0.1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.5f);
                player.sendSystemMessage(Component.translatable("tutien.skill.cast.lang_ba_vi_bo"));
            }
            case "liet_hoa_chuong" -> {
                // Liệt Hỏa Chưởng: Chưởng lực hỏa diễm
                for (int i = 1; i <= 8; i++) {
                    Vec3 pos = eyePos.add(look.scale(i));
                    level.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 6, 0.3, 0.3, 0.3, 0.02);
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.2f, 1.0f);
                AABB box = player.getBoundingBox().expandTowards(look.scale(8)).inflate(2.0);
                level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive()).forEach(e -> {
                    e.hurt(player.damageSources().playerAttack(player), 28.0f * dmgMult);
                    e.setRemainingFireTicks(100);
                });
                player.sendSystemMessage(Component.translatable("tutien.skill.cast.liet_hoa_chuong"));
            }
            case "hu_khong_bo" -> {
                // Hư Không Bộ: Dịch chuyển tức thời 8 ô
                Vec3 target = player.position().add(look.scale(8.0));
                level.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1.0, player.getZ(), 40, 0.5, 0.5, 0.5, 0.1);
                player.teleportTo(target.x, target.y, target.z);
                level.sendParticles(ParticleTypes.PORTAL, target.x, target.y + 1.0, target.z, 40, 0.5, 0.5, 0.5, 0.1);
                level.playSound(null, target.x, target.y, target.z, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.2f, 1.2f);
                player.sendSystemMessage(Component.translatable("tutien.skill.cast.hu_khong_bo"));
            }
            case "thien_loi_quyet" -> {
                // Thiên Lôi Quyết: Gọi sét đánh mục tiêu trong tầm nhìn
                AABB searchBox = player.getBoundingBox().expandTowards(look.scale(25)).inflate(3.0);
                LivingEntity target = null;
                double closest = Double.MAX_VALUE;
                for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, searchBox, e -> e != player && e.isAlive())) {
                    double dist = e.distanceToSqr(player);
                    if (dist < closest) {
                        closest = dist;
                        target = e;
                    }
                }

                BlockPos strikePos = target != null ? target.blockPosition() : BlockPos.containing(player.position().add(look.scale(10)));
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                if (bolt != null) {
                    bolt.moveTo(Vec3.atBottomCenterOf(strikePos));
                    level.addFreshEntity(bolt);
                }
                if (target != null) {
                    target.hurt(player.damageSources().playerAttack(player), 55.0f * dmgMult);
                }
                player.sendSystemMessage(Component.translatable("tutien.skill.cast.thien_loi_quyet"));
            }
            case "huyen_bang_kiem" -> {
                // Huyền Băng Kiếm Trận: Đóng băng kẻ địch trong 10 khối
                AABB box = player.getBoundingBox().inflate(10.0);
                level.sendParticles(ParticleTypes.SNOWFLAKE, player.getX(), player.getY() + 1.0, player.getZ(), 80, 4.0, 1.0, 4.0, 0.05);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.5f, 0.8f);
                level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive()).forEach(e -> {
                    e.hurt(player.damageSources().playerAttack(player), 40.0f * dmgMult);
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 3));
                    e.setTicksFrozen(200);
                });
                player.sendSystemMessage(Component.translatable("tutien.skill.cast.huyen_bang_kiem"));
            }
            case "van_kiem_quy_tong" -> {
                // Vạn Kiếm Quy Tông: Kiếm trận hủy diệt 15 khối
                AABB box = player.getBoundingBox().inflate(15.0);
                level.sendParticles(ParticleTypes.DRAGON_BREATH, player.getX(), player.getY() + 1.0, player.getZ(), 120, 5.0, 1.0, 5.0, 0.1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 2.0f, 1.0f);
                level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive()).forEach(e -> {
                    e.hurt(player.damageSources().playerAttack(player), 120.0f * dmgMult);
                    level.sendParticles(ParticleTypes.EXPLOSION, e.getX(), e.getY() + 0.5, e.getZ(), 1, 0, 0, 0, 0);
                });
                player.sendSystemMessage(Component.translatable("tutien.skill.cast.van_kiem_quy_tong"));
            }
            case "thai_hu_than_loi" -> {
                // Thái Hư Thần Lôi: Đại thiên kiếp nộ nổ tung
                AABB box = player.getBoundingBox().inflate(20.0);
                level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, player.getX(), player.getY() + 1.0, player.getZ(), 3, 2.0, 1.0, 2.0, 0.0);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 2.5f, 0.7f);
                level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive()).forEach(e -> {
                    e.hurt(player.damageSources().magic(), 180.0f * dmgMult);
                    e.knockback(2.0, player.getX() - e.getX(), player.getZ() - e.getZ());
                });
                player.sendSystemMessage(Component.translatable("tutien.skill.cast.thai_hu_than_loi"));
            }
        }
    }
}
