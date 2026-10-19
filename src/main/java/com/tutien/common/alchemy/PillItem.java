package com.tutien.common.alchemy;

import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.registry.ModDataAttachments;
import net.minecraft.ChatFormatting;
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

public class PillItem extends Item {
    public enum PillType {
        TU_VI,
        SPEED,
        BREAKTHROUGH,
        HEAL_HP,
        HEAL_QI,
        BUFF_ATTACK,
        BUFF_DEFENSE,
        BUFF_SPEED
    }

    private final PillType type;
    private final int danVan; // 1 to 9 đan văn
    private final double value; // Tu vi or buff value or heal amount
    private final int durationTicks; // Duration for buffs

    public PillItem(PillType type, int danVan, double value, int durationTicks) {
        super(new Item.Properties().stacksTo(64));
        this.type = type;
        this.danVan = Math.max(1, Math.min(9, danVan));
        this.value = value;
        this.durationTicks = durationTicks;
    }

    public PillType getType() { return type; }
    public int getDanVan() { return danVan; }
    public double getValue() { return value; }
    public int getDurationTicks() { return durationTicks; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PlayerCultivationData data = serverPlayer.getData(ModDataAttachments.CULTIVATION_DATA);
            if (data == null) {
                return InteractionResultHolder.pass(stack);
            }

            switch (type) {
                case TU_VI -> {
                    data.addTuVi(value);
                    serverPlayer.sendSystemMessage(Component.translatable("message.tutien.pill_tuvi", (long) value).withStyle(ChatFormatting.GREEN));
                }
                case SPEED -> {
                    data.applyPillBuff(durationTicks, value, 0.0f, 0.0f);
                    serverPlayer.sendSystemMessage(Component.translatable("message.tutien.pill_speed", value, durationTicks / 20).withStyle(ChatFormatting.AQUA));
                }
                case HEAL_HP -> {
                    player.heal((float) value);
                    serverPlayer.sendSystemMessage(Component.translatable("message.tutien.pill_heal_hp", (float) value).withStyle(ChatFormatting.GREEN));
                }
                case HEAL_QI -> {
                    data.restoreLingQi((float) value);
                    serverPlayer.sendSystemMessage(Component.translatable("message.tutien.pill_heal_qi", (float) value).withStyle(ChatFormatting.AQUA));
                }
                case BUFF_ATTACK -> {
                    data.applyPillBuff(durationTicks, 1.0, (float) value, 0.0f);
                    data.syncAttributes(serverPlayer);
                    serverPlayer.sendSystemMessage(Component.translatable("message.tutien.pill_buff_attack", (float) value, durationTicks / 20).withStyle(ChatFormatting.RED));
                }
                case BUFF_DEFENSE -> {
                    data.applyPillBuff(durationTicks, 1.0, 0.0f, (float) value);
                    data.syncAttributes(serverPlayer);
                    serverPlayer.sendSystemMessage(Component.translatable("message.tutien.pill_buff_defense", (float) value, durationTicks / 20).withStyle(ChatFormatting.YELLOW));
                }
                case BREAKTHROUGH -> {
                    serverPlayer.sendSystemMessage(Component.translatable("message.tutien.pill_breakthrough").withStyle(ChatFormatting.GOLD));
                    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                }
                default -> {}
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5f, 1.2f);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.tutien.pill_dan_van", "★".repeat(danVan), danVan).withStyle(ChatFormatting.GOLD));
        switch (type) {
            case TU_VI -> tooltipComponents.add(Component.translatable("tooltip.tutien.pill_tuvi", (long) value).withStyle(ChatFormatting.GREEN));
            case SPEED -> tooltipComponents.add(Component.translatable("tooltip.tutien.pill_speed", value, durationTicks / 20).withStyle(ChatFormatting.AQUA));
            case BREAKTHROUGH -> tooltipComponents.add(Component.translatable("tooltip.tutien.pill_breakthrough").withStyle(ChatFormatting.GOLD));
            case HEAL_HP -> tooltipComponents.add(Component.translatable("tooltip.tutien.pill_heal_hp", (float) value).withStyle(ChatFormatting.RED));
            case HEAL_QI -> tooltipComponents.add(Component.translatable("tooltip.tutien.pill_heal_qi", (float) value).withStyle(ChatFormatting.AQUA));
            case BUFF_ATTACK -> tooltipComponents.add(Component.translatable("tooltip.tutien.pill_buff_attack", (float) value, durationTicks / 20).withStyle(ChatFormatting.RED));
            case BUFF_DEFENSE -> tooltipComponents.add(Component.translatable("tooltip.tutien.pill_buff_defense", (float) value, durationTicks / 20).withStyle(ChatFormatting.YELLOW));
            default -> {}
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
