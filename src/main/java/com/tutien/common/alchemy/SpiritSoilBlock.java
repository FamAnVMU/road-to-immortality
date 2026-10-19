package com.tutien.common.alchemy;

import com.tutien.common.currency.SpiritStoneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class SpiritSoilBlock extends Block {
    private final int tier; // 1 = Hạ cấp, 2 = Trung cấp, 3 = Thượng cấp
    private final String tierName;

    public SpiritSoilBlock(int tier, String tierName) {
        super(Properties.of()
                .strength(0.8f)
                .sound(SoundType.GRAVEL));
        this.tier = tier;
        this.tierName = tierName;
    }

    public int getTier() {
        return tier;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof SpiritStoneItem stoneItem) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            if (aboveState.getBlock() instanceof CropBlock cropBlock) {
                if (!cropBlock.isMaxAge(aboveState)) {
                    if (!level.isClientSide()) {
                        int growthBoost = switch (stoneItem.getQuality()) {
                            case HA_PHAM -> 1;
                            case TRUNG_PHAM -> 3;
                            case THUONG_PHAM -> 5;
                            case THAN_PHAM -> 7;
                        };

                        int currentAge = cropBlock.getAge(aboveState);
                        int newAge = Math.min(cropBlock.getMaxAge(), currentAge + growthBoost);
                        level.setBlock(abovePos, cropBlock.getStateForAge(newAge), 2);

                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }

                        if (level instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                    abovePos.getX() + 0.5, abovePos.getY() + 0.5, abovePos.getZ() + 0.5,
                                    15, 0.3, 0.3, 0.3, 0.0);
                        }

                        level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                        player.displayClientMessage(Component.literal("§a[Linh Điền] Bón linh thạch thành công! Linh dược đã hấp thụ linh khí phát triển!"), true);
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide());
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§7Đẳng cấp đất: §e" + tierName + " §7(Cấp " + tier + ")"));
        tooltipComponents.add(Component.literal("§2Đất chứa nồng độ linh khí thích hợp để trồng linh dược."));
        tooltipComponents.add(Component.literal("§eCó thể dùng linh thạch chuột phải để bón thúc sinh trưởng linh dược bên trên!"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
