package com.tutien;

import com.tutien.common.config.TuTienConfig;
import com.tutien.common.refining.TuTienArmorMaterials;
import com.tutien.common.registry.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(TuTienMod.MOD_ID)
public class TuTienMod {
    public static final String MOD_ID = "tutien";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public TuTienMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("[Tu Tiên] Đang khởi tạo mod Tu Tiên RPG cho Minecraft 1.21.1 NeoForge...");

        // Đăng ký toàn bộ Registry
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
        ModDataAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        TuTienArmorMaterials.ARMOR_MATERIALS.register(modEventBus);

        // Đăng ký cấu hình Mod
        modContainer.registerConfig(ModConfig.Type.COMMON, TuTienConfig.COMMON_SPEC);

        LOGGER.info("[Tu Tiên] Khởi tạo thành công các hệ thống Cốt Lõi: Tu Vi, Cảnh Giới, Linh Khí, Đan Dược, Pháp Bảo, Trận Pháp, NPC!");
    }
}
