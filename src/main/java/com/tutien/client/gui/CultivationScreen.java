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
        super(Component.literal("Bảng Trạng Thái Tu Tiên"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        Realm realm = Realm.fromId(ClientCultivationCache.realm);
        double req = realm.calculateRequiredTuVi(ClientCultivationCache.layer);
        boolean canBreakthrough = ClientCultivationCache.tuVi >= req;

        // Nút Đột phá
        this.breakthroughButton = addRenderableWidget(Button.builder(
                Component.literal("Đột Phá"),
                btn -> {
                    PacketDistributor.sendToServer(new C2SPerformBreakthroughPayload());
                    this.onClose();
                }
        ).bounds(centerX - 50, centerY + 65, 100, 20).build());

        this.breakthroughButton.active = canBreakthrough;

        // Nút đóng
        addRenderableWidget(Button.builder(
                Component.literal("Đóng"),
                btn -> this.onClose()
        ).bounds(centerX - 35, centerY + 90, 70, 18).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Background dim
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelWidth = 320;
        int panelHeight = 220;
        int left = centerX - panelWidth / 2;
        int top = centerY - panelHeight / 2;

        // Panel Window
        graphics.fill(left, top, left + panelWidth, top + panelHeight, 0xEE1A1A24);
        graphics.fill(left + 2, top + 2, left + panelWidth - 2, top + panelHeight - 2, 0xEE0F0F17);

        // Header
        graphics.drawCenteredString(this.font, "§6§l✦ BẢNG THÔNG TIN TU TIÊN ✦", centerX, top + 8, 0xFFFFFF);

        Realm currentRealm = Realm.fromId(ClientCultivationCache.realm);
        double reqTuVi = currentRealm.calculateRequiredTuVi(ClientCultivationCache.layer);
        float tuViRatio = (float) Math.max(0.0, Math.min(1.0, ClientCultivationCache.tuVi / Math.max(1.0, reqTuVi)));

        // Left Column: Cảnh giới & Tu vi
        int col1X = left + 15;
        graphics.drawString(this.font, "§eCảnh Giới Hiện Tại:", col1X, top + 25, 0xFFFFFF);
        graphics.drawString(this.font, "§6§l" + currentRealm.getDisplayName() + " - Tầng " + ClientCultivationCache.layer, col1X, top + 38, 0xFFFFFF);

        // Tu Vi Progress Bar
        graphics.drawString(this.font, "§aTiến Độ Tu Vi:", col1X, top + 55, 0xFFFFFF);
        int barW = 140;
        graphics.fill(col1X, top + 67, col1X + barW, top + 77, 0xFF333333);
        graphics.fill(col1X, top + 67, col1X + (int)(barW * tuViRatio), top + 77, 0xFF4E9F3D);
        graphics.drawString(this.font, (long)ClientCultivationCache.tuVi + " / " + (long)reqTuVi, col1X + 4, top + 68, 0xFFFFFF, false);

        // Củng cố căn cơ
        int foundationPct = (int)(ClientCultivationCache.foundation * 100);
        graphics.drawString(this.font, "§bCăn Cơ Đã Củng Cố: §f" + foundationPct + "%", col1X, top + 84, 0xFFFFFF);
        if (foundationPct < 100) {
            graphics.drawString(this.font, "§8(Hãy ngồi thiền để củng cố 100% căn cơ)", col1X, top + 95, 0xAAAAAA);
        }

        // Tông Môn & Điểm số
        graphics.drawString(this.font, "§6Tông Môn: §f" + getSectDisplayName(ClientCultivationCache.sectId), col1X, top + 112, 0xFFFFFF);
        graphics.drawString(this.font, "§eĐiểm Công Huân: §f" + ClientCultivationCache.congHuan, col1X, top + 125, 0xFFFFFF);
        graphics.drawString(this.font, "§aCống Hiến Tông Môn: §f" + ClientCultivationCache.congHien, col1X, top + 138, 0xFFFFFF);

        // Right Column: Chỉ số chiến đấu & Kỹ năng
        int col2X = left + 175;
        graphics.drawString(this.font, "§bThuộc Tính Nhân Vật:", col2X, top + 25, 0xFFFFFF);
        graphics.drawString(this.font, "§fLinh Khí: §b" + (int)ClientCultivationCache.lingQi + " / " + (int)ClientCultivationCache.maxLingQi, col2X, top + 38, 0xFFFFFF);
        graphics.drawString(this.font, "§cKhí Huyết Gia Tăng: §f+" + (int)currentRealm.getBaseHealthBonus() + " HP", col2X, top + 51, 0xFFFFFF);
        graphics.drawString(this.font, "§6Sức Tấn Công: §f+" + (int)currentRealm.getBaseAttackBonus() + " Điểm", col2X, top + 64, 0xFFFFFF);
        graphics.drawString(this.font, "§eGiáp Hộ Thể: §f+" + (int)currentRealm.getBaseArmorBonus() + " Điểm", col2X, top + 77, 0xFFFFFF);

        // Kỹ năng trang bị
        graphics.drawString(this.font, "§eÔ Kỹ Năng Đang Dùng:", col2X, top + 95, 0xFFFFFF);
        String[] keys = {"Z", "X", "V", "R"};
        for (int i = 0; i < 4; i++) {
            String sk = ClientCultivationCache.equippedSkills[i];
            String name = (sk != null && !sk.isEmpty()) ? sk : "Trống";
            graphics.drawString(this.font, "§7[" + keys[i] + "] §f" + name, col2X, top + 110 + i * 13, 0xFFFFFF);
        }

        super.render(graphics, mouseX, mouseY, partialTick);

        // Tooltip for Breakthrough Button if at Layer 9
        if (ClientCultivationCache.layer == 9 && breakthroughButton.isHovered()) {
            Realm next = currentRealm.getNext();
            graphics.renderTooltip(this.font, Component.literal("§6Đột phá lên " + next.getDisplayName() + " yêu cầu: §e" + currentRealm.getNextBreakthroughPillId()), mouseX, mouseY);
        }
    }

    private String getSectDisplayName(String sectId) {
        return switch (sectId) {
            case "thuong_van" -> "Thương Vân Tông";
            case "con_luan" -> "Côn Luân Thánh Địa";
            default -> "Tán Tu (Chưa gia nhập)";
        };
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
