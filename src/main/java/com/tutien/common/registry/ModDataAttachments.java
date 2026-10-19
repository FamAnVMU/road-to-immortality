package com.tutien.common.registry;

import com.tutien.TuTienMod;
import com.tutien.common.cultivation.PlayerCultivationData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TuTienMod.MOD_ID);

    public static final Supplier<AttachmentType<PlayerCultivationData>> CULTIVATION_DATA =
            ATTACHMENT_TYPES.register("cultivation_data", () ->
                    AttachmentType.serializable(PlayerCultivationData::new)
                            .copyOnDeath()
                            .build()
            );
}
