package com.tutien.common.array;

import com.mojang.serialization.MapCodec;
import com.tutien.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CultivationArrayBlock extends BaseEntityBlock {
    public static final MapCodec<CultivationArrayBlock> CODEC = simpleCodec(properties -> new CultivationArrayBlock());

    public CultivationArrayBlock() {
        super(Properties.of()
                .strength(2.5f, 20.0f)
                .sound(SoundType.AMETHYST)
                .lightLevel(state -> 10)
                .noOcclusion());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CultivationArrayBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntities.CULTIVATION_ARRAY.get(),
                (lvl, pos, st, be) -> be.tick(lvl, pos, st));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CultivationArrayBlockEntity array) {
                player.sendSystemMessage(Component.literal("§b[Tụ Linh Trận] " + array.getTierName() +
                        " | Bán kính: " + array.getRadius() + " ô | Tăng tốc tu luyện: §e" + array.getMultiplier() + "x"));
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CultivationArrayBlockEntity array) {
                player.sendSystemMessage(Component.literal("§b[Tụ Linh Trận] " + array.getTierName() +
                        " | Bán kính: " + array.getRadius() + " ô | Tăng tốc tu luyện: §e" + array.getMultiplier() + "x"));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§bTrận pháp ngưng tụ thiên địa linh khí."));
        tooltipComponents.add(Component.literal("§eNgười tu tiên ngồi thiền trong phạm vi trận pháp sẽ được tăng tốc tu vi vượt trội."));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
