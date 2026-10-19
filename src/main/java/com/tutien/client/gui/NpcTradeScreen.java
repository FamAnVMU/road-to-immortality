package com.tutien.client.gui;

import com.tutien.common.cultivation.Realm;
import com.tutien.common.network.C2SNpcTradePayload;
import com.tutien.common.npc.NpcTradeMenu;
import com.tutien.common.npc.NpcTradeOffer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class NpcTradeScreen extends AbstractContainerScreen<NpcTradeMenu> {
    public NpcTradeScreen(NpcTradeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 240;
        this.imageHeight = 220;
    }

    @Override
    protected void init() {
        super.init();
        int left = this.leftPos;
        int top = this.topPos;

        List<NpcTradeOffer> offers = menu.getOffers();
        int maxOffers = Math.min(4, offers.size());

        for (int i = 0; i < maxOffers; i++) {
            final int index = i;
            addRenderableWidget(Button.builder(
                    Component.translatable("gui.tutien.trade.buy"),
                    btn -> PacketDistributor.sendToServer(new C2SNpcTradePayload(menu.getNpcEntityId(), index))
            ).bounds(left + 185, top + 26 + i * 22, 42, 16).build());
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;

        graphics.fill(left, top, left + this.imageWidth, top + this.imageHeight, 0xEE1A1A26);
        graphics.fill(left + 2, top + 2, left + this.imageWidth - 2, top + this.imageHeight - 2, 0xEE101018);

        // Trade rows
        List<NpcTradeOffer> offers = menu.getOffers();
        int maxOffers = Math.min(4, offers.size());

        for (int i = 0; i < maxOffers; i++) {
            int rowY = top + 24 + i * 22;
            graphics.fill(left + 10, rowY, left + 230, rowY + 20, 0xFF242436);
            graphics.fill(left + 11, rowY + 1, left + 229, rowY + 19, 0xFF181824);

            NpcTradeOffer offer = offers.get(i);
            ItemStack item = offer.getOutput();

            // Render output item
            graphics.renderItem(item, left + 14, rowY + 2);
            graphics.renderItemDecorations(this.font, item, left + 14, rowY + 2);

            // Item Name & Cost Text
            String name = item.getHoverName().getString();
            if (name.length() > 14) name = name.substring(0, 14) + "...";
            graphics.drawString(this.font, name, left + 35, rowY + 3, 0xFFFFFF, false);

            String costStr = "";
            if (offer.getSpiritStoneCost() > 0) costStr += Component.translatable("gui.tutien.trade.cost_spirit_stone", offer.getSpiritStoneCost()).getString() + " ";
            if (offer.getCongHuanCost() > 0) costStr += Component.translatable("gui.tutien.trade.cost_merit", offer.getCongHuanCost()).getString() + " ";
            if (offer.getCongHienCost() > 0) costStr += Component.translatable("gui.tutien.trade.cost_sect_contrib", offer.getCongHienCost()).getString() + " ";
            if (offer.getMinRealm() > 0) {
                costStr += "(" + Realm.fromId(offer.getMinRealm()).getDisplayNameComponent().getString() + ")";
            }

            graphics.drawString(this.font, costStr, left + 35, rowY + 11, 0xAAAAAA, false);
        }

        // Inventory slots background
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                drawSlot(graphics, left + 38 + c * 18, top + 119 + r * 18);
            }
        }
        for (int c = 0; c < 9; c++) {
            drawSlot(graphics, left + 38 + c * 18, top + 177);
        }
    }

    private void drawSlot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, 0xFF353545);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF1C1C26);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, Component.literal("§6✦ ").append(menu.getRole().getTitleComponent()).append(" ✦"), 10, 8, 0xFFFFFF, false);
        graphics.drawString(this.font, Component.translatable("container.inventory"), 38, 108, 0x888888, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Tooltip for item hovering
        int left = this.leftPos;
        int top = this.topPos;
        List<NpcTradeOffer> offers = menu.getOffers();
        for (int i = 0; i < Math.min(4, offers.size()); i++) {
            int rowY = top + 24 + i * 22;
            if (mouseX >= left + 14 && mouseX <= left + 32 && mouseY >= rowY + 2 && mouseY <= rowY + 20) {
                graphics.renderTooltip(this.font, offers.get(i).getOutput(), mouseX, mouseY);
            }
        }
    }
}
