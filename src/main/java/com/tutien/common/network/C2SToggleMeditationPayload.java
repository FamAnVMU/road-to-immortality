package com.tutien.common.network;

import com.tutien.TuTienMod;
import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.registry.ModDataAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SToggleMeditationPayload() implements CustomPacketPayload {
    public static final Type<C2SToggleMeditationPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TuTienMod.MOD_ID, "toggle_meditation"));

    public static final StreamCodec<ByteBuf, C2SToggleMeditationPayload> STREAM_CODEC =
            StreamCodec.unit(new C2SToggleMeditationPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SToggleMeditationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                PlayerCultivationData data = player.getData(ModDataAttachments.CULTIVATION_DATA);
                if (data == null) return;

                if (!player.isAlive()) return;

                boolean newState = !data.isMeditating();
                data.setMeditating(newState);

                if (newState) {
                    player.sendSystemMessage(Component.translatable("tutien.message.meditation_start"));
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 0.8f);
                    if (player.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 0.5, player.getZ(), 20, 0.5, 0.5, 0.5, 0.05);
                    }
                } else {
                    player.sendSystemMessage(Component.translatable("tutien.message.meditation_stop"));
                }

                // Sync back to client
                ModPayloads.syncToPlayer(player);
            }
        });
    }
}
