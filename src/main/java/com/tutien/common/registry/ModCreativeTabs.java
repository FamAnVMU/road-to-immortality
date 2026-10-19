package com.tutien.common.registry;

import com.tutien.TuTienMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TuTienMod.MOD_ID);

    public static final Supplier<CreativeModeTab> TU_TIEN_TAB = CREATIVE_TABS.register("tutien_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.literal("Tu Tiên - Tiên Hiệp RPG"))
                    .icon(() -> new ItemStack(ModItems.THAN_PHAM_LINH_THACH.get()))
                    .displayItems((parameters, output) -> {
                        // Linh thạch
                        output.accept(ModItems.HA_PHAM_LINH_THACH.get());
                        output.accept(ModItems.TRUNG_PHAM_LINH_THACH.get());
                        output.accept(ModItems.THUONG_PHAM_LINH_THACH.get());
                        output.accept(ModItems.THAN_PHAM_LINH_THACH.get());
                        output.accept(ModItems.LINH_THACH_QUY_DOI_ITEM.get());

                        // Blocks
                        output.accept(ModItems.LO_LUYEN_DAN_ITEM.get());
                        output.accept(ModItems.LO_LUYEN_KHI_ITEM.get());
                        output.accept(ModItems.TE_DAN_HOI_PHUC_ITEM.get());
                        output.accept(ModItems.TRAN_PHAP_CORE_ITEM.get());
                        output.accept(ModItems.DAT_LINH_DUOC_HA_CAP_ITEM.get());
                        output.accept(ModItems.DAT_LINH_DUOC_TRUNG_CAP_ITEM.get());
                        output.accept(ModItems.DAT_LINH_DUOC_THUONG_CAP_ITEM.get());

                        // Linh dược
                        output.accept(ModItems.LINH_THAO.get());
                        output.accept(ModItems.THANH_TAM_HOA.get());
                        output.accept(ModItems.HUYEN_BANG_THAO.get());
                        output.accept(ModItems.XICH_HOA_QUA.get());
                        output.accept(ModItems.NGU_HANH_CHI.get());
                        output.accept(ModItems.U_HON_HOA.get());
                        output.accept(ModItems.THIEN_LINH_QUA.get());
                        output.accept(ModItems.CUU_DIEP_THAO.get());
                        output.accept(ModItems.NGUYEN_THAN_THAO.get());

                        // Đan dược tu vi & tốc độ
                        output.accept(ModItems.TU_VI_DAN_1.get());
                        output.accept(ModItems.TU_VI_DAN_2.get());
                        output.accept(ModItems.TU_VI_DAN_3.get());
                        output.accept(ModItems.TU_VI_DAN_4.get());
                        output.accept(ModItems.TU_VI_DAN_5.get());
                        output.accept(ModItems.TU_VI_DAN_6.get());
                        output.accept(ModItems.TU_VI_DAN_7.get());
                        output.accept(ModItems.TU_VI_DAN_8.get());
                        output.accept(ModItems.TU_VI_DAN_9.get());

                        output.accept(ModItems.TOC_DO_DAN_1.get());
                        output.accept(ModItems.TOC_DO_DAN_2.get());
                        output.accept(ModItems.TOC_DO_DAN_3.get());
                        output.accept(ModItems.TOC_DO_DAN_4.get());
                        output.accept(ModItems.TOC_DO_DAN_5.get());
                        output.accept(ModItems.TOC_DO_DAN_6.get());
                        output.accept(ModItems.TOC_DO_DAN_7.get());
                        output.accept(ModItems.TOC_DO_DAN_8.get());
                        output.accept(ModItems.TOC_DO_DAN_9.get());

                        // Đột phá
                        output.accept(ModItems.TRUC_CO_DAN.get());
                        output.accept(ModItems.KIM_DAN_DAN.get());
                        output.accept(ModItems.NGUYEN_ANH_DAN.get());
                        output.accept(ModItems.HOA_THAN_DAN.get());
                        output.accept(ModItems.DONG_HU_DAN.get());
                        output.accept(ModItems.DO_KIEP_DAN.get());
                        output.accept(ModItems.VU_HOA_DAN.get());
                        output.accept(ModItems.DANG_TIEN_DAN.get());

                        // Hồi phục & Buff
                        output.accept(ModItems.HOI_HUYET_DAN_1.get());
                        output.accept(ModItems.HOI_HUYET_DAN_2.get());
                        output.accept(ModItems.HOI_HUYET_DAN_3.get());
                        output.accept(ModItems.HOI_KHI_DAN_1.get());
                        output.accept(ModItems.HOI_KHI_DAN_2.get());
                        output.accept(ModItems.HOI_KHI_DAN_3.get());
                        output.accept(ModItems.CUONG_LUC_DAN_1.get());
                        output.accept(ModItems.KIM_CANG_DAN_1.get());

                        // Đan phương & Bản vẽ
                        output.accept(ModItems.DAN_PHUONG_HOI_HUYET.get());
                        output.accept(ModItems.DAN_PHUONG_HOI_KHI.get());
                        output.accept(ModItems.DAN_PHUONG_CUONG_LUC.get());
                        output.accept(ModItems.DAN_PHUONG_TRUC_CO.get());
                        output.accept(ModItems.DAN_PHUONG_KIM_DAN.get());
                        output.accept(ModItems.DAN_PHUONG_NGUYEN_ANH.get());

                        output.accept(ModItems.BAN_VE_KIEM.get());
                        output.accept(ModItems.BAN_VE_DAO_BAO.get());
                        output.accept(ModItems.BAN_VE_PHAP_KHI.get());

                        // Sách công pháp
                        output.accept(ModItems.SACH_THANH_PHONG_KIEM.get());
                        output.accept(ModItems.SACH_LANG_BA_VI_BO.get());
                        output.accept(ModItems.SACH_THIEN_LOI_QUYET.get());
                        output.accept(ModItems.SACH_VAN_KIEM_QUY_TONG.get());
                        output.accept(ModItems.SACH_LIET_HOA_CHUONG.get());
                        output.accept(ModItems.SACH_HU_KHONG_BO.get());
                        output.accept(ModItems.SACH_HUYEN_BANG_KIEM.get());
                        output.accept(ModItems.SACH_THAI_HU_THAN_LOI.get());

                        // Pháp bảo
                        output.accept(ModItems.THANH_PHONG_KIEM.get());
                        output.accept(ModItems.HUYEN_THIET_KIEM.get());
                        output.accept(ModItems.XICH_DIEM_KIEM.get());
                        output.accept(ModItems.THAI_HU_KIEM.get());

                        output.accept(ModItems.HOANG_DAO_BAO_MU.get());
                        output.accept(ModItems.HOANG_DAO_BAO_GIAP.get());
                        output.accept(ModItems.HOANG_DAO_BAO_QUAN.get());
                        output.accept(ModItems.HOANG_DAO_BAO_GIAY.get());
                        output.accept(ModItems.HUYEN_DAO_BAO_GIAP.get());
                        output.accept(ModItems.DIA_DAO_BAO_GIAP.get());
                        output.accept(ModItems.THIEN_DAO_BAO_GIAP.get());

                        output.accept(ModItems.HO_TAM_KINH.get());
                        output.accept(ModItems.CHIEU_YEU_KINH.get());
                        output.accept(ModItems.PHIEN_THIEN_AN.get());
                        output.accept(ModItems.CHU_TIEN_TRAN_DO.get());
                    })
                    .build());
}
