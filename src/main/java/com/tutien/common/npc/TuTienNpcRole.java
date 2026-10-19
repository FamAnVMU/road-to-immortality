package com.tutien.common.npc;

import com.tutien.common.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public enum TuTienNpcRole {
    DAN_DUOC("Chưởng Quầy Đan Dược", "Bán các loại đan dược tu luyện, hồi phục và đột phá."),
    DAN_PHUONG("Đan Sư Tông Sư", "Bán đan phương, hướng dẫn luyện đan và thu mua linh dược."),
    PHAP_BAO("Thần Binh Các Chủ", "Bán vũ khí, giáp trụ và pháp bảo chiến đấu."),
    BAN_VE("Luyện Khí Đại Sư", "Bán bản vẽ pháp bảo và vật liệu luyện khí quý hiếm."),
    CONG_PHAP("Tàng Kinh Các Trưởng Lão", "Bán công pháp võ kỹ, thân pháp, tuyệt kỹ và bí pháp."),
    NHIEM_VU_THI_TRAN("Chấp Sự Thị Trấn", "Giao nhiệm vụ thu thập, tiêu diệt yêu thú để kiếm công huân và linh thạch."),
    TONG_MON_THUONG_VAN("Thương Vân Trưởng Lão", "Trao đổi pháp bảo tầm trung, công pháp Thương Vân Tông và nhiệm vụ tông môn."),
    TONG_MON_CON_LUAN("Côn Luân Thái Thượng", "Trao đổi pháp bảo vô thượng, bí pháp đỉnh phong và nhiệm vụ tối cao Côn Luân Thánh Địa.");

    private final String title;
    private final String description;

    TuTienNpcRole(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public String getTranslationKey() {
        return "tutien.npc_role." + this.name().toLowerCase();
    }

    public Component getTitleComponent() {
        return Component.translatable(getTranslationKey());
    }

    public Component getDescriptionComponent() {
        return Component.translatable(getTranslationKey() + ".desc");
    }

    public List<NpcTradeOffer> getOffers() {
        List<NpcTradeOffer> offers = new ArrayList<>();
        switch (this) {
            case DAN_DUOC -> {
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.HOI_HUYET_DAN_1.get(), 4), 20, 0, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.HOI_KHI_DAN_1.get(), 4), 25, 0, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.TU_VI_DAN_1.get(), 2), 50, 0, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.TOC_DO_DAN_1.get(), 1), 80, 0, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.TRUC_CO_DAN.get(), 1), 500, 50, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.CUONG_LUC_DAN_1.get(), 2), 60, 0, 0, 0, "none"));
            }
            case DAN_PHUONG -> {
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.DAN_PHUONG_HOI_HUYET.get(), 1), 100, 20, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.DAN_PHUONG_HOI_KHI.get(), 1), 120, 25, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.DAN_PHUONG_CUONG_LUC.get(), 1), 200, 40, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.DAN_PHUONG_TRUC_CO.get(), 1), 800, 100, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.LO_LUYEN_DAN_ITEM.get(), 1), 300, 50, 0, 0, "none"));
            }
            case PHAP_BAO -> {
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.THANH_PHONG_KIEM.get(), 1), 300, 50, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.HOANG_DAO_BAO_GIAP.get(), 1), 400, 60, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.HO_TAM_KINH.get(), 1), 800, 100, 0, 1, "none"));
            }
            case BAN_VE -> {
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.BAN_VE_KIEM.get(), 1), 150, 30, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.BAN_VE_DAO_BAO.get(), 1), 180, 35, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.BAN_VE_PHAP_KHI.get(), 1), 250, 50, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.LO_LUYEN_KHI_ITEM.get(), 1), 400, 60, 0, 0, "none"));
            }
            case CONG_PHAP -> {
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.SACH_THANH_PHONG_KIEM.get(), 1), 200, 50, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.SACH_LANG_BA_VI_BO.get(), 1), 500, 100, 0, 1, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.SACH_LIET_HOA_CHUONG.get(), 1), 250, 60, 0, 0, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.SACH_THIEN_LOI_QUYET.get(), 1), 2000, 400, 0, 2, "none"));
            }
            case NHIEM_VU_THI_TRAN -> {
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.TRAN_PHAP_CORE_ITEM.get(), 1), 1000, 200, 0, 1, "none"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.DAT_LINH_DUOC_HA_CAP_ITEM.get(), 8), 100, 20, 0, 0, "none"));
            }
            case TONG_MON_THUONG_VAN -> {
                // Thương Vân Tông - Mid tier
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.HUYEN_THIET_KIEM.get(), 1), 2000, 0, 300, 1, "thuong_van"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.HUYEN_DAO_BAO_GIAP.get(), 1), 2500, 0, 400, 1, "thuong_van"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.SACH_HUYEN_BANG_KIEM.get(), 1), 3000, 0, 500, 2, "thuong_van"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.KIM_DAN_DAN.get(), 1), 5000, 0, 800, 2, "thuong_van"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.CHIEU_YEU_KINH.get(), 1), 4000, 0, 600, 2, "thuong_van"));
            }
            case TONG_MON_CON_LUAN -> {
                // Côn Luân Thánh Địa - High tier & Late game
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.XICH_DIEM_KIEM.get(), 1), 20000, 0, 1500, 3, "con_luan"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.THAI_HU_KIEM.get(), 1), 100000, 0, 5000, 5, "con_luan"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.THIEN_DAO_BAO_GIAP.get(), 1), 80000, 0, 4000, 5, "con_luan"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.SACH_VAN_KIEM_QUY_TONG.get(), 1), 150000, 0, 8000, 6, "con_luan"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.SACH_THAI_HU_THAN_LOI.get(), 1), 200000, 0, 10000, 7, "con_luan"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.CHU_TIEN_TRAN_DO.get(), 1), 250000, 0, 12000, 7, "con_luan"));
                offers.add(new NpcTradeOffer(new ItemStack(ModItems.DANG_TIEN_DAN.get(), 1), 500000, 0, 20000, 7, "con_luan"));
            }
        }
        return offers;
    }
}
