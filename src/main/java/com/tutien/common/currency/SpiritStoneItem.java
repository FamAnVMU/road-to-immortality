package com.tutien.common.currency;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SpiritStoneItem extends Item {
    public enum Quality {
        HA_PHAM(1, "Hạ Phẩm", ChatFormatting.WHITE, 1L),
        TRUNG_PHAM(2, "Trung Phẩm", ChatFormatting.GREEN, 1000L),
        THUONG_PHAM(3, "Thượng Phẩm", ChatFormatting.AQUA, 1000000L),
        THAN_PHAM(4, "Thần Phẩm", ChatFormatting.GOLD, 1000000000L);

        private final int tier;
        private final String name;
        private final ChatFormatting color;
        private final long baseValue; // Equivalent value in Hạ Phẩm

        Quality(int tier, String name, ChatFormatting color, long baseValue) {
            this.tier = tier;
            this.name = name;
            this.color = color;
            this.baseValue = baseValue;
        }

        public int getTier() { return tier; }
        public String getName() { return name; }
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
        tooltipComponents.add(Component.literal("§7Phẩm chất: " + quality.getColor() + quality.getName()));
        tooltipComponents.add(Component.literal("§eĐơn vị tiền tệ và tinh hoa linh khí của Tu Tiên giới."));
        if (quality == Quality.HA_PHAM) {
            tooltipComponents.add(Component.literal("§81.000 Hạ Phẩm = 1 Trung Phẩm"));
        } else if (quality == Quality.TRUNG_PHAM) {
            tooltipComponents.add(Component.literal("§81.000 Trung Phẩm = 1 Thượng Phẩm"));
        } else if (quality == Quality.THUONG_PHAM) {
            tooltipComponents.add(Component.literal("§81.000 Thượng Phẩm = 1 Thần Phẩm"));
        } else {
            tooltipComponents.add(Component.literal("§6Linh thạch vô thượng tương đương 1 tỷ Hạ Phẩm linh thạch!"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
