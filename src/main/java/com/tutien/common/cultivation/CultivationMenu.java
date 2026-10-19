package com.tutien.common.cultivation;

import com.tutien.common.registry.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class CultivationMenu extends AbstractContainerMenu {
    public CultivationMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.CULTIVATION_MENU.get(), containerId);
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
