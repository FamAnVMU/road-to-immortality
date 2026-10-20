package com.tutien.client.gui;

import com.tutien.common.alchemy.AlchemyCauldronMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AlchemyCauldronScreen extends AbstractContainerScreen<AlchemyCauldronMenu> {
    public AlchemyCauldronScreen(AlchemyCauldronMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;

        // Custom stylized cauldron background
        graphics.fill(left, top, left + this.imageWidth, top + this.imageHeight, 0xEE222230);
        graphics.fill(left + 2, top + 2, left + this.imageWidth - 2, top + this.imageHeight - 2, 0xEE14141E);

        // Slot boxes
        drawSlot(graphics, left + 29, top + 19);
        drawSlot(graphics, left + 49, top + 19);
        drawSlot(graphics, left + 69, top + 19);

        // Catalyst slot box
        drawSlot(graphics, left + 49, top + 49);

        // Output slot box (larger or highlighted)
        graphics.fill(left + 128, top + 33, left + 148, top + 53, 0xFF4E9F3D);
        drawSlot(graphics, left + 129, top + 34);

        // Progress Arrow
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        int arrowWidth = 24;
        int filled = maxProgress > 0 ? (progress * arrowWidth / maxProgress) : 0;

        int arrowX = left + 95;
        int arrowY = top + 38;
        graphics.fill(arrowX, arrowY, arrowX + arrowWidth, arrowY + 6, 0xFF333333);
        if (filled > 0) {
            graphics.fill(arrowX, arrowY, arrowX + filled, arrowY + 6, 0xFFFFCC00);
        }

        // Player inventory slot boxes
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
        graphics.fill(x, y, x + 18, y + 18, 0xFF373748);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF1E1E28);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, Component.translatable("container.tutien.alchemy_cauldron_tier", menu.getTier()), 8, 6, 0xFFFFFF, false);
        if (menu.isFixed()) {
            Component fixedComp = Component.translatable("gui.tutien.fixed_facility");
            int fixedW = this.font.width(fixedComp);
            graphics.drawString(this.font, fixedComp, this.imageWidth - 8 - fixedW, 6, 0xAAAAAA, false);
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
