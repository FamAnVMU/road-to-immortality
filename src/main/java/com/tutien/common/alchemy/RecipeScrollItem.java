package com.tutien.common.alchemy;

import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.registry.ModDataAttachments;
import net.minecraft.network.chat.Component;
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
        DAN_PHUONG("Đan Phương", "Tăng kinh nghiệm luyện đan và mở khóa công thức"),
        BAN_VE("Bản Vẽ Luyện Khí", "Tăng kinh nghiệm luyện khí và mở khóa chế tạo pháp bảo");

        private final String title;
        private final String desc;

        ScrollType(String title, String desc) {
            this.title = title;
            this.desc = desc;
        }

        public String getTitle() { return title; }
        public String getDesc() { return desc; }
    }

    private final ScrollType scrollType;
    private final String recipeName;
    private final int expReward;
    private final int requiredLevel;

    public RecipeScrollItem(ScrollType scrollType, String recipeName, int expReward, int requiredLevel) {
        super(new Item.Properties().stacksTo(16));
        this.scrollType = scrollType;
        this.recipeName = recipeName;
        this.expReward = expReward;
        this.requiredLevel = requiredLevel;
    }

    public ScrollType getScrollType() { return scrollType; }
    public String getRecipeName() { return recipeName; }
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
                serverPlayer.sendSystemMessage(Component.literal("§a[Tu Tiên] Bạn đã nghiên cứu " + recipeName + ", nhận +" + expReward + " EXP Luyện Đan (Cấp " + data.getAlchemyLevel() + ")!"));
            } else {
                data.addRefiningExp(expReward);
                serverPlayer.sendSystemMessage(Component.literal("§b[Tu Tiên] Bạn đã nghiên cứu " + recipeName + ", nhận +" + expReward + " EXP Luyện Khí (Cấp " + data.getRefiningLevel() + ")!"));
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
        tooltipComponents.add(Component.literal("§6" + scrollType.getTitle() + ": §f" + recipeName));
        tooltipComponents.add(Component.literal("§7Yêu cầu cấp độ: §eCấp " + requiredLevel));
        tooltipComponents.add(Component.literal("§aNhận: §e+" + expReward + " EXP " + (scrollType == ScrollType.DAN_PHUONG ? "Luyện Đan" : "Luyện Khí")));
        tooltipComponents.add(Component.literal("§8" + scrollType.getDesc()));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
