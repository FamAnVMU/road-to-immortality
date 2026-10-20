package com.tutien.common.structure;

import com.tutien.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

public class StructureLootGenerator {

    public static void generateStructureLoot(ServerLevel level, String structureName, BlockPos origin) {
        RandomSource random = level.getRandom();
        switch (structureName.toLowerCase()) {
            case "thitran", "thi_tran" -> generateTownLoot(level, origin, random);
            case "thuongvan", "thuong_van" -> generateCangyunLoot(level, origin, random);
            case "conluan", "con_luan" -> generateKunlunLoot(level, origin, random);
        }
    }

    private static void generateTownLoot(ServerLevel level, BlockPos origin, RandomSource random) {
        // Town marketplace booths and storage chests
        placeLootChest(level, origin.offset(35, 2, 63), random, LootTier.TOWN);
        placeLootChest(level, origin.offset(49, 2, 63), random, LootTier.TOWN);
        placeLootChest(level, origin.offset(35, 2, 71), random, LootTier.TOWN);
        placeLootChest(level, origin.offset(49, 2, 71), random, LootTier.TOWN);
        placeLootChest(level, origin.offset(42, 2, 60), random, LootTier.TOWN);
        placeLootChest(level, origin.offset(42, 2, 75), random, LootTier.TOWN);
    }

    private static void generateCangyunLoot(ServerLevel level, BlockPos origin, RandomSource random) {
        // Cangyun sect pavilions, alchemy lab, disciple quarters, treasury
        placeLootChest(level, origin.offset(74, 11, 56), random, LootTier.SECT);
        placeLootChest(level, origin.offset(88, 11, 56), random, LootTier.SECT);
        placeLootChest(level, origin.offset(75, 11, 63), random, LootTier.SECT);
        placeLootChest(level, origin.offset(87, 11, 63), random, LootTier.SECT);
        placeLootChest(level, origin.offset(81, 11, 66), random, LootTier.SECT);
        placeLootChest(level, origin.offset(54, 65, 55), random, LootTier.SECT);
        placeLootChest(level, origin.offset(108, 65, 55), random, LootTier.SECT);
        placeLootChest(level, origin.offset(81, 20, 56), random, LootTier.SECT);
    }

    private static void generateKunlunLoot(ServerLevel level, BlockPos origin, RandomSource random) {
        // Kunlun Sacred Ground celestial vaults, grand alchemy hall, high sanctuary
        placeLootChest(level, origin.offset(28, 47, 85), random, LootTier.SACRED);
        placeLootChest(level, origin.offset(30, 47, 85), random, LootTier.SACRED);
        placeLootChest(level, origin.offset(22, 47, 90), random, LootTier.SACRED);
        placeLootChest(level, origin.offset(36, 47, 90), random, LootTier.SACRED);
        placeLootChest(level, origin.offset(29, 47, 95), random, LootTier.SACRED);
        placeLootChest(level, origin.offset(10, 3, 33), random, LootTier.SACRED);
        placeLootChest(level, origin.offset(10, 3, 35), random, LootTier.SACRED);
        placeLootChest(level, origin.offset(65, 5, 32), random, LootTier.SACRED);
        placeLootChest(level, origin.offset(75, 29, 107), random, LootTier.SACRED);
    }

