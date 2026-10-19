package com.tutien.common.registry;

import com.tutien.TuTienMod;
import com.tutien.common.npc.TuTienNpcEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, TuTienMod.MOD_ID);

    public static final Supplier<EntityType<TuTienNpcEntity>> TUTIEN_NPC =
            ENTITIES.register("tutien_npc", () ->
                    EntityType.Builder.of(TuTienNpcEntity::new, MobCategory.MISC)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(10)
                            .build("tutien:tutien_npc"));
}
