package com.tutien.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import com.tutien.TuTienMod;
import com.tutien.client.gui.CultivationScreen;
import com.tutien.client.hud.ClientCultivationCache;
import com.tutien.common.network.C2SCastSkillPayload;
import com.tutien.common.network.C2SToggleMeditationPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = TuTienMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientKeyBindings {
    public static final String CATEGORY = "key.categories.tutien";

    public static final KeyMapping KEY_MEDITATE = new KeyMapping("key.tutien.meditate",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, CATEGORY);

    public static final KeyMapping KEY_CULTIVATION_MENU = new KeyMapping("key.tutien.menu",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, CATEGORY);

    public static final KeyMapping KEY_SKILL_1 = new KeyMapping("key.tutien.skill_1",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY);
    public static final KeyMapping KEY_SKILL_2 = new KeyMapping("key.tutien.skill_2",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY);
    public static final KeyMapping KEY_SKILL_3 = new KeyMapping("key.tutien.skill_3",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping KEY_SKILL_4 = new KeyMapping("key.tutien.skill_4",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KEY_MEDITATE);
        event.register(KEY_CULTIVATION_MENU);
        event.register(KEY_SKILL_1);
        event.register(KEY_SKILL_2);
        event.register(KEY_SKILL_3);
        event.register(KEY_SKILL_4);
    }

    @EventBusSubscriber(modid = TuTienMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static class GameEventHandler {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            while (KEY_MEDITATE.consumeClick()) {
                PacketDistributor.sendToServer(new C2SToggleMeditationPayload());
            }

            // Immediately interrupt meditation if player moves, walks, jumps, or sprints
            if (ClientCultivationCache.isMeditating && mc.player.input != null) {
                if (mc.player.input.hasForwardImpulse() || mc.player.input.leftImpulse != 0 || mc.player.input.jumping || mc.player.isSprinting()) {
                    ClientCultivationCache.isMeditating = false;
                    PacketDistributor.sendToServer(new C2SToggleMeditationPayload());
                }
            }

            while (KEY_CULTIVATION_MENU.consumeClick()) {
                if (mc.screen == null) {
                    mc.setScreen(new CultivationScreen());
                }
            }

            while (KEY_SKILL_1.consumeClick()) {
                PacketDistributor.sendToServer(new C2SCastSkillPayload(0));
            }
            while (KEY_SKILL_2.consumeClick()) {
                PacketDistributor.sendToServer(new C2SCastSkillPayload(1));
            }
            while (KEY_SKILL_3.consumeClick()) {
                PacketDistributor.sendToServer(new C2SCastSkillPayload(2));
            }
            while (KEY_SKILL_4.consumeClick()) {
                PacketDistributor.sendToServer(new C2SCastSkillPayload(3));
            }
        }
    }
}
