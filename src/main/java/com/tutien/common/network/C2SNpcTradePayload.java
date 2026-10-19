package com.tutien.common.network;

import com.tutien.TuTienMod;
import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.currency.SpiritStoneHelper;
import com.tutien.common.npc.NpcTradeOffer;
import com.tutien.common.npc.TuTienNpcEntity;
import com.tutien.common.registry.ModDataAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record C2SNpcTradePayload(int npcEntityId, int offerIndex) implements CustomPacketPayload {
    public static final Type<C2SNpcTradePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TuTienMod.MOD_ID, "npc_trade"));

    public static final StreamCodec<ByteBuf, C2SNpcTradePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    C2SNpcTradePayload::npcEntityId,
                    ByteBufCodecs.VAR_INT,
                    C2SNpcTradePayload::offerIndex,
                    C2SNpcTradePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SNpcTradePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                Entity entity = player.level().getEntity(payload.npcEntityId());
                if (!(entity instanceof TuTienNpcEntity npc) || npc.distanceToSqr(player) > 64.0) {
                    player.sendSystemMessage(Component.literal("§c[Giao Dịch] Bạn ở quá xa NPC!"));
                    return;
                }

                List<NpcTradeOffer> offers = npc.getNpcRole().getOffers();
                if (payload.offerIndex() < 0 || payload.offerIndex() >= offers.size()) {
                    return;
                }

                NpcTradeOffer offer = offers.get(payload.offerIndex());
                PlayerCultivationData data = player.getData(ModDataAttachments.CULTIVATION_DATA);
                if (data == null) return;

                // 1. Check Realm Requirement
                if (data.getRealmId() < offer.getMinRealm()) {
                    player.sendSystemMessage(Component.literal("§c[Giao Dịch] Cảnh giới của bạn chưa đủ để mua vật phẩm này!"));
                    return;
                }

                // 2. Check Currency Requirements
                if (offer.getSpiritStoneCost() > 0 && !SpiritStoneHelper.hasEnough(player, offer.getSpiritStoneCost())) {
                    player.sendSystemMessage(Component.literal("§c[Giao Dịch] Không đủ linh thạch! Cần §e" + offer.getSpiritStoneCost() + " Hạ Phẩm Linh Thạch."));
                    return;
                }

                if (offer.getCongHuanCost() > 0 && data.getCongHuan() < offer.getCongHuanCost()) {
                    player.sendSystemMessage(Component.literal("§c[Giao Dịch] Không đủ điểm Công Huân! Cần §e" + offer.getCongHuanCost() + " điểm."));
                    return;
                }

                if (offer.getCongHienCost() > 0 && data.getCongHien() < offer.getCongHienCost()) {
                    player.sendSystemMessage(Component.literal("§c[Giao Dịch] Không đủ điểm Cống Hiến Tông Môn! Cần §e" + offer.getCongHienCost() + " điểm."));
                    return;
                }

                // 3. Deduct Currency Atomically
                if (offer.getSpiritStoneCost() > 0) {
                    SpiritStoneHelper.deductValue(player, offer.getSpiritStoneCost());
                }
                if (offer.getCongHuanCost() > 0) {
                    data.consumeCongHuan(offer.getCongHuanCost());
                }
                if (offer.getCongHienCost() > 0) {
                    data.consumeCongHien(offer.getCongHienCost());
                }

                // 4. Give Purchased Item
                ItemStack purchased = offer.getOutput();
                if (!player.getInventory().add(purchased)) {
                    player.drop(purchased, false);
                }

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.0f);
                player.sendSystemMessage(Component.literal("§a[Giao Dịch] Giao dịch thành công với " + npc.getNpcRole().getTitle() + "!"));

                ModPayloads.syncToPlayer(player);
            }
        });
    }
}
