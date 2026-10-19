package com.tutien.common.refining;

import com.tutien.common.registry.ModMenuTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RefiningForgeMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;
    private final RefiningForgeBlockEntity blockEntity;

    public RefiningForgeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(6), new SimpleContainerData(4), null);
    }

    public RefiningForgeMenu(int containerId, Inventory playerInventory, Container container, ContainerData data, RefiningForgeBlockEntity blockEntity) {
        super(ModMenuTypes.REFINING_FORGE_MENU.get(), containerId);
        checkContainerSize(container, 6);
        checkContainerDataCount(data, 4);
        this.container = container;
        this.data = data;
        this.blockEntity = blockEntity;

        addDataSlots(data);

        // Blueprint Slot: 0
        this.addSlot(new Slot(container, 0, 18, 35));

        // Material Slots: 1, 2, 3
        this.addSlot(new Slot(container, 1, 46, 20));
        this.addSlot(new Slot(container, 2, 66, 20));
        this.addSlot(new Slot(container, 3, 86, 20));

        // Catalyst Slot: 4
        this.addSlot(new Slot(container, 4, 66, 50));

        // Result Slot: 5 (Cannot insert into result slot)
        this.addSlot(new Slot(container, 5, 136, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // Player Inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public RefiningForgeMenu(int containerId, Inventory playerInventory, NonNullList<ItemStack> items, ContainerData dataAccess, RefiningForgeBlockEntity blockEntity) {
        this(containerId, playerInventory, new SimpleContainer(items.toArray(new ItemStack[0])) {
            @Override
            public void setChanged() {
                super.setChanged();
                for (int i = 0; i < 6; i++) {
                    items.set(i, this.getItem(i));
                }
                if (blockEntity != null) blockEntity.setChanged();
            }
        }, dataAccess, blockEntity);
    }

    public int getTier() {
        return data.get(0);
    }

    public int getProgress() {
        return data.get(1);
    }

    public int getMaxProgress() {
        return data.get(2);
    }

    public boolean isFixed() {
        return data.get(3) == 1;
    }

    public RefiningForgeBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index == 5) { // Result slot
                if (!this.moveItemStackTo(stackInSlot, 6, 42, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemstack);
            } else if (index < 5) { // Input slots
                if (!this.moveItemStackTo(stackInSlot, 6, 42, false)) {
                    return ItemStack.EMPTY;
                }
            } else { // Player inventory
                if (!this.moveItemStackTo(stackInSlot, 0, 5, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }
        return itemstack;
    }
}
