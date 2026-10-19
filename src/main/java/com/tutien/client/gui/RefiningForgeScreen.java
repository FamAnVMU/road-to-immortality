package com.tutien.client.gui;

import com.tutien.common.refining.RefiningForgeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class RefiningForgeScreen extends AbstractContainerScreen<RefiningForgeMenu> {
    public RefiningForgeScreen(RefiningForgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;

        graphics.fill(left, top, left + this.imageWidth, top + this.imageHeight, 0xEE2A2020);
        graphics.fill(left + 2, top + 2, left + this.imageWidth - 2, top + this.imageHeight - 2, 0xEE1A1212);

        // Blueprint Slot (18, 35)
        drawSlot(graphics, left + 17, top + 34);

        // Material Slots: 1, 2, 3 (46, 20), (66, 20), (86, 20)
        drawSlot(graphics, left + 45, top + 19);
        drawSlot(graphics, left + 65, top + 19);
        drawSlot(graphics, left + 85, top + 19);

        // Catalyst Slot (66, 50)
        drawSlot(graphics, left + 65, top + 49);

        // Result Slot (136, 35)
        graphics.fill(left + 134, top + 33, left + 154, top + 53, 0xFFE84545);
        drawSlot(graphics, left + 135, top + 34);

        // Progress Arrow
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        int arrowWidth = 24;
        int filled = maxProgress > 0 ? (progress * arrowWidth / maxProgress) : 0;

        int arrowX = left + 105;
        int arrowY = top + 38;
        graphics.fill(arrowX, arrowY, arrowX + arrowWidth, arrowY + 6, 0xFF333333);
        if (filled > 0) {
            graphics.fill(arrowX, arrowY, arrowX + filled, arrowY + 6, 0xFFFF5500);
        }

        // Inventory
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                drawSlot(graphics, left + 7 + c * 18, top + 83 + r * 18);
            }
        }
        for (int c = 0; c < 9; c++) {
            drawSlot(graphics, left + 7 + c * 18, top + 141);
        }
    }

    private void drawSlot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, 0xFF4A3434);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF281C1C);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String tierKey = switch (menu.getTier()) {
            case 1 -> "tutien.tier.yellow";
            case 2 -> "tutien.tier.mystic";
            case 3 -> "tutien.tier.earth";
            case 4 -> "tutien.tier.heaven";
            default -> "tutien.tier.unknown";
        };
        graphics.drawString(this.font, Component.translatable("container.tutien.refining_forge_tier", Component.translatable(tierKey)), 8, 6, 0xFFFFFF, false);
        if (menu.isFixed()) {
            graphics.drawString(this.font, Component.translatable("gui.tutien.fixed_facility"), 120, 6, 0xAAAAAA, false);
        }
        graphics.drawString(this.font, Component.translatable("container.inventory"), 8, 72, 0x888888, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
