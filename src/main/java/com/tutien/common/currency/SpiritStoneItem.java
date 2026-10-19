package com.tutien.common.currency;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SpiritStoneItem extends Item {
    public enum Quality {
        HA_PHAM(1, "quality.tutien.ha_pham", ChatFormatting.WHITE, 1L),
        TRUNG_PHAM(2, "quality.tutien.trung_pham", ChatFormatting.GREEN, 1000L),
        THUONG_PHAM(3, "quality.tutien.thuong_pham", ChatFormatting.AQUA, 1000000L),
        THAN_PHAM(4, "quality.tutien.than_pham", ChatFormatting.GOLD, 1000000000L);

        private final int tier;
        private final String translationKey;
        private final ChatFormatting color;
        private final long baseValue; // Equivalent value in Hạ Phẩm

        Quality(int tier, String translationKey, ChatFormatting color, long baseValue) {
            this.tier = tier;
            this.translationKey = translationKey;
            this.color = color;
            this.baseValue = baseValue;
        }

        public int getTier() { return tier; }
        public String getTranslationKey() { return translationKey; }
        public MutableComponent getNameComponent() {
            return Component.translatable(translationKey).withStyle(color);
        }
        public ChatFormatting getColor() { return color; }
        public long getBaseValue() { return baseValue; }
    }

    private final Quality quality;

    public SpiritStoneItem(Quality quality) {
        super(new Item.Properties().stacksTo(64));
        this.quality = quality;
    }

    public Quality getQuality() {
        return quality;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.tutien.spirit_stone_quality", quality.getNameComponent()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.tutien.spirit_stone_desc").withStyle(ChatFormatting.YELLOW));
        if (quality == Quality.HA_PHAM) {
            tooltipComponents.add(Component.translatable("tooltip.tutien.spirit_stone_exchange_1").withStyle(ChatFormatting.DARK_GRAY));
        } else if (quality == Quality.TRUNG_PHAM) {
            tooltipComponents.add(Component.translatable("tooltip.tutien.spirit_stone_exchange_2").withStyle(ChatFormatting.DARK_GRAY));
        } else if (quality == Quality.THUONG_PHAM) {
            tooltipComponents.add(Component.translatable("tooltip.tutien.spirit_stone_exchange_3").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.tutien.spirit_stone_exchange_4").withStyle(ChatFormatting.GOLD));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
