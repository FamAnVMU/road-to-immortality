package com.tutien.common.alchemy;

import com.tutien.common.registry.ModBlockEntities;
import com.tutien.common.registry.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AlchemyCauldronBlockEntity extends BlockEntity implements MenuProvider {
    private final NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
    private int tier = 1; // 1 to 9
    private int progress = 0;
    private int maxProgress = 100;
    private boolean isFixedInWorld = false;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> tier;
                case 1 -> progress;
                case 2 -> maxProgress;
                case 3 -> isFixedInWorld ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> tier = value;
                case 1 -> progress = value;
                case 2 -> maxProgress = value;
                case 3 -> isFixedInWorld = (value == 1);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public AlchemyCauldronBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ALCHEMY_CAULDRON.get(), pos, blockState);
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = Math.max(1, Math.min(9, tier));
        setChanged();
    }

    public boolean isFixedInWorld() {
        return isFixedInWorld;
    }

    public void setFixedInWorld(boolean fixed) {
        this.isFixedInWorld = fixed;
        setChanged();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        AlchemyRecipe.AlchemyInput input = new AlchemyRecipe.AlchemyInput(
                items.get(0), items.get(1), items.get(2), items.get(3), tier
        );

        Optional<RecipeHolder<AlchemyRecipe>> match = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.ALCHEMY_TYPE.get(), input, level);

        if (match.isPresent()) {
            AlchemyRecipe recipe = match.get().value();
            this.maxProgress = recipe.getCraftingTime();

            ItemStack resultItem = recipe.assemble(input, level.registryAccess());
            ItemStack currentOutput = items.get(4);

            if (canInsertResult(currentOutput, resultItem)) {
                progress++;
                if (progress >= maxProgress) {
                    progress = 0;
                    craftItem(resultItem);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                                10, 0.2, 0.3, 0.2, 0.02);
                        serverLevel.sendParticles(ParticleTypes.FLAME,
                                pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5,
                                8, 0.15, 0.1, 0.15, 0.01);
                    }
                    level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                setChanged();
                return;
            }
        }

        if (progress > 0) {
            progress = Math.max(0, progress - 2);
            setChanged();
        }
    }

    private boolean canInsertResult(ItemStack current, ItemStack toInsert) {
        if (current.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(current, toInsert)) return false;
        return current.getCount() + toInsert.getCount() <= current.getMaxStackSize();
    }

    private void craftItem(ItemStack result) {
        for (int i = 0; i < 4; i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                stack.shrink(1);
            }
        }

        ItemStack currentOut = items.get(4);
        if (currentOut.isEmpty()) {
            items.set(4, result.copy());
        } else {
            currentOut.grow(result.getCount());
        }
    }

    /**
     * Nâng cấp lò luyện đan tuần tự: Cấp 1 -> Cấp 2 -> Cấp 3 ... -> Cấp 9.
     * Không cho phép nâng cấp nhảy cóc.
     */
    public boolean upgradeTier(Player player) {
        if (tier >= 9) {
            player.sendSystemMessage(Component.translatable("tutien.message.cauldron_max_tier"));
            return false;
        }
        int nextTier = tier + 1;
        this.tier = nextTier;
        setChanged();
        player.sendSystemMessage(Component.translatable("tutien.message.cauldron_upgrade_success", nextTier));
        return true;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.tutien.alchemy_cauldron_tier", tier);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AlchemyCauldronMenu(containerId, playerInventory, items, dataAccess, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt("Tier", tier);
        tag.putInt("Progress", progress);
        tag.putInt("MaxProgress", maxProgress);
        tag.putBoolean("FixedInWorld", isFixedInWorld);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, items, registries);
        tier = Math.max(1, tag.getInt("Tier"));
        progress = tag.getInt("Progress");
        maxProgress = tag.contains("MaxProgress") ? tag.getInt("MaxProgress") : 100;
        isFixedInWorld = tag.getBoolean("FixedInWorld");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
