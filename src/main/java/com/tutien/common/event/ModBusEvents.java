package com.tutien.common.event;

import com.tutien.TuTienMod;
import com.tutien.common.npc.TuTienNpcEntity;
import com.tutien.common.registry.ModEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = TuTienMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        var attrs = TuTienNpcEntity.createAttributes().build();
        event.put(ModEntities.TUTIEN_NPC.get(), attrs);
        event.put(ModEntities.NPC_DAN_DUOC.get(), attrs);
        event.put(ModEntities.NPC_DAN_PHUONG.get(), attrs);
        event.put(ModEntities.NPC_PHAP_BAO.get(), attrs);
        event.put(ModEntities.NPC_BAN_VE.get(), attrs);
        event.put(ModEntities.NPC_CONG_PHAP.get(), attrs);
        event.put(ModEntities.NPC_NHIEM_VU.get(), attrs);
        event.put(ModEntities.NPC_THUONG_VAN.get(), attrs);
        event.put(ModEntities.NPC_CON_LUAN.get(), attrs);
    }
}
