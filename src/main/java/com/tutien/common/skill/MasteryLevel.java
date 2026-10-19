package com.tutien.common.skill;

public enum MasteryLevel {
    TIEU_THANH(1, "Tiểu Thành", 0, 1.0f, 1.0f),
    DAI_THANH(2, "Đại Thành", 200, 1.25f, 0.9f),
    VIEN_MAN(3, "Viên Mãn", 800, 1.6f, 0.8f),
    DAI_VIEN_MAN(4, "Đại Viên Mãn", 2500, 2.2f, 0.65f);

    private final int level;
    private final String displayName;
    private final int requiredExp;
    private final float damageMultiplier;
    private final float qiCostMultiplier;

    MasteryLevel(int level, String displayName, int requiredExp, float damageMultiplier, float qiCostMultiplier) {
        this.level = level;
        this.displayName = displayName;
        this.requiredExp = requiredExp;
        this.damageMultiplier = damageMultiplier;
        this.qiCostMultiplier = qiCostMultiplier;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getRequiredExp() {
        return requiredExp;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public float getQiCostMultiplier() {
        return qiCostMultiplier;
    }

    public static MasteryLevel fromExp(int exp) {
        if (exp >= DAI_VIEN_MAN.requiredExp) return DAI_VIEN_MAN;
        if (exp >= VIEN_MAN.requiredExp) return VIEN_MAN;
        if (exp >= DAI_VIEN_MAN.requiredExp) return DAI_THANH;
        if (exp >= DAI_THANH.requiredExp) return DAI_THANH;
        return TIEU_THANH;
    }
}
