package com.tutien.common.registry;

import com.tutien.TuTienMod;
import com.tutien.common.alchemy.PillItem;
import com.tutien.common.alchemy.RecipeScrollItem;
import com.tutien.common.currency.SpiritStoneItem;
import com.tutien.common.currency.SpiritStoneConversionMenu;
import com.tutien.common.refining.*;
import com.tutien.common.skill.SkillManualItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, TuTienMod.MOD_ID);

    // ================= 1. LINH THẠCH (4 Phẩm chất) =================
    public static final Supplier<Item> HA_PHAM_LINH_THACH = ITEMS.register("ha_pham_linh_thach",
            () -> new SpiritStoneItem(SpiritStoneItem.Quality.HA_PHAM));
    public static final Supplier<Item> TRUNG_PHAM_LINH_THACH = ITEMS.register("trung_pham_linh_thach",
            () -> new SpiritStoneItem(SpiritStoneItem.Quality.TRUNG_PHAM));
    public static final Supplier<Item> THUONG_PHAM_LINH_THACH = ITEMS.register("thuong_pham_linh_thach",
            () -> new SpiritStoneItem(SpiritStoneItem.Quality.THUONG_PHAM));
    public static final Supplier<Item> THAN_PHAM_LINH_THACH = ITEMS.register("than_pham_linh_thach",
            () -> new SpiritStoneItem(SpiritStoneItem.Quality.THAN_PHAM));

    // Gương quy đổi linh thạch (mở GUI quy đổi tiền tệ mọi lúc)
    public static final Supplier<Item> LINH_THACH_QUY_DOI_ITEM = ITEMS.register("linh_thach_quy_doi_item",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                    if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.openMenu(new MenuProvider() {
                            @Override
                            public Component getDisplayName() {
                                return Component.literal("Giao Diện Quy Đổi Linh Thạch");
                            }

                            @Nullable
                            @Override
                            public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                                return new SpiritStoneConversionMenu(containerId, playerInventory);
                            }
                        });
                    }
                    return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
                }

                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.literal("§6Linh Lung Bảo Kính: §eNhấp chuột phải để mở giao diện quy đổi linh thạch."));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    // ================= 2. BLOCK ITEMS =================
    public static final Supplier<Item> LO_LUYEN_DAN_ITEM = ITEMS.register("lo_luyen_dan",
            () -> new BlockItem(ModBlocks.LO_LUYEN_DAN.get(), new Item.Properties()));
    public static final Supplier<Item> LO_LUYEN_KHI_ITEM = ITEMS.register("lo_luyen_khi",
            () -> new BlockItem(ModBlocks.LO_LUYEN_KHI.get(), new Item.Properties()));
    public static final Supplier<Item> TE_DAN_HOI_PHUC_ITEM = ITEMS.register("te_dan_hoi_phuc",
            () -> new BlockItem(ModBlocks.TE_DAN_HOI_PHUC.get(), new Item.Properties()));
    public static final Supplier<Item> TRAN_PHAP_CORE_ITEM = ITEMS.register("tran_phap_core",
            () -> new BlockItem(ModBlocks.TRAN_PHAP_CORE.get(), new Item.Properties()));

    public static final Supplier<Item> DAT_LINH_DUOC_HA_CAP_ITEM = ITEMS.register("dat_linh_duoc_ha_cap",
            () -> new BlockItem(ModBlocks.DAT_LINH_DUOC_HA_CAP.get(), new Item.Properties()));
    public static final Supplier<Item> DAT_LINH_DUOC_TRUNG_CAP_ITEM = ITEMS.register("dat_linh_duoc_trung_cap",
            () -> new BlockItem(ModBlocks.DAT_LINH_DUOC_TRUNG_CAP.get(), new Item.Properties()));
    public static final Supplier<Item> DAT_LINH_DUOC_THUONG_CAP_ITEM = ITEMS.register("dat_linh_duoc_thuong_cap",
            () -> new BlockItem(ModBlocks.DAT_LINH_DUOC_THUONG_CAP.get(), new Item.Properties()));

    // ================= 3. LINH DƯỢC (9 Phẩm Cấp) =================
    public static final Supplier<Item> LINH_THAO = ITEMS.register("linh_thao",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> THANH_TAM_HOA = ITEMS.register("thanh_tam_hoa",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> HUYEN_BANG_THAO = ITEMS.register("huyen_bang_thao",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> XICH_HOA_QUA = ITEMS.register("xich_hoa_qua",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> NGU_HANH_CHI = ITEMS.register("ngu_hanh_chi",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> U_HON_HOA = ITEMS.register("u_hon_hoa",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> THIEN_LINH_QUA = ITEMS.register("thien_linh_qua",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CUU_DIEP_THAO = ITEMS.register("cuu_diep_thao",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> NGUYEN_THAN_THAO = ITEMS.register("nguyen_than_thao",
            () -> new Item(new Item.Properties()));

    // ================= 4. ĐAN DƯỢC (9 Tiers) =================
    // Đan dược tăng tu vi trực tiếp (1 đến 9 đan văn)
    public static final Supplier<Item> TU_VI_DAN_1 = ITEMS.register("tu_vi_dan_1", () -> new PillItem(PillItem.PillType.TU_VI, 1, 100.0, 0));
    public static final Supplier<Item> TU_VI_DAN_2 = ITEMS.register("tu_vi_dan_2", () -> new PillItem(PillItem.PillType.TU_VI, 2, 300.0, 0));
    public static final Supplier<Item> TU_VI_DAN_3 = ITEMS.register("tu_vi_dan_3", () -> new PillItem(PillItem.PillType.TU_VI, 3, 1000.0, 0));
    public static final Supplier<Item> TU_VI_DAN_4 = ITEMS.register("tu_vi_dan_4", () -> new PillItem(PillItem.PillType.TU_VI, 4, 3000.0, 0));
    public static final Supplier<Item> TU_VI_DAN_5 = ITEMS.register("tu_vi_dan_5", () -> new PillItem(PillItem.PillType.TU_VI, 5, 10000.0, 0));
    public static final Supplier<Item> TU_VI_DAN_6 = ITEMS.register("tu_vi_dan_6", () -> new PillItem(PillItem.PillType.TU_VI, 6, 30000.0, 0));
    public static final Supplier<Item> TU_VI_DAN_7 = ITEMS.register("tu_vi_dan_7", () -> new PillItem(PillItem.PillType.TU_VI, 7, 100000.0, 0));
    public static final Supplier<Item> TU_VI_DAN_8 = ITEMS.register("tu_vi_dan_8", () -> new PillItem(PillItem.PillType.TU_VI, 8, 300000.0, 0));
    public static final Supplier<Item> TU_VI_DAN_9 = ITEMS.register("tu_vi_dan_9", () -> new PillItem(PillItem.PillType.TU_VI, 9, 1000000.0, 0));

    // Đan dược tăng tốc độ tu luyện
    public static final Supplier<Item> TOC_DO_DAN_1 = ITEMS.register("toc_do_dan_1", () -> new PillItem(PillItem.PillType.SPEED, 1, 1.5, 3600)); // 3 mins
    public static final Supplier<Item> TOC_DO_DAN_2 = ITEMS.register("toc_do_dan_2", () -> new PillItem(PillItem.PillType.SPEED, 2, 2.0, 4800));
    public static final Supplier<Item> TOC_DO_DAN_3 = ITEMS.register("toc_do_dan_3", () -> new PillItem(PillItem.PillType.SPEED, 3, 2.5, 6000));
    public static final Supplier<Item> TOC_DO_DAN_4 = ITEMS.register("toc_do_dan_4", () -> new PillItem(PillItem.PillType.SPEED, 4, 3.0, 7200));
    public static final Supplier<Item> TOC_DO_DAN_5 = ITEMS.register("toc_do_dan_5", () -> new PillItem(PillItem.PillType.SPEED, 5, 4.0, 8400));
    public static final Supplier<Item> TOC_DO_DAN_6 = ITEMS.register("toc_do_dan_6", () -> new PillItem(PillItem.PillType.SPEED, 6, 5.0, 9600));
    public static final Supplier<Item> TOC_DO_DAN_7 = ITEMS.register("toc_do_dan_7", () -> new PillItem(PillItem.PillType.SPEED, 7, 6.0, 10800));
    public static final Supplier<Item> TOC_DO_DAN_8 = ITEMS.register("toc_do_dan_8", () -> new PillItem(PillItem.PillType.SPEED, 8, 8.0, 12000));
    public static final Supplier<Item> TOC_DO_DAN_9 = ITEMS.register("toc_do_dan_9", () -> new PillItem(PillItem.PillType.SPEED, 9, 10.0, 14400));

    // Đan dược đột phá cảnh giới (8 loại cho các cảnh giới kế tiếp)
    public static final Supplier<Item> TRUC_CO_DAN = ITEMS.register("truc_co_dan", () -> new PillItem(PillItem.PillType.BREAKTHROUGH, 2, 0, 0));
    public static final Supplier<Item> KIM_DAN_DAN = ITEMS.register("kim_dan_dan", () -> new PillItem(PillItem.PillType.BREAKTHROUGH, 3, 0, 0));
    public static final Supplier<Item> NGUYEN_ANH_DAN = ITEMS.register("nguyen_anh_dan", () -> new PillItem(PillItem.PillType.BREAKTHROUGH, 4, 0, 0));
    public static final Supplier<Item> HOA_THAN_DAN = ITEMS.register("hoa_than_dan", () -> new PillItem(PillItem.PillType.BREAKTHROUGH, 5, 0, 0));
    public static final Supplier<Item> DONG_HU_DAN = ITEMS.register("dong_hu_dan", () -> new PillItem(PillItem.PillType.BREAKTHROUGH, 6, 0, 0));
    public static final Supplier<Item> DO_KIEP_DAN = ITEMS.register("do_kiep_dan", () -> new PillItem(PillItem.PillType.BREAKTHROUGH, 7, 0, 0));
    public static final Supplier<Item> VU_HOA_DAN = ITEMS.register("vu_hoa_dan", () -> new PillItem(PillItem.PillType.BREAKTHROUGH, 8, 0, 0));
    public static final Supplier<Item> DANG_TIEN_DAN = ITEMS.register("dang_tien_dan", () -> new PillItem(PillItem.PillType.BREAKTHROUGH, 9, 0, 0));

    // Đan dược hồi phục (Máu & Linh khí)
    public static final Supplier<Item> HOI_HUYET_DAN_1 = ITEMS.register("hoi_huyet_dan_1", () -> new PillItem(PillItem.PillType.HEAL_HP, 1, 10.0, 0));
    public static final Supplier<Item> HOI_HUYET_DAN_2 = ITEMS.register("hoi_huyet_dan_2", () -> new PillItem(PillItem.PillType.HEAL_HP, 2, 25.0, 0));
    public static final Supplier<Item> HOI_HUYET_DAN_3 = ITEMS.register("hoi_huyet_dan_3", () -> new PillItem(PillItem.PillType.HEAL_HP, 3, 60.0, 0));

    public static final Supplier<Item> HOI_KHI_DAN_1 = ITEMS.register("hoi_khi_dan_1", () -> new PillItem(PillItem.PillType.HEAL_QI, 1, 50.0, 0));
    public static final Supplier<Item> HOI_KHI_DAN_2 = ITEMS.register("hoi_khi_dan_2", () -> new PillItem(PillItem.PillType.HEAL_QI, 2, 150.0, 0));
    public static final Supplier<Item> HOI_KHI_DAN_3 = ITEMS.register("hoi_khi_dan_3", () -> new PillItem(PillItem.PillType.HEAL_QI, 3, 400.0, 0));

    // Đan dược Buff chiến đấu
    public static final Supplier<Item> CUONG_LUC_DAN_1 = ITEMS.register("cuong_luc_dan_1", () -> new PillItem(PillItem.PillType.BUFF_ATTACK, 1, 4.0, 2400));
    public static final Supplier<Item> KIM_CANG_DAN_1 = ITEMS.register("kim_cang_dan_1", () -> new PillItem(PillItem.PillType.BUFF_DEFENSE, 1, 3.0, 2400));

    // ================= 5. ĐAN PHƯƠNG & BẢN VẼ =================
    public static final Supplier<Item> DAN_PHUONG_HOI_HUYET = ITEMS.register("dan_phuong_hoi_huyet",
            () -> new RecipeScrollItem(RecipeScrollItem.ScrollType.DAN_PHUONG, "Hồi Huyết Đan", 100, 1));
    public static final Supplier<Item> DAN_PHUONG_HOI_KHI = ITEMS.register("dan_phuong_hoi_khi",
            () -> new RecipeScrollItem(RecipeScrollItem.ScrollType.DAN_PHUONG, "Hồi Khí Đan", 120, 1));
    public static final Supplier<Item> DAN_PHUONG_CUONG_LUC = ITEMS.register("dan_phuong_cuong_luc",
            () -> new RecipeScrollItem(RecipeScrollItem.ScrollType.DAN_PHUONG, "Cường Lực Đan", 200, 2));
    public static final Supplier<Item> DAN_PHUONG_TRUC_CO = ITEMS.register("dan_phuong_truc_co",
            () -> new RecipeScrollItem(RecipeScrollItem.ScrollType.DAN_PHUONG, "Trúc Cơ Đan", 500, 2));
    public static final Supplier<Item> DAN_PHUONG_KIM_DAN = ITEMS.register("dan_phuong_kim_dan",
            () -> new RecipeScrollItem(RecipeScrollItem.ScrollType.DAN_PHUONG, "Kim Đan Đan", 1200, 3));
    public static final Supplier<Item> DAN_PHUONG_NGUYEN_ANH = ITEMS.register("dan_phuong_nguyen_anh",
            () -> new RecipeScrollItem(RecipeScrollItem.ScrollType.DAN_PHUONG, "Nguyên Anh Đan", 3000, 4));

    public static final Supplier<Item> BAN_VE_KIEM = ITEMS.register("ban_ve_kiem",
            () -> new RecipeScrollItem(RecipeScrollItem.ScrollType.BAN_VE, "Bản Vẽ Phi Kiếm", 150, 1));
    public static final Supplier<Item> BAN_VE_DAO_BAO = ITEMS.register("ban_ve_dao_bao",
            () -> new RecipeScrollItem(RecipeScrollItem.ScrollType.BAN_VE, "Bản Vẽ Đạo Bào", 180, 1));
    public static final Supplier<Item> BAN_VE_PHAP_KHI = ITEMS.register("ban_ve_phap_khi",
            () -> new RecipeScrollItem(RecipeScrollItem.ScrollType.BAN_VE, "Bản Vẽ Pháp Khí", 250, 2));

    // ================= 6. SÁCH CÔNG PHÁP =================
    public static final Supplier<Item> SACH_THANH_PHONG_KIEM = ITEMS.register("sach_thanh_phong_kiem",
            () -> new SkillManualItem("thanh_phong_kiem", "Thanh Phong Kiếm Quyết",
                    SkillManualItem.SkillTier.HOANG, SkillManualItem.SkillType.VO_KY, 20.0f, 5));
    public static final Supplier<Item> SACH_LANG_BA_VI_BO = ITEMS.register("sach_lang_ba_vi_bo",
            () -> new SkillManualItem("lang_ba_vi_bo", "Lăng Ba Vi Bộ",
                    SkillManualItem.SkillTier.HUYEN, SkillManualItem.SkillType.THAN_PHAP, 30.0f, 4));
    public static final Supplier<Item> SACH_THIEN_LOI_QUYET = ITEMS.register("sach_thien_loi_quyet",
            () -> new SkillManualItem("thien_loi_quyet", "Thiên Lôi Quyết",
                    SkillManualItem.SkillTier.DIA, SkillManualItem.SkillType.TUYET_KY, 80.0f, 12));
    public static final Supplier<Item> SACH_VAN_KIEM_QUY_TONG = ITEMS.register("sach_van_kiem_quy_tong",
            () -> new SkillManualItem("van_kiem_quy_tong", "Vạn Kiếm Quy Tông",
                    SkillManualItem.SkillTier.THIEN, SkillManualItem.SkillType.BI_PHAP, 200.0f, 30));
    public static final Supplier<Item> SACH_LIET_HOA_CHUONG = ITEMS.register("sach_liet_hoa_chuong",
            () -> new SkillManualItem("liet_hoa_chuong", "Liệt Hỏa Chưởng",
                    SkillManualItem.SkillTier.HOANG, SkillManualItem.SkillType.VO_KY, 25.0f, 6));
    public static final Supplier<Item> SACH_HU_KHONG_BO = ITEMS.register("sach_hu_khong_bo",
            () -> new SkillManualItem("hu_khong_bo", "Hư Không Bộ",
                    SkillManualItem.SkillTier.DIA, SkillManualItem.SkillType.THAN_PHAP, 60.0f, 8));
    public static final Supplier<Item> SACH_HUYEN_BANG_KIEM = ITEMS.register("sach_huyen_bang_kiem",
            () -> new SkillManualItem("huyen_bang_kiem", "Huyền Băng Kiếm Trận",
                    SkillManualItem.SkillTier.HUYEN, SkillManualItem.SkillType.TUYET_KY, 70.0f, 15));
    public static final Supplier<Item> SACH_THAI_HU_THAN_LOI = ITEMS.register("sach_thai_hu_than_loi",
            () -> new SkillManualItem("thai_hu_than_loi", "Thái Hư Thần Lôi",
                    SkillManualItem.SkillTier.THIEN, SkillManualItem.SkillType.BI_PHAP, 250.0f, 40));

    // ================= 7. PHÁP BẢO VŨ KHÍ =================
    public static final Supplier<Item> THANH_PHONG_KIEM = ITEMS.register("thanh_phong_kiem",
            () -> new TreasureSwordItem(TuTienItemTiers.HOANG, TreasureSwordItem.ArtifactTier.HOANG, "Thanh Phong Kiếm Khí", new Item.Properties()));
    public static final Supplier<Item> HUYEN_THIET_KIEM = ITEMS.register("huyen_thiet_kiem",
            () -> new TreasureSwordItem(TuTienItemTiers.HUYEN, TreasureSwordItem.ArtifactTier.HUYEN, "Huyền Thiết Trảm Kích", new Item.Properties()));
    public static final Supplier<Item> XICH_DIEM_KIEM = ITEMS.register("xich_diem_kiem",
            () -> new TreasureSwordItem(TuTienItemTiers.DIA, TreasureSwordItem.ArtifactTier.DIA, "Xích Diệm Hỏa Trảm", new Item.Properties()));
    public static final Supplier<Item> THAI_HU_KIEM = ITEMS.register("thai_hu_kiem",
            () -> new TreasureSwordItem(TuTienItemTiers.THIEN, TreasureSwordItem.ArtifactTier.THIEN, "Vạn Kiếm Quy Tông", new Item.Properties()));

    // ================= 8. PHÁP BẢO GIÁP TRỤ (Đạo Bào) =================
    public static final Supplier<Item> HOANG_DAO_BAO_MU = ITEMS.register("hoang_dao_bao_mu",
            () -> new TreasureArmorItem(TuTienArmorMaterials.HOANG_DAO_BAO, ArmorItem.Type.HELMET, "Hoàng Cấp", "§e", 0.5f, new Item.Properties()));
    public static final Supplier<Item> HOANG_DAO_BAO_GIAP = ITEMS.register("hoang_dao_bao_giap",
            () -> new TreasureArmorItem(TuTienArmorMaterials.HOANG_DAO_BAO, ArmorItem.Type.CHESTPLATE, "Hoàng Cấp", "§e", 1.0f, new Item.Properties()));
    public static final Supplier<Item> HOANG_DAO_BAO_QUAN = ITEMS.register("hoang_dao_bao_quan",
            () -> new TreasureArmorItem(TuTienArmorMaterials.HOANG_DAO_BAO, ArmorItem.Type.LEGGINGS, "Hoàng Cấp", "§e", 0.8f, new Item.Properties()));
    public static final Supplier<Item> HOANG_DAO_BAO_GIAY = ITEMS.register("hoang_dao_bao_giay",
            () -> new TreasureArmorItem(TuTienArmorMaterials.HOANG_DAO_BAO, ArmorItem.Type.BOOTS, "Hoàng Cấp", "§e", 0.5f, new Item.Properties()));

    public static final Supplier<Item> HUYEN_DAO_BAO_GIAP = ITEMS.register("huyen_dao_bao_giap",
            () -> new TreasureArmorItem(TuTienArmorMaterials.HUYEN_DAO_BAO, ArmorItem.Type.CHESTPLATE, "Huyền Cấp", "§9", 2.0f, new Item.Properties()));
    public static final Supplier<Item> DIA_DAO_BAO_GIAP = ITEMS.register("dia_dao_bao_giap",
            () -> new TreasureArmorItem(TuTienArmorMaterials.DIA_DAO_BAO, ArmorItem.Type.CHESTPLATE, "Địa Cấp", "§5", 3.5f, new Item.Properties()));
    public static final Supplier<Item> THIEN_DAO_BAO_GIAP = ITEMS.register("thien_dao_bao_giap",
            () -> new TreasureArmorItem(TuTienArmorMaterials.THIEN_DAO_BAO, ArmorItem.Type.CHESTPLATE, "Thiên Cấp", "§6§l", 6.0f, new Item.Properties()));

    // ================= 9. PHÁP BẢO PHỤ TRỢ (Curios/Combat) =================
    public static final Supplier<Item> HO_TAM_KINH = ITEMS.register("ho_tam_kinh",
            () -> new SupportArtifactItem(SupportArtifactItem.ArtifactType.HO_TAM_KINH));
    public static final Supplier<Item> CHIEU_YEU_KINH = ITEMS.register("chieu_yeu_kinh",
            () -> new SupportArtifactItem(SupportArtifactItem.ArtifactType.CHIEU_YEU_KINH));
    public static final Supplier<Item> PHIEN_THIEN_AN = ITEMS.register("phien_thien_an",
            () -> new SupportArtifactItem(SupportArtifactItem.ArtifactType.PHIEN_THIEN_AN));
    public static final Supplier<Item> CHU_TIEN_TRAN_DO = ITEMS.register("chu_tien_tran_do",
            () -> new SupportArtifactItem(SupportArtifactItem.ArtifactType.CHU_TIEN_TRAN_DO));
}
