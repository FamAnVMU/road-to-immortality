package com.tutien.common.network;

import com.tutien.TuTienMod;
import com.tutien.client.hud.ClientCultivationCache;
import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.registry.ModDataAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CSyncCultivationPayload(CompoundTag dataTag) implements CustomPacketPayload {
    public static final Type<S2CSyncCultivationPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TuTienMod.MOD_ID, "sync_cultivation"));

    public static final StreamCodec<ByteBuf, S2CSyncCultivationPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.COMPOUND_TAG,
                    S2CSyncCultivationPayload::dataTag,
                    S2CSyncCultivationPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(S2CSyncCultivationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                PlayerCultivationData data = player.getData(ModDataAttachments.CULTIVATION_DATA);
                if (data != null) {
                    data.deserializeNBT(player.level().registryAccess(), payload.dataTag());
                }
                ClientCultivationCache.updateFromTag(payload.dataTag());
            }
        });
    }
}
