package com.tutien.common.network;

import com.tutien.TuTienMod;
import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.registry.ModDataAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SEquipSkillPayload(int slotIndex, String skillId) implements CustomPacketPayload {
    public static final Type<C2SEquipSkillPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TuTienMod.MOD_ID, "equip_skill"));

    public static final StreamCodec<ByteBuf, C2SEquipSkillPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    C2SEquipSkillPayload::slotIndex,
                    ByteBufCodecs.STRING_UTF8,
                    C2SEquipSkillPayload::skillId,
                    C2SEquipSkillPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SEquipSkillPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                PlayerCultivationData data = player.getData(ModDataAttachments.CULTIVATION_DATA);
                if (data == null) return;

                int slot = payload.slotIndex();
                String skillId = payload.skillId();

                if (slot < 0 || slot >= 4) return;

                if (!skillId.isEmpty() && !data.hasLearnedSkill(skillId)) {
                    player.sendSystemMessage(Component.literal("§c[Công Pháp] Bạn chưa học công pháp này!"));
                    return;
                }

                data.setEquippedSkill(slot, skillId);
                player.sendSystemMessage(Component.literal("§a[Công Pháp] Đã trang bị công pháp vào ô kỹ năng số " + (slot + 1) + "!"));

                ModPayloads.syncToPlayer(player);
            }
        });
    }
}
