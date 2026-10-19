package com.tutien.common.alchemy;

import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.registry.ModDataAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class RecipeScrollItem extends Item {
    public enum ScrollType {
        DAN_PHUONG("scroll.tutien.dan_phuong", "scroll.tutien.dan_phuong.desc"),
        BAN_VE("scroll.tutien.ban_ve", "scroll.tutien.ban_ve.desc");

        private final String titleKey;
        private final String descKey;

        ScrollType(String titleKey, String descKey) {
            this.titleKey = titleKey;
            this.descKey = descKey;
        }

        public MutableComponent getTitleComponent() { return Component.translatable(titleKey); }
        public MutableComponent getDescComponent() { return Component.translatable(descKey); }
    }

    private final ScrollType scrollType;
    private final String recipeNameKey;
    private final int expReward;
    private final int requiredLevel;

    public RecipeScrollItem(ScrollType scrollType, String recipeNameKey, int expReward, int requiredLevel) {
        super(new Item.Properties().stacksTo(16));
        this.scrollType = scrollType;
        this.recipeNameKey = recipeNameKey;
        this.expReward = expReward;
        this.requiredLevel = requiredLevel;
    }

    public ScrollType getScrollType() { return scrollType; }
    public String getRecipeNameKey() { return recipeNameKey; }
    public int getExpReward() { return expReward; }
    public int getRequiredLevel() { return requiredLevel; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PlayerCultivationData data = serverPlayer.getData(ModDataAttachments.CULTIVATION_DATA);
            if (data == null) return InteractionResultHolder.pass(stack);

            if (scrollType == ScrollType.DAN_PHUONG) {
                data.addAlchemyExp(expReward);
                serverPlayer.sendSystemMessage(Component.translatable("message.tutien.recipe_alchemy_researched",
                        Component.translatable(recipeNameKey), expReward, data.getAlchemyLevel()).withStyle(ChatFormatting.GREEN));
            } else {
                data.addRefiningExp(expReward);
                serverPlayer.sendSystemMessage(Component.translatable("message.tutien.recipe_refining_researched",
                        Component.translatable(recipeNameKey), expReward, data.getRefiningLevel()).withStyle(ChatFormatting.AQUA));
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 1.0f);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.tutien.recipe_title", scrollType.getTitleComponent(), Component.translatable(recipeNameKey)).withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("tooltip.tutien.recipe_req_level", requiredLevel).withStyle(ChatFormatting.GRAY));
        Component typeStr = scrollType == ScrollType.DAN_PHUONG ? Component.translatable("tooltip.tutien.recipe_alchemy") : Component.translatable("tooltip.tutien.recipe_refining");
        tooltipComponents.add(Component.translatable("tooltip.tutien.recipe_reward", expReward, typeStr).withStyle(ChatFormatting.GREEN));
        tooltipComponents.add(scrollType.getDescComponent().withStyle(ChatFormatting.DARK_GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
