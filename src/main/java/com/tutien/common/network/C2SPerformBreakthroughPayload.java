package com.tutien.common.network;

import com.tutien.TuTienMod;
import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.cultivation.Realm;
import com.tutien.common.registry.ModDataAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SPerformBreakthroughPayload() implements CustomPacketPayload {
    public static final Type<C2SPerformBreakthroughPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TuTienMod.MOD_ID, "perform_breakthrough"));

    public static final StreamCodec<ByteBuf, C2SPerformBreakthroughPayload> STREAM_CODEC =
            StreamCodec.unit(new C2SPerformBreakthroughPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SPerformBreakthroughPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                PlayerCultivationData data = player.getData(ModDataAttachments.CULTIVATION_DATA);
                if (data == null) return;

                if (!data.canBreakthrough()) {
                    return;
                }

                boolean hasPill = false;
                if (data.getLayer() == 9) {
                    Realm current = data.getRealm();
                    String requiredPillId = current.getNextBreakthroughPillId();
                    if (!requiredPillId.isEmpty()) {
                        ResourceLocation pillLoc = ResourceLocation.parse(requiredPillId);
                        Item pillItem = BuiltInRegistries.ITEM.get(pillLoc);

                        for (int i = 0; i < player.getInventory().items.size(); i++) {
                            ItemStack stack = player.getInventory().items.get(i);
                            if (stack.is(pillItem)) {
                                stack.shrink(1);
                                hasPill = true;
                                break;
                            }
                        }
                    }
                }

                boolean success = data.performBreakthrough(player, hasPill);
                if (success) {
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
                    if (player.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1.0, player.getZ(), 3, 0, 0, 0, 0);
                        serverLevel.sendParticles(ParticleTypes.FIREWORK, player.getX(), player.getY() + 1.0, player.getZ(), 50, 0.5, 1.0, 0.5, 0.1);
                    }
                }

                ModPayloads.syncToPlayer(player);
            }
        });
    }
}
