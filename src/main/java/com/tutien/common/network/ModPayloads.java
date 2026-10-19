package com.tutien.common.network;

import com.tutien.TuTienMod;
import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.registry.ModDataAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = TuTienMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModPayloads {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(TuTienMod.MOD_ID).versioned("1.0.0");

        registrar.playToServer(C2SToggleMeditationPayload.TYPE, C2SToggleMeditationPayload.STREAM_CODEC, C2SToggleMeditationPayload::handle);
        registrar.playToServer(C2SPerformBreakthroughPayload.TYPE, C2SPerformBreakthroughPayload.STREAM_CODEC, C2SPerformBreakthroughPayload::handle);
        registrar.playToServer(C2SCastSkillPayload.TYPE, C2SCastSkillPayload.STREAM_CODEC, C2SCastSkillPayload::handle);
        registrar.playToServer(C2SSpiritStoneConvertPayload.TYPE, C2SSpiritStoneConvertPayload.STREAM_CODEC, C2SSpiritStoneConvertPayload::handle);
        registrar.playToServer(C2SNpcTradePayload.TYPE, C2SNpcTradePayload.STREAM_CODEC, C2SNpcTradePayload::handle);
        registrar.playToServer(C2SEquipSkillPayload.TYPE, C2SEquipSkillPayload.STREAM_CODEC, C2SEquipSkillPayload::handle);

        registrar.playToClient(S2CSyncCultivationPayload.TYPE, S2CSyncCultivationPayload.STREAM_CODEC, S2CSyncCultivationPayload::handle);
    }

    public static void syncToPlayer(ServerPlayer player) {
        PlayerCultivationData data = player.getData(ModDataAttachments.CULTIVATION_DATA);
        if (data != null) {
            PacketDistributor.sendToPlayer(player, new S2CSyncCultivationPayload(data.serializeNBT(player.level().registryAccess())));
        }
    }
}
