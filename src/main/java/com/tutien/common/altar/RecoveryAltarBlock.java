package com.tutien.common.altar;

import com.mojang.serialization.MapCodec;
import com.tutien.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class RecoveryAltarBlock extends BaseEntityBlock {
    public static final MapCodec<RecoveryAltarBlock> CODEC = simpleCodec(properties -> new RecoveryAltarBlock());

    public RecoveryAltarBlock() {
        super(Properties.of()
                .strength(4.0f, 100.0f)
                .sound(SoundType.STONE)
                .lightLevel(state -> 12)
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
        return new RecoveryAltarBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RecoveryAltarBlockEntity altar) {
                altar.activateAltar(player);
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RecoveryAltarBlockEntity altar) {
                altar.activateAltar(player);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RecoveryAltarBlockEntity altar && altar.isFixedInWorld()) {
            return 0.0f; // Tế đàn tại công trình thế giới không thể bị phá hủy
        }
        return super.getDestroyProgress(state, player, level, pos);
    }
}
