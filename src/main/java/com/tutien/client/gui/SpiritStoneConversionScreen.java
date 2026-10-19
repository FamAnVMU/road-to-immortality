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
        this.imageHeight = 210;
    }

    @Override
    protected void init() {
        super.init();
        int left = this.leftPos;
        int top = this.topPos;

        // Button 1: 1000 Hạ -> 1 Trung
        addRenderableWidget(Button.builder(Component.literal("1.000 Hạ ➔ 1 Trung Phẩm"),
                btn -> PacketDistributor.sendToServer(new C2SSpiritStoneConvertPayload(0)))
                .bounds(left + 15, top + 25, 190, 16).build());

        // Button 2: 1000 Trung -> 1 Thượng
        addRenderableWidget(Button.builder(Component.literal("1.000 Trung ➔ 1 Thượng Phẩm"),
                btn -> PacketDistributor.sendToServer(new C2SSpiritStoneConvertPayload(1)))
                .bounds(left + 15, top + 44, 190, 16).build());

        // Button 3: 1000 Thượng -> 1 Thần
        addRenderableWidget(Button.builder(Component.literal("1.000 Thượng ➔ 1 THẦN PHẨM"),
                btn -> PacketDistributor.sendToServer(new C2SSpiritStoneConvertPayload(2)))
                .bounds(left + 15, top + 63, 190, 16).build());

        // Button 4: 1 Thần -> 1000 Thượng
        addRenderableWidget(Button.builder(Component.literal("Phân rã 1 Thần ➔ 1.000 Thượng"),
                btn -> PacketDistributor.sendToServer(new C2SSpiritStoneConvertPayload(3)))
                .bounds(left + 15, top + 82, 190, 16).build());

        // Button 5: 1 Thượng -> 1000 Trung
        addRenderableWidget(Button.builder(Component.literal("Phân rã 1 Thượng ➔ 1.000 Trung"),
                btn -> PacketDistributor.sendToServer(new C2SSpiritStoneConvertPayload(4)))
                .bounds(left + 15, top + 101, 190, 16).build());

        // Button 6: 1 Trung -> 1000 Hạ
        addRenderableWidget(Button.builder(Component.literal("Phân rã 1 Trung ➔ 1.000 Hạ"),
                btn -> PacketDistributor.sendToServer(new C2SSpiritStoneConvertPayload(5)))
                .bounds(left + 15, top + 120, 190, 16).build());

        // Button 7: Tối ưu hóa linh thạch
        addRenderableWidget(Button.builder(Component.literal("§6Tối Ưu Hóa Túi Linh Thạch"),
                btn -> PacketDistributor.sendToServer(new C2SSpiritStoneConvertPayload(6)))
                .bounds(left + 15, top + 139, 190, 18).build());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // Draw dark themed background panel
        graphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xEE1E1E28);
        graphics.fill(this.leftPos + 2, this.topPos + 2, this.leftPos + this.imageWidth - 2, this.topPos + this.imageHeight - 2, 0xEE111118);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, "§6✦ QUY ĐỔI LINH THẠCH ✦", 40, 8, 0xFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Display total player wealth
        if (minecraft != null && minecraft.player != null) {
            long total = SpiritStoneHelper.getTotalValue(minecraft.player);
            graphics.drawString(this.font, "§eTổng tài sản: §f" + total + " §7(Hạ phẩm)", this.leftPos + 15, this.topPos + 160, 0xFFFFFF);
        }
    }
}
