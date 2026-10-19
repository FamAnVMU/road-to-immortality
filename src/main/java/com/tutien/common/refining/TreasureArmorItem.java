package com.tutien.common.refining;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class TreasureArmorItem extends ArmorItem {
    private final String tierName;
    private final String color;
    private final float qiRegenBonus;

    public TreasureArmorItem(Holder<ArmorMaterial> material, Type type, String tierName, String color, float qiRegenBonus, Properties properties) {
        super(material, type, properties);
        this.tierName = tierName;
        this.color = color;
        this.qiRegenBonus = qiRegenBonus;
    }

    public float getQiRegenBonus() {
        return qiRegenBonus;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§7Đẳng cấp: " + color + tierName));
        tooltipComponents.add(Component.literal("§bHồi phục linh khí: §e+" + qiRegenBonus + " Qi/giây"));
        tooltipComponents.add(Component.literal("§7Chế tạo từ tơ thiên tằm và huyền thiết ngàn năm."));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
