package com.tutien.client.hud;

import com.tutien.TuTienMod;
import com.tutien.common.cultivation.Realm;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
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

        // 1. Spiritual Qi Bar (Positioned directly above the vanilla food/hunger bar)
        int midX = screenWidth / 2;
        int qiX = midX + 10;
        int qiWidth = 81;
        int qiHeight = 6;

        // Base position directly above food bar (y = screenHeight - 39)
        int qiY = screenHeight - 47;

        // If player is submerged / air bubbles are visible, move above air bubbles
        if (mc.player.getAirSupply() < mc.player.getMaxAirSupply()) {
            qiY -= 10;
        }

        float maxQi = Math.max(1.0f, ClientCultivationCache.maxLingQi);
        float qiRatio = Math.max(0.0f, Math.min(1.0f, ClientCultivationCache.lingQi / maxQi));
        int fillWidth = (int) (qiWidth * qiRatio);

        // Background & Border
        graphics.fill(qiX - 1, qiY - 1, qiX + qiWidth + 1, qiY + qiHeight + 1, 0xCC000000);
        graphics.fill(qiX, qiY, qiX + qiWidth, qiY + qiHeight, 0xEE0A1118);

        // Radiant Spiritual Qi Fill (Spiritual Cyan / Blue Gradient)
        if (fillWidth > 0) {
            graphics.fillGradient(qiX, qiY, qiX + fillWidth, qiY + qiHeight, 0xFF00E5FF, 0xFF0091EA);
        }

        // Compact centered numeric overlay (e.g. "100/100")
        String qiText = (int) ClientCultivationCache.lingQi + "/" + (int) maxQi;
        int textWidth = mc.font.width(qiText);
        float scale = 0.75f;
        graphics.pose().pushPose();
        graphics.pose().translate(qiX + (qiWidth - textWidth * scale) / 2.0f, qiY - 0.5f, 0.0f);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.drawString(mc.font, qiText, 0, 0, 0xFFFFFF, true);
        graphics.pose().popPose();

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
            String title = Component.literal("§6§l[").append(currentRealm.getDisplayNameComponent()).append(" - ").append(Component.translatable("tutien.realm.layer", ClientCultivationCache.layer)).append("]").getString();
            graphics.drawCenteredString(mc.font, title, centerX, medY + 4, 0xFFFFFF);

            // Tu Vi Bar
            double reqTuVi = currentRealm.calculateRequiredTuVi(ClientCultivationCache.layer);
            float tuViRatio = (float) Math.max(0.0, Math.min(1.0, ClientCultivationCache.tuVi / Math.max(1.0, reqTuVi)));
            int barWidth = 180;
            int barX = centerX - barWidth / 2;
            int barY = medY + 18;

            graphics.fill(barX, barY, barX + barWidth, barY + 8, 0xFF333333);
            graphics.fill(barX, barY, barX + (int) (barWidth * tuViRatio), barY + 8, 0xFF4E9F3D);

            String tuViText = Component.translatable("hud.tutien.tuvi_progress", (long) ClientCultivationCache.tuVi, (long) reqTuVi, (int)(tuViRatio * 100)).getString();
            graphics.drawCenteredString(mc.font, tuViText, centerX, barY, 0xFFFFFF);

            // Foundation & Meditation Tip
            int foundationPct = (int) (ClientCultivationCache.foundation * 100);
            String infoText = Component.translatable("hud.tutien.foundation_info", foundationPct).getString();
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
            String display = skill != null && !skill.isEmpty() ? skill.substring(0, Math.min(3, skill.length())) : Component.translatable("gui.tutien.cultivation.skill_empty").getString();
            graphics.drawString(mc.font, "[" + keys[i] + "]", slotBoxX + 2, skillY + 2, 0xFFFF00, false);
            graphics.drawString(mc.font, display, slotBoxX + 2, skillY + 10, 0x00FFCC, false);
        }
    }
}
