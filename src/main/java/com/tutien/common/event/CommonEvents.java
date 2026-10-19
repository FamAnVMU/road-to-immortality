package com.tutien.common.event;

import com.tutien.TuTienMod;
import com.tutien.common.array.CultivationArrayBlockEntity;
import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.network.ModPayloads;
import com.tutien.common.registry.ModDataAttachments;
import com.tutien.common.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = TuTienMod.MOD_ID)
public class CommonEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerCultivationData data = player.getData(ModDataAttachments.CULTIVATION_DATA);
            if (data == null) return;

            // Check array multiplier
            double arrayMultiplier = CultivationArrayBlockEntity.getMaxArrayMultiplierForPlayer(player.getUUID());

            // Check if player moved while meditating
            if (data.isMeditating()) {
                double dx = player.getX() - player.xOld;
                double dy = player.getY() - player.yOld;
                double dz = player.getZ() - player.zOld;
                boolean hasMoved = (dx * dx + dz * dz > 0.0004) || Math.abs(dy) > 0.08 || player.isSprinting() || player.isFallFlying();

                if (hasMoved) {
                    data.setMeditating(false);
                    player.sendSystemMessage(Component.translatable("tutien.message.meditation_interrupted_movement"));
                    ModPayloads.syncToPlayer(player);
                } else if (player.tickCount % 5 == 0 && player.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 0.3, player.getZ(), 3, 0.3, 0.2, 0.3, 0.05);
                }
            }

            // Tick cultivation data (tu vi, ling qi, buffs, cooldowns)
            data.tick(player, arrayMultiplier);

            // Sync to client every second (20 ticks)
            if (player.tickCount % 20 == 0) {
                ModPayloads.syncToPlayer(player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDamage(LivingDamageEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerCultivationData data = player.getData(ModDataAttachments.CULTIVATION_DATA);
            if (data != null && data.isMeditating()) {
                data.setMeditating(false);
                player.sendSystemMessage(Component.translatable("tutien.message.meditation_interrupted_damage"));
                ModPayloads.syncToPlayer(player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerCultivationData data = player.getData(ModDataAttachments.CULTIVATION_DATA);
            if (data != null) {
                data.syncAttributes(player);
                ModPayloads.syncToPlayer(player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerCultivationData data = player.getData(ModDataAttachments.CULTIVATION_DATA);
            if (data != null) {
                data.setMeditating(false);
                data.setLingQi(data.getMaxLingQi());
                data.syncAttributes(player);
                ModPayloads.syncToPlayer(player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        CultivationArrayBlockEntity.cleanUpPlayer(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        // Normal Monsters drop Hạ Phẩm Linh Thạch
        if (entity instanceof Enemy && !(entity instanceof WitherBoss) && !(entity instanceof EnderDragon)) {
            if (entity.getRandom().nextFloat() < 0.45f) { // 45% drop rate
                int count = 1 + entity.getRandom().nextInt(3);
                ItemStack stones = new ItemStack(ModItems.HA_PHAM_LINH_THACH.get(), count);
                event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), stones));
            }
        }

        // Bosses drop Thượng Phẩm Linh Thạch and rare breakthrough pills
        if (entity instanceof WitherBoss || entity instanceof EnderDragon) {
            int count = 2 + entity.getRandom().nextInt(4);
            ItemStack stones = new ItemStack(ModItems.THUONG_PHAM_LINH_THACH.get(), count);
            event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), stones));

            // Drop rare pill
            ItemStack pill = new ItemStack(ModItems.HOA_THAN_DAN.get(), 1);
            event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), pill));
        }
    }
}
