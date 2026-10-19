package com.tutien.common.npc;

import net.minecraft.world.item.ItemStack;

public class NpcTradeOffer {
    private final ItemStack output;
    private final long spiritStoneCost; // Hạ Phẩm Linh Thạch equivalent
    private final long congHuanCost;    // Merit points
    private final long congHienCost;    // Sect contribution points
    private final int minRealm;         // 0 = Luyện Khí, etc.
    private final String requiredSect;  // "none", "thuong_van", "con_luan"

    public NpcTradeOffer(ItemStack output, long spiritStoneCost, long congHuanCost, long congHienCost, int minRealm, String requiredSect) {
        this.output = output;
        this.spiritStoneCost = spiritStoneCost;
        this.congHuanCost = congHuanCost;
        this.congHienCost = congHienCost;
        this.minRealm = minRealm;
        this.requiredSect = requiredSect;
    }

    public ItemStack getOutput() { return output.copy(); }
    public long getSpiritStoneCost() { return spiritStoneCost; }
    public long getCongHuanCost() { return congHuanCost; }
    public long getCongHienCost() { return congHienCost; }
    public int getMinRealm() { return minRealm; }
    public String getRequiredSect() { return requiredSect; }
}
