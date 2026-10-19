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
        event.put(ModEntities.TUTIEN_NPC.get(), TuTienNpcEntity.createAttributes().build());
    }
}
