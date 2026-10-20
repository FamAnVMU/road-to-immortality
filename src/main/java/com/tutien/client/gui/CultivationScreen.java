package com.tutien.client.gui;

import com.tutien.client.hud.ClientCultivationCache;
import com.tutien.common.cultivation.Realm;
import com.tutien.common.network.C2SPerformBreakthroughPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class CultivationScreen extends Screen {
    private Button breakthroughButton;

    public CultivationScreen() {
        super(Component.translatable("gui.tutien.cultivation.title"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        Realm realm = Realm.fromId(ClientCultivationCache.realm);
        double req = realm.calculateRequiredTuVi(ClientCultivationCache.layer);
        boolean canBreakthrough = ClientCultivationCache.tuVi >= req;

        // Side-by-side bottom buttons to prevent vertical text collisions
        this.breakthroughButton = addRenderableWidget(Button.builder(
                Component.translatable("gui.tutien.cultivation.breakthrough"),
                btn -> {
                    PacketDistributor.sendToServer(new C2SPerformBreakthroughPayload());
                    this.onClose();
                }
        ).bounds(centerX - 105, centerY + 68, 100, 20).build());

        this.breakthroughButton.active = canBreakthrough;

        // Nút đóng
        addRenderableWidget(Button.builder(
                Component.translatable("gui.tutien.close"),
                btn -> this.onClose()
        ).bounds(centerX + 5, centerY + 68, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Render world background dimming first
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelWidth = 320;
        int panelHeight = 220;
        int left = centerX - panelWidth / 2;
        int top = centerY - panelHeight / 2;

        // Solid, crisp Cultivation Panel with Golden Frame (No Blur)
        graphics.fill(left - 2, top - 2, left + panelWidth + 2, top + panelHeight + 2, 0xFF05050A);
        graphics.fill(left, top, left + panelWidth, top + panelHeight, 0xFFD4AF37); // Golden border
        graphics.fill(left + 2, top + 2, left + panelWidth - 2, top + panelHeight - 2, 0xFF14141E); // Main dark slate
        graphics.fill(left + 2, top + 2, left + panelWidth - 2, top + 22, 0xFF1E1E2E); // Header banner

        // Header Title
        graphics.drawCenteredString(this.font, Component.translatable("gui.tutien.cultivation.header"), centerX, top + 7, 0xFFD4AF37);

        Realm currentRealm = Realm.fromId(ClientCultivationCache.realm);
        double reqTuVi = currentRealm.calculateRequiredTuVi(ClientCultivationCache.layer);
        float tuViRatio = (float) Math.max(0.0, Math.min(1.0, ClientCultivationCache.tuVi / Math.max(1.0, reqTuVi)));

        // Left Column: Cảnh giới & Tu vi
        int col1X = left + 15;
        graphics.drawString(this.font, Component.translatable("gui.tutien.cultivation.current_realm"), col1X, top + 28, 0xCCCCCC);
        graphics.drawString(this.font, Component.literal("§6§l").append(currentRealm.getDisplayNameComponent()).append(" - ").append(Component.translatable("tutien.realm.layer", ClientCultivationCache.layer)), col1X, top + 40, 0xFFFFFF);

        // Tu Vi Progress Bar
        graphics.drawString(this.font, Component.translatable("gui.tutien.cultivation.tuvi_progress"), col1X, top + 55, 0xCCCCCC);
        int barW = 140;
        graphics.fill(col1X, top + 67, col1X + barW, top + 77, 0xFF333333);
        graphics.fill(col1X, top + 67, col1X + (int)(barW * tuViRatio), top + 77, 0xFF4E9F3D);
        graphics.drawString(this.font, (long)ClientCultivationCache.tuVi + " / " + (long)reqTuVi, col1X + 4, top + 68, 0xFFFFFF, false);

        // Củng cố căn cơ
        int foundationPct = (int)(ClientCultivationCache.foundation * 100);
        graphics.drawString(this.font, Component.translatable("gui.tutien.cultivation.foundation_consolidation", foundationPct), col1X, top + 84, 0xFFFFFF);
        if (foundationPct < 100) {
            graphics.drawString(this.font, Component.translatable("gui.tutien.cultivation.foundation_hint"), col1X, top + 95, 0xAAAAAA);
        }

        // Tông Môn & Điểm số
        graphics.drawString(this.font, Component.translatable("gui.tutien.cultivation.sect").append(" ").append(getSectDisplayName(ClientCultivationCache.sectId)), col1X, top + 112, 0xFFFFFF);
        graphics.drawString(this.font, Component.translatable("gui.tutien.cultivation.merit", ClientCultivationCache.congHuan), col1X, top + 125, 0xFFFFFF);
        graphics.drawString(this.font, Component.translatable("gui.tutien.cultivation.sect_contrib", ClientCultivationCache.congHien), col1X, top + 138, 0xFFFFFF);

        // Right Column: Chỉ số chiến đấu & Kỹ năng
        int col2X = left + 175;
        graphics.drawString(this.font, Component.translatable("gui.tutien.cultivation.character_attributes"), col2X, top + 28, 0xCCCCCC);
        graphics.drawString(this.font, Component.translatable("gui.tutien.cultivation.lingqi", (int)ClientCultivationCache.lingQi, (int)ClientCultivationCache.maxLingQi), col2X, top + 40, 0x00E5FF);
        graphics.drawString(this.font, Component.translatable("gui.tutien.cultivation.bonus_hp", (int)currentRealm.getBaseHealthBonus()), col2X, top + 52, 0xFF5555);
        graphics.drawString(this.font, Component.translatable("gui.tutien.cultivation.bonus_attack", (int)currentRealm.getBaseAttackBonus()), col2X, top + 64, 0xFFAA00);
        graphics.drawString(this.font, Component.translatable("gui.tutien.cultivation.bonus_armor", (int)currentRealm.getBaseArmorBonus()), col2X, top + 76, 0x55FF55);

        // Kỹ năng trang bị
        graphics.drawString(this.font, Component.translatable("gui.tutien.cultivation.equipped_skills"), col2X, top + 95, 0xCCCCCC);
        String[] keys = {"Z", "X", "V", "R"};
        for (int i = 0; i < 4; i++) {
            String sk = ClientCultivationCache.equippedSkills[i];
            Component skillName = (sk != null && !sk.isEmpty()) ? Component.translatable("tutien.skill." + sk) : Component.translatable("gui.tutien.cultivation.skill_empty");
            graphics.drawString(this.font, Component.literal("§7[" + keys[i] + "] §f").append(skillName), col2X, top + 110 + i * 13, 0xFFFFFF);
        }

        // Render widgets (buttons) directly to avoid super.render invoking renderBackground again
        for (net.minecraft.client.gui.components.Renderable renderable : this.renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTick);
        }

        // Tooltip for Breakthrough Button if at Layer 9
        if (ClientCultivationCache.layer == 9 && breakthroughButton.isHovered()) {
            Realm next = currentRealm.getNext();
            String pillId = currentRealm.getNextBreakthroughPillId();
            String pillKey = pillId.contains(":") ? "item." + pillId.replace(':', '.') : "item.tutien." + pillId;
            graphics.renderTooltip(this.font, Component.translatable("gui.tutien.cultivation.breakthrough_req_tooltip", next.getDisplayNameComponent(), Component.translatable(pillKey)), mouseX, mouseY);
        }
    }

    private Component getSectDisplayName(String sectId) {
        return switch (sectId) {
            case "thuong_van" -> Component.translatable("tutien.sect.thuong_van");
            case "con_luan" -> Component.translatable("tutien.sect.con_luan");
            default -> Component.translatable("tutien.sect.none");
        };
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
