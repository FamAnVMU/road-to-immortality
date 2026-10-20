package com.tutien.client.gui;

import com.tutien.common.currency.SpiritStoneConversionMenu;
import com.tutien.common.currency.SpiritStoneHelper;
import com.tutien.common.network.C2SSpiritStoneConvertPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class SpiritStoneConversionScreen extends AbstractContainerScreen<SpiritStoneConversionMenu> {
    public SpiritStoneConversionScreen(SpiritStoneConversionMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 220;
        this.imageHeight = 206;
    }

    @Override
    protected void init() {
        super.init();
        int left = this.leftPos;
        int top = this.topPos;

        // Column 1 (Left): Upgrade conversions
        addRenderableWidget(Button.builder(Component.translatable("gui.tutien.convert.low_to_mid"),
                btn -> PacketDistributor.sendToServer(new C2SSpiritStoneConvertPayload(0)))
                .bounds(left + 10, top + 22, 98, 18).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.tutien.convert.mid_to_high"),
                btn -> PacketDistributor.sendToServer(new C2SSpiritStoneConvertPayload(1)))
                .bounds(left + 10, top + 42, 98, 18).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.tutien.convert.high_to_divine"),
                btn -> PacketDistributor.sendToServer(new C2SSpiritStoneConvertPayload(2)))
                .bounds(left + 10, top + 62, 98, 18).build());

        // Column 2 (Right): Downgrade conversions
        addRenderableWidget(Button.builder(Component.translatable("gui.tutien.convert.degrade_divine"),
                btn -> PacketDistributor.sendToServer(new C2SSpiritStoneConvertPayload(3)))
                .bounds(left + 112, top + 22, 98, 18).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.tutien.convert.degrade_high"),
                btn -> PacketDistributor.sendToServer(new C2SSpiritStoneConvertPayload(4)))
                .bounds(left + 112, top + 42, 98, 18).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.tutien.convert.degrade_mid"),
                btn -> PacketDistributor.sendToServer(new C2SSpiritStoneConvertPayload(5)))
                .bounds(left + 112, top + 62, 98, 18).build());

        // Button 7: Tối ưu hóa linh thạch (Full width)
        addRenderableWidget(Button.builder(Component.translatable("gui.tutien.convert.optimize"),
                btn -> PacketDistributor.sendToServer(new C2SSpiritStoneConvertPayload(6)))
                .bounds(left + 10, top + 83, 200, 18).build());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;

        // Draw dark themed background panel
        graphics.fill(left, top, left + this.imageWidth, top + this.imageHeight, 0xEE1E1E28);
        graphics.fill(left + 2, top + 2, left + this.imageWidth - 2, top + this.imageHeight - 2, 0xEE111118);

        // Player Inventory slot boxes
        int invLeft = left + 28;
        int invTop = top + 117;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                drawSlot(graphics, invLeft + c * 18, invTop + r * 18);
            }
        }
        for (int c = 0; c < 9; c++) {
            drawSlot(graphics, invLeft + c * 18, invTop + 58);
        }
    }

    private void drawSlot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, 0xFF353545);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF1C1C26);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(this.font, Component.translatable("gui.tutien.convert.title"), this.imageWidth / 2, 7, 0xFFD4AF37);
        graphics.drawString(this.font, Component.translatable("container.inventory"), 28, 106, 0x888888, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Display total player wealth
        if (minecraft != null && minecraft.player != null) {
            long total = SpiritStoneHelper.getTotalValue(minecraft.player);
            graphics.drawCenteredString(this.font, Component.translatable("gui.tutien.convert.total_wealth", total), this.leftPos + this.imageWidth / 2, this.topPos + 104, 0xFFFFAA);
        }
    }
}
