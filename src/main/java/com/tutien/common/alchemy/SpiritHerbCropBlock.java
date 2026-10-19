package com.tutien.common.alchemy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class SpiritHerbCropBlock extends CropBlock {
    private final Supplier<? extends ItemLike> seedSupplier;
    private final int minSoilTier;

    public SpiritHerbCropBlock(Supplier<? extends ItemLike> seedSupplier, int minSoilTier) {
        super(BlockBehaviour.Properties.of()
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP));
        this.seedSupplier = seedSupplier;
        this.minSoilTier = minSoilTier;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seedSupplier.get();
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getBlock() instanceof SpiritSoilBlock soil) {
            return soil.getTier() >= minSoilTier;
        }
        return state.is(Blocks.FARMLAND);
    }
}
