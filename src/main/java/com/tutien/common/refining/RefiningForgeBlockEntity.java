package com.tutien.common.refining;

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

public class RefiningForgeBlockEntity extends BlockEntity implements MenuProvider {
    // 0: Blueprint, 1,2,3: Materials, 4: Catalyst, 5: Result
    private final NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
    private int tier = 1; // 1 = Hoàng, 2 = Huyền, 3 = Địa, 4 = Thiên
    private int progress = 0;
    private int maxProgress = 120;
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

    public RefiningForgeBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.REFINING_FORGE.get(), pos, blockState);
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = Math.max(1, Math.min(4, tier));
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

        RefiningRecipe.RefiningInput input = new RefiningRecipe.RefiningInput(
                items.get(0), items.get(1), items.get(2), items.get(3), items.get(4), tier
        );

        Optional<RecipeHolder<RefiningRecipe>> match = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.REFINING_TYPE.get(), input, level);

        if (match.isPresent()) {
            RefiningRecipe recipe = match.get().value();
            this.maxProgress = recipe.getCraftingTime();

            ItemStack resultItem = recipe.assemble(input, level.registryAccess());
            ItemStack currentOutput = items.get(5);

            if (canInsertResult(currentOutput, resultItem)) {
                progress++;
                if (progress >= maxProgress) {
                    progress = 0;
                    craftItem(resultItem);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.LAVA,
                                pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5,
                                12, 0.25, 0.2, 0.25, 0.05);
                    }
                    level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
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
        // Blueprint is kept or consumed based on design. Here blueprint is kept so master can reuse it!
        for (int i = 1; i <= 4; i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                stack.shrink(1);
            }
        }

        ItemStack currentOut = items.get(5);
        if (currentOut.isEmpty()) {
            items.set(5, result.copy());
        } else {
            currentOut.grow(result.getCount());
        }
    }

    public boolean upgradeTier(Player player) {
        if (tier >= 4) {
            player.sendSystemMessage(Component.literal("§6[Lò Luyện Khí] Lò đã đạt cấp tối đa (Cấp 4 - Thiên Cấp)!"));
            return false;
        }
        int nextTier = tier + 1;
        this.tier = nextTier;
        setChanged();
        player.sendSystemMessage(Component.literal("§a[Lò Luyện Khí] Nâng cấp lò luyện khí thành công lên Cấp " + nextTier + "!"));
        return true;
    }

    @Override
    public Component getDisplayName() {
        String tierStr = switch (tier) {
            case 1 -> "Hoàng Cấp";
            case 2 -> "Huyền Cấp";
            case 3 -> "Địa Cấp";
            case 4 -> "Thiên Cấp";
            default -> "Cấp " + tier;
        };
        return Component.literal("Lò Luyện Khí - " + tierStr);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new RefiningForgeMenu(containerId, playerInventory, items, dataAccess, this);
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
        maxProgress = tag.contains("MaxProgress") ? tag.getInt("MaxProgress") : 120;
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
