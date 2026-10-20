package com.tutien.common.loot;

import com.tutien.TuTienMod;
import com.tutien.common.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

@EventBusSubscriber(modid = TuTienMod.MOD_ID)
public class VanillaLootInjector {

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation id = event.getName();
        String path = id.getPath();

        if (id.getNamespace().equals("minecraft") && path.startsWith("chests/")) {
            LootTable table = event.getTable();
            if (table == null) return;

            boolean isHighTier = path.contains("ancient_city") || path.contains("stronghold") ||
                    path.contains("end_city") || path.contains("mansion") || path.contains("nether_bridge");

            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .name("tutien_vanilla_injection")
                    .setRolls(UniformGenerator.between(1.0f, isHighTier ? 3.0f : 2.0f))
                    // Low grade spirit stones (common in all structures)
                    .add(LootItem.lootTableItem(ModItems.HA_PHAM_LINH_THACH.get())
                            .setWeight(30)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0f, 20.0f))))
                    // Medium grade spirit stones
                    .add(LootItem.lootTableItem(ModItems.TRUNG_PHAM_LINH_THACH.get())
                            .setWeight(isHighTier ? 18 : 6)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                    // Cultivation herbs
                    .add(LootItem.lootTableItem(ModItems.LINH_THAO.get())
                            .setWeight(14)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                    .add(LootItem.lootTableItem(ModItems.THANH_TAM_HOA.get())
                            .setWeight(12)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))
                    .add(LootItem.lootTableItem(ModItems.HUYEN_BANG_THAO.get())
                            .setWeight(8)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))
                    // Pills
                    .add(LootItem.lootTableItem(ModItems.HOI_HUYET_DAN_1.get())
                            .setWeight(12)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                    .add(LootItem.lootTableItem(ModItems.HOI_KHI_DAN_1.get())
                            .setWeight(12)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                    .add(LootItem.lootTableItem(ModItems.TU_VI_DAN_1.get())
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))
                    // Basic Skill Scrolls & Recipes (Rare treasure)
                    .add(LootItem.lootTableItem(ModItems.SACH_THANH_PHONG_KIEM.get())
                            .setWeight(3)
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0f))))
                    .add(LootItem.lootTableItem(ModItems.DAN_PHUONG_HOI_HUYET.get())
                            .setWeight(3)
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0f))));

            if (isHighTier) {
                // High tier structures can also have High-grade stones, Foundation pills, and Artifacts
                poolBuilder.add(LootItem.lootTableItem(ModItems.THUONG_PHAM_LINH_THACH.get())
                                .setWeight(4)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0f))))
                        .add(LootItem.lootTableItem(ModItems.TRUC_CO_DAN.get())
                                .setWeight(5)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0f))))
                        .add(LootItem.lootTableItem(ModItems.HO_TAM_KINH.get())
                                .setWeight(2)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0f))))
                        .add(LootItem.lootTableItem(ModItems.SACH_LANG_BA_VI_BO.get())
                                .setWeight(2)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0f))));
            }

            table.addPool(poolBuilder.build());
        }
    }
}
