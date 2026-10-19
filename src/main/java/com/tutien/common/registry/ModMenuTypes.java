package com.tutien.common.registry;

import com.tutien.TuTienMod;
import com.tutien.common.alchemy.AlchemyCauldronMenu;
import com.tutien.common.cultivation.CultivationMenu;
import com.tutien.common.currency.SpiritStoneConversionMenu;
import com.tutien.common.npc.NpcTradeMenu;
import com.tutien.common.refining.RefiningForgeMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, TuTienMod.MOD_ID);

    public static final Supplier<MenuType<AlchemyCauldronMenu>> ALCHEMY_CAULDRON_MENU =
            MENU_TYPES.register("alchemy_cauldron_menu", () -> new MenuType<>(AlchemyCauldronMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));

    public static final Supplier<MenuType<RefiningForgeMenu>> REFINING_FORGE_MENU =
            MENU_TYPES.register("refining_forge_menu", () -> new MenuType<>(RefiningForgeMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));

    public static final Supplier<MenuType<SpiritStoneConversionMenu>> SPIRIT_STONE_CONVERSION_MENU =
            MENU_TYPES.register("spirit_stone_conversion_menu", () -> new MenuType<>(SpiritStoneConversionMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));

    public static final Supplier<MenuType<NpcTradeMenu>> NPC_TRADE_MENU =
            MENU_TYPES.register("npc_trade_menu", () -> IMenuTypeExtension.create(NpcTradeMenu::new));

    public static final Supplier<MenuType<CultivationMenu>> CULTIVATION_MENU =
            MENU_TYPES.register("cultivation_menu", () -> new MenuType<>(CultivationMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
}
