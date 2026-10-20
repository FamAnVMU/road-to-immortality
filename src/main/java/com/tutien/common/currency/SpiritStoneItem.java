package com.tutien.common.currency;

import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.network.ModPayloads;
import com.tutien.common.registry.ModDataAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PlayerCultivationData data = serverPlayer.getData(ModDataAttachments.CULTIVATION_DATA);
            if (data == null) {
                return InteractionResultHolder.pass(stack);
            }

            int countToConsume = player.isShiftKeyDown() ? Math.min(stack.getCount(), 16) : 1;
            long baseTuVi = switch (quality) {
                case HA_PHAM -> 10L;
                case TRUNG_PHAM -> 10_000L;
                case THUONG_PHAM -> 10_000_000L;
                case THAN_PHAM -> 10_000_000_000L;
            };

            float baseQi = switch (quality) {
                case HA_PHAM -> 20.0f;
                case TRUNG_PHAM -> 200.0f;
                case THUONG_PHAM -> 2_000.0f;
                case THAN_PHAM -> 20_000.0f;
            };

            double totalTuVi = baseTuVi * countToConsume;
            float totalQi = baseQi * countToConsume;

            data.addTuVi(totalTuVi);
            data.restoreLingQi(totalQi);
            ModPayloads.syncToPlayer(serverPlayer);

            if (!player.getAbilities().instabuild) {
                stack.shrink(countToConsume);
            }

            serverPlayer.sendSystemMessage(Component.translatable("tutien.message.absorbed_spirit_stone", countToConsume, quality.getNameComponent(), (long) totalTuVi, (int) totalQi));

            if (serverPlayer.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT, serverPlayer.getX(), serverPlayer.getY() + 1.0, serverPlayer.getZ(), 10 * countToConsume, 0.4, 0.4, 0.4, 0.1);
            }
        }

        player.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0f, 1.2f);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.tutien.spirit_stone_quality", quality.getNameComponent()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.tutien.spirit_stone_desc").withStyle(ChatFormatting.YELLOW));
        tooltipComponents.add(Component.translatable("tooltip.tutien.spirit_stone_use_hint").withStyle(ChatFormatting.GREEN));
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
