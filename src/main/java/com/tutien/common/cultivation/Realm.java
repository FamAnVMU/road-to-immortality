package com.tutien.common.cultivation;

import com.tutien.common.config.TuTienConfig;

public enum Realm {
    LUYEN_KHI(0, "Luyện Khí", "tutien:truc_co_dan", 20.0f, 2.0f, 1.0f, 100.0f),
    TRUC_CO(1, "Trúc Cơ", "tutien:kim_dan_dan", 40.0f, 5.0f, 3.0f, 250.0f),
    KIM_DAN(2, "Kim Đan", "tutien:nguyen_anh_dan", 80.0f, 10.0f, 6.0f, 500.0f),
    NGUYEN_ANH(3, "Nguyên Anh", "tutien:hoa_than_dan", 150.0f, 18.0f, 10.0f, 1000.0f),
    HOA_THAN(4, "Hóa Thần", "tutien:dong_hu_dan", 250.0f, 30.0f, 15.0f, 2000.0f),
    DONG_HU(5, "Động Hư", "tutien:do_kiep_dan", 400.0f, 45.0f, 22.0f, 3500.0f),
    DO_KIEP(6, "Độ Kiếp", "tutien:vu_hoa_dan", 600.0f, 65.0f, 30.0f, 5500.0f),
    VU_HOA(7, "Vũ Hóa", "tutien:dang_tien_dan", 900.0f, 90.0f, 40.0f, 8000.0f),
    DANG_TIEN(8, "Đăng Tiên", "", 1500.0f, 130.0f, 55.0f, 12000.0f);

    private final int id;
    private final String displayName;
    private final String nextBreakthroughPillId;
    private final float baseHealthBonus;
    private final float baseAttackBonus;
    private final float baseArmorBonus;
    private final float baseMaxQi;

    Realm(int id, String displayName, String nextBreakthroughPillId, float baseHealthBonus, float baseAttackBonus, float baseArmorBonus, float baseMaxQi) {
        this.id = id;
        this.displayName = displayName;
        this.nextBreakthroughPillId = nextBreakthroughPillId;
        this.baseHealthBonus = baseHealthBonus;
        this.baseAttackBonus = baseAttackBonus;
        this.baseArmorBonus = baseArmorBonus;
        this.baseMaxQi = baseMaxQi;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getNextBreakthroughPillId() {
        return nextBreakthroughPillId;
    }

    public float getBaseHealthBonus() {
        return baseHealthBonus;
    }

    public float getBaseAttackBonus() {
        return baseAttackBonus;
    }

    public float getBaseArmorBonus() {
        return baseArmorBonus;
    }

    public float getBaseMaxQi() {
        return baseMaxQi;
    }

    public static Realm fromId(int id) {
        if (id < 0) return LUYEN_KHI;
        if (id >= values().length) return DANG_TIEN;
        return values()[id];
    }

    public Realm getNext() {
        if (this.id < values().length - 1) {
            return values()[this.id + 1];
        }
        return this;
    }

    /**
     * Tính toán lượng tu vi cần thiết để hoàn thành tầng hiện tại
     */
    public double calculateRequiredTuVi(int layer) {
        double base = TuTienConfig.TU_VI_BASE_REQUIREMENT.get();
        double layerMult = TuTienConfig.TU_VI_LAYER_MULTIPLIER.get();
        double realmMult = TuTienConfig.TU_VI_REALM_MULTIPLIER.get();

        double realmFactor = Math.pow(realmMult, this.id);
        double layerFactor = Math.pow(layerMult, Math.max(0, layer - 1));

        return Math.floor(base * realmFactor * layerFactor);
    }
}
