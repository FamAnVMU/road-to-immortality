package com.tutien.client.hud;

import com.tutien.TuTienMod;
import com.tutien.common.cultivation.Realm;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = TuTienMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class CultivationHudOverlay {
    public static final ResourceLocation OVERLAY_ID =
            ResourceLocation.fromNamespaceAndPath(TuTienMod.MOD_ID, "cultivation_hud");

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(OVERLAY_ID, CultivationHudOverlay::render);
    }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // 1. LingQi Bar (Bottom Left or Top Left)
        int qiX = 10;
        int qiY = 10;
        int qiWidth = 110;
        int qiHeight = 12;

        float qiRatio = Math.max(0.0f, Math.min(1.0f, ClientCultivationCache.lingQi / Math.max(1.0f, ClientCultivationCache.maxLingQi)));
        int fillWidth = (int) (qiWidth * qiRatio);

        // Background & Border
        graphics.fill(qiX - 1, qiY - 1, qiX + qiWidth + 1, qiY + qiHeight + 1, 0xAA000000);
        graphics.fill(qiX, qiY, qiX + qiWidth, qiY + qiHeight, 0xFF1A1A2E);
        // LingQi Fill (Deep Cyan / Blue)
        graphics.fill(qiX, qiY, qiX + fillWidth, qiY + qiHeight, 0xFF00ADB5);

        String qiText = "Linh Khí: " + (int) ClientCultivationCache.lingQi + " / " + (int) ClientCultivationCache.maxLingQi;
        graphics.drawString(mc.font, qiText, qiX + 4, qiY + 2, 0xFFFFFF, true);

        // 2. Cultivation & Meditation HUD (Center Top when meditating or active)
        if (ClientCultivationCache.isMeditating) {
            int centerX = screenWidth / 2;
            int medY = 15;
            int boxWidth = 200;
            int boxHeight = 52;
            int boxX = centerX - boxWidth / 2;

            graphics.fill(boxX - 2, medY - 2, boxX + boxWidth + 2, medY + boxHeight + 2, 0xCC000000);
            graphics.fill(boxX, medY, boxX + boxWidth, medY + boxHeight, 0xDD12121E);

            Realm currentRealm = Realm.fromId(ClientCultivationCache.realm);
            String title = "§6§l[" + currentRealm.getDisplayName() + " - Tầng " + ClientCultivationCache.layer + "]";
            graphics.drawCenteredString(mc.font, title, centerX, medY + 4, 0xFFFFFF);

            // Tu Vi Bar
            double reqTuVi = currentRealm.calculateRequiredTuVi(ClientCultivationCache.layer);
            float tuViRatio = (float) Math.max(0.0, Math.min(1.0, ClientCultivationCache.tuVi / Math.max(1.0, reqTuVi)));
            int barWidth = 180;
            int barX = centerX - barWidth / 2;
            int barY = medY + 18;

            graphics.fill(barX, barY, barX + barWidth, barY + 8, 0xFF333333);
            graphics.fill(barX, barY, barX + (int) (barWidth * tuViRatio), barY + 8, 0xFF4E9F3D);

            String tuViText = "Tu Vi: " + (long) ClientCultivationCache.tuVi + " / " + (long) reqTuVi + " (" + (int)(tuViRatio * 100) + "%)";
            graphics.drawCenteredString(mc.font, tuViText, centerX, barY, 0xFFFFFF);

            // Foundation & Meditation Tip
            int foundationPct = (int) (ClientCultivationCache.foundation * 100);
            String infoText = "§bCăn cơ: " + foundationPct + "% §7| §e[Nhấn C để dừng ngồi thiền]";
            graphics.drawCenteredString(mc.font, infoText, centerX, medY + 32, 0xFFFFFF);
        }

        // 3. Skill Slots Overlay (Bottom Right)
        int skillX = screenWidth - 145;
        int skillY = screenHeight - 25;
        String[] keys = {"Z", "X", "V", "R"};

        for (int i = 0; i < 4; i++) {
            int slotBoxX = skillX + i * 35;
            graphics.fill(slotBoxX, skillY, slotBoxX + 30, skillY + 20, 0xAA000000);
            graphics.fill(slotBoxX + 1, skillY + 1, slotBoxX + 29, skillY + 19, 0xDD222831);

            String skill = ClientCultivationCache.equippedSkills[i];
            String display = skill != null && !skill.isEmpty() ? skill.substring(0, Math.min(3, skill.length())) : "Trống";
            graphics.drawString(mc.font, "[" + keys[i] + "]", slotBoxX + 2, skillY + 2, 0xFFFF00, false);
            graphics.drawString(mc.font, display, slotBoxX + 2, skillY + 10, 0x00FFCC, false);
        }
    }
}
