package com.tutien.common.refining;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class TreasureArmorItem extends ArmorItem {
    private final String tierKey;
    private final ChatFormatting color;
    private final float qiRegenBonus;

    public TreasureArmorItem(Holder<ArmorMaterial> material, Type type, String tierKey, ChatFormatting color, float qiRegenBonus, Properties properties) {
        super(material, type, properties);
        this.tierKey = tierKey;
        this.color = color;
        this.qiRegenBonus = qiRegenBonus;
    }

    public float getQiRegenBonus() {
        return qiRegenBonus;
    }

    public MutableComponent getTierComponent() {
        return Component.translatable(tierKey).withStyle(color);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.tutien.artifact_tier", getTierComponent()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.tutien.armor_qi_regen", qiRegenBonus).withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Component.translatable("tooltip.tutien.armor_desc").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
