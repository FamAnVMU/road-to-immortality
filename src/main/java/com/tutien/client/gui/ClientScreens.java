package com.tutien.client.gui;

import com.tutien.TuTienMod;
import com.tutien.common.registry.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = TuTienMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientScreens {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.ALCHEMY_CAULDRON_MENU.get(), AlchemyCauldronScreen::new);
        event.register(ModMenuTypes.REFINING_FORGE_MENU.get(), RefiningForgeScreen::new);
        event.register(ModMenuTypes.SPIRIT_STONE_CONVERSION_MENU.get(), SpiritStoneConversionScreen::new);
        event.register(ModMenuTypes.NPC_TRADE_MENU.get(), NpcTradeScreen::new);
    }
}
