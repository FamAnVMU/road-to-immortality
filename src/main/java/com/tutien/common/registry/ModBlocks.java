package com.tutien.common.registry;

import com.tutien.TuTienMod;
import com.tutien.common.alchemy.SpiritHerbCropBlock;
import com.tutien.common.alchemy.SpiritSoilBlock;
import com.tutien.common.altar.RecoveryAltarBlock;
import com.tutien.common.array.CultivationArrayBlock;
import com.tutien.common.alchemy.AlchemyCauldronBlock;
import com.tutien.common.refining.RefiningForgeBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, TuTienMod.MOD_ID);

    // Functional Crafting & Cultivation Blocks
    public static final Supplier<Block> LO_LUYEN_DAN = BLOCKS.register("lo_luyen_dan", AlchemyCauldronBlock::new);
    public static final Supplier<Block> LO_LUYEN_KHI = BLOCKS.register("lo_luyen_khi", RefiningForgeBlock::new);
    public static final Supplier<Block> TE_DAN_HOI_PHUC = BLOCKS.register("te_dan_hoi_phuc", RecoveryAltarBlock::new);
    public static final Supplier<Block> TRAN_PHAP_CORE = BLOCKS.register("tran_phap_core", CultivationArrayBlock::new);

    // Spirit Soils
    public static final Supplier<Block> DAT_LINH_DUOC_HA_CAP = BLOCKS.register("dat_linh_duoc_ha_cap",
            () -> new SpiritSoilBlock(1, "Hạ Cấp"));
    public static final Supplier<Block> DAT_LINH_DUOC_TRUNG_CAP = BLOCKS.register("dat_linh_duoc_trung_cap",
            () -> new SpiritSoilBlock(2, "Trung Cấp"));
    public static final Supplier<Block> DAT_LINH_DUOC_THUONG_CAP = BLOCKS.register("dat_linh_duoc_thuong_cap",
            () -> new SpiritSoilBlock(3, "Thượng Cấp"));

    // Spirit Herb Crops (9 Tiers)
    public static final Supplier<Block> CROP_LINH_THAO = BLOCKS.register("crop_linh_thao",
            () -> new SpiritHerbCropBlock(ModItems.LINH_THAO, 1));
    public static final Supplier<Block> CROP_THANH_TAM_HOA = BLOCKS.register("crop_thanh_tam_hoa",
            () -> new SpiritHerbCropBlock(ModItems.THANH_TAM_HOA, 1));
    public static final Supplier<Block> CROP_HUYEN_BANG_THAO = BLOCKS.register("crop_huyen_bang_thao",
            () -> new SpiritHerbCropBlock(ModItems.HUYEN_BANG_THAO, 2));
    public static final Supplier<Block> CROP_XICH_HOA_QUA = BLOCKS.register("crop_xich_hoa_qua",
            () -> new SpiritHerbCropBlock(ModItems.XICH_HOA_QUA, 2));
    public static final Supplier<Block> CROP_NGU_HANH_CHI = BLOCKS.register("crop_ngu_hanh_chi",
            () -> new SpiritHerbCropBlock(ModItems.NGU_HANH_CHI, 2));
    public static final Supplier<Block> CROP_U_HON_HOA = BLOCKS.register("crop_u_hon_hoa",
            () -> new SpiritHerbCropBlock(ModItems.U_HON_HOA, 3));
    public static final Supplier<Block> CROP_THIEN_LINH_QUA = BLOCKS.register("crop_thien_linh_qua",
            () -> new SpiritHerbCropBlock(ModItems.THIEN_LINH_QUA, 3));
    public static final Supplier<Block> CROP_CUU_DIEP_THAO = BLOCKS.register("crop_cuu_diep_thao",
            () -> new SpiritHerbCropBlock(ModItems.CUU_DIEP_THAO, 3));
    public static final Supplier<Block> CROP_NGUYEN_THAN_THAO = BLOCKS.register("crop_nguyen_than_thao",
            () -> new SpiritHerbCropBlock(ModItems.NGUYEN_THAN_THAO, 3));
}