    private static void placeLootChest(ServerLevel level, BlockPos pos, RandomSource random, LootTier tier) {
        level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof Container container) {
            fillChest(container, random, tier);
        }
    }

    private static void fillChest(Container container, RandomSource random, LootTier tier) {
        int itemsToPlace = 4 + random.nextInt(5); // 4 - 8 item stacks
        for (int i = 0; i < itemsToPlace; i++) {
            int slot = random.nextInt(container.getContainerSize());
            if (!container.getItem(slot).isEmpty()) continue;

            ItemStack stack = getRandomLootItem(random, tier);
            if (!stack.isEmpty()) {
                container.setItem(slot, stack);
            }
        }
    }

    public static ItemStack getRandomLootItem(RandomSource random, LootTier tier) {
        int roll = random.nextInt(100);
        return switch (tier) {
            case TOWN -> {
                if (roll < 35) {
                    yield new ItemStack(ModItems.HA_PHAM_LINH_THACH.get(), 8 + random.nextInt(25));
                } else if (roll < 55) {
                    yield new ItemStack(random.nextBoolean() ? ModItems.HOI_HUYET_DAN_1.get() : ModItems.HOI_KHI_DAN_1.get(), 1 + random.nextInt(3));
                } else if (roll < 70) {
                    yield new ItemStack(ModItems.TU_VI_DAN_1.get(), 1 + random.nextInt(2));
                } else if (roll < 85) {
                    yield new ItemStack(random.nextBoolean() ? ModItems.LINH_THAO.get() : ModItems.THANH_TAM_HOA.get(), 2 + random.nextInt(4));
                } else if (roll < 95) {
                    yield new ItemStack(random.nextBoolean() ? ModItems.BAN_VE_KIEM.get() : ModItems.DAN_PHUONG_HOI_HUYET.get(), 1);
                } else {
                    yield new ItemStack(ModItems.SACH_THANH_PHONG_KIEM.get(), 1);
                }
            }
            case SECT -> {
                if (roll < 30) {
                    yield new ItemStack(ModItems.TRUNG_PHAM_LINH_THACH.get(), 1 + random.nextInt(4));
                } else if (roll < 50) {
                    yield new ItemStack(ModItems.HA_PHAM_LINH_THACH.get(), 32 + random.nextInt(33));
                } else if (roll < 65) {
                    yield new ItemStack(random.nextBoolean() ? ModItems.HOI_HUYET_DAN_2.get() : ModItems.HOI_KHI_DAN_2.get(), 1 + random.nextInt(3));
                } else if (roll < 75) {
                    yield new ItemStack(ModItems.TRUC_CO_DAN.get(), 1);
                } else if (roll < 85) {
                    yield new ItemStack(random.nextBoolean() ? ModItems.XICH_HOA_QUA.get() : ModItems.HUYEN_BANG_THAO.get(), 2 + random.nextInt(4));
                } else if (roll < 95) {
                    yield new ItemStack(random.nextBoolean() ? ModItems.BAN_VE_DAO_BAO.get() : ModItems.DAN_PHUONG_TRUC_CO.get(), 1);
                } else {
                    yield new ItemStack(random.nextBoolean() ? ModItems.SACH_LANG_BA_VI_BO.get() : ModItems.HO_TAM_KINH.get(), 1);
                }
            }
            case SACRED -> {
                if (roll < 25) {
                    yield new ItemStack(ModItems.THUONG_PHAM_LINH_THACH.get(), 1 + random.nextInt(3));
                } else if (roll < 45) {
                    yield new ItemStack(ModItems.TRUNG_PHAM_LINH_THACH.get(), 8 + random.nextInt(17));
                } else if (roll < 60) {
                    yield new ItemStack(random.nextBoolean() ? ModItems.TU_VI_DAN_3.get() : ModItems.TU_VI_DAN_4.get(), 1 + random.nextInt(2));
                } else if (roll < 75) {
                    int p = random.nextInt(3);
                    yield new ItemStack(p == 0 ? ModItems.KIM_DAN_DAN.get() : (p == 1 ? ModItems.HOA_THAN_DAN.get() : ModItems.DO_KIEP_DAN.get()), 1);
                } else if (roll < 85) {
                    yield new ItemStack(random.nextBoolean() ? ModItems.THIEN_LINH_QUA.get() : ModItems.NGUYEN_THAN_THAO.get(), 1 + random.nextInt(3));
                } else if (roll < 93) {
                    yield new ItemStack(random.nextBoolean() ? ModItems.CHIEU_YEU_KINH.get() : ModItems.BAN_VE_PHAP_KHI.get(), 1);
                } else {
                    int a = random.nextInt(3);
                    yield new ItemStack(a == 0 ? ModItems.PHIEN_THIEN_AN.get() : (a == 1 ? ModItems.CHU_TIEN_TRAN_DO.get() : ModItems.SACH_VAN_KIEM_QUY_TONG.get()), 1);
                }
            }
        };
    }

    public enum LootTier {
        TOWN,
        SECT,
        SACRED
    }
}
