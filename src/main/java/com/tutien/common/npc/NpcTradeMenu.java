package com.tutien.common.npc;

import com.tutien.common.registry.ModMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class NpcTradeMenu extends AbstractContainerMenu {
    private final int npcEntityId;
    private final TuTienNpcRole role;

    public NpcTradeMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readVarInt(), TuTienNpcRole.values()[buf.readVarInt()]);
    }

    public NpcTradeMenu(int containerId, Inventory playerInventory, int npcEntityId, TuTienNpcRole role) {
        super(ModMenuTypes.NPC_TRADE_MENU.get(), containerId);
        this.npcEntityId = npcEntityId;
        this.role = role;

        // Player Inventory
        int invLeft = 58;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, invLeft + col * 18, 120 + row * 18));
            }
        }

        // Player Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, invLeft + col * 18, 178));
        }
    }

    public int getNpcEntityId() { return npcEntityId; }
    public TuTienNpcRole getRole() { return role; }
    public List<NpcTradeOffer> getOffers() { return role.getOffers(); }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
