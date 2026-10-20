package com.tutien.client.hud;

import net.minecraft.nbt.CompoundTag;

public class ClientCultivationCache {
    public static int realm = 0;
    public static int layer = 1;
    public static double tuVi = 0.0;
    public static double foundation = 1.0;
    public static float lingQi = 200.0f;
    public static float maxLingQi = 200.0f;
    public static boolean isMeditating = false;
    public static double speedMultiplier = 1.0;
    public static long congHuan = 0;
    public static long congHien = 0;
    public static String sectId = "none";
    public static String[] equippedSkills = new String[]{"", "", "", ""};

    public static void updateFromTag(CompoundTag tag) {
        realm = tag.getInt("Realm");
        layer = Math.max(1, tag.getInt("Layer"));
        tuVi = tag.getDouble("TuVi");
        foundation = tag.contains("Foundation") ? tag.getDouble("Foundation") : 1.0;
        lingQi = tag.getFloat("LingQi");
        maxLingQi = tag.contains("MaxLingQi") ? tag.getFloat("MaxLingQi") : 200.0f;
        isMeditating = tag.getBoolean("Meditating");
        congHuan = tag.getLong("CongHuan");
        congHien = tag.getLong("CongHien");
        sectId = tag.getString("SectId");

        for (int i = 0; i < 4; i++) {
            equippedSkills[i] = tag.getString("EquippedSkill_" + i);
        }
    }
}
