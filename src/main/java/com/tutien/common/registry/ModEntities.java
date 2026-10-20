package com.tutien.common.registry;

import com.tutien.TuTienMod;
import com.tutien.common.npc.TuTienNpcEntity;
import com.tutien.common.npc.TuTienNpcRole;
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
                    EntityType.Builder.<TuTienNpcEntity>of(TuTienNpcEntity::new, MobCategory.MISC)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(10)
                            .build("tutien:tutien_npc"));

    public static final Supplier<EntityType<TuTienNpcEntity>> NPC_DAN_DUOC =
            ENTITIES.register("npc_dan_duoc", () ->
                    EntityType.Builder.<TuTienNpcEntity>of((type, lvl) -> new TuTienNpcEntity(type, lvl, TuTienNpcRole.DAN_DUOC), MobCategory.MISC)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(10)
                            .build("tutien:npc_dan_duoc"));

    public static final Supplier<EntityType<TuTienNpcEntity>> NPC_DAN_PHUONG =
            ENTITIES.register("npc_dan_phuong", () ->
                    EntityType.Builder.<TuTienNpcEntity>of((type, lvl) -> new TuTienNpcEntity(type, lvl, TuTienNpcRole.DAN_PHUONG), MobCategory.MISC)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(10)
                            .build("tutien:npc_dan_phuong"));

    public static final Supplier<EntityType<TuTienNpcEntity>> NPC_PHAP_BAO =
            ENTITIES.register("npc_phap_bao", () ->
                    EntityType.Builder.<TuTienNpcEntity>of((type, lvl) -> new TuTienNpcEntity(type, lvl, TuTienNpcRole.PHAP_BAO), MobCategory.MISC)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(10)
                            .build("tutien:npc_phap_bao"));

    public static final Supplier<EntityType<TuTienNpcEntity>> NPC_BAN_VE =
            ENTITIES.register("npc_ban_ve", () ->
                    EntityType.Builder.<TuTienNpcEntity>of((type, lvl) -> new TuTienNpcEntity(type, lvl, TuTienNpcRole.BAN_VE), MobCategory.MISC)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(10)
                            .build("tutien:npc_ban_ve"));

    public static final Supplier<EntityType<TuTienNpcEntity>> NPC_CONG_PHAP =
            ENTITIES.register("npc_cong_phap", () ->
                    EntityType.Builder.<TuTienNpcEntity>of((type, lvl) -> new TuTienNpcEntity(type, lvl, TuTienNpcRole.CONG_PHAP), MobCategory.MISC)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(10)
                            .build("tutien:npc_cong_phap"));

    public static final Supplier<EntityType<TuTienNpcEntity>> NPC_NHIEM_VU =
            ENTITIES.register("npc_nhiem_vu", () ->
                    EntityType.Builder.<TuTienNpcEntity>of((type, lvl) -> new TuTienNpcEntity(type, lvl, TuTienNpcRole.NHIEM_VU_THI_TRAN), MobCategory.MISC)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(10)
                            .build("tutien:npc_nhiem_vu"));

    public static final Supplier<EntityType<TuTienNpcEntity>> NPC_THUONG_VAN =
            ENTITIES.register("npc_thuong_van", () ->
                    EntityType.Builder.<TuTienNpcEntity>of((type, lvl) -> new TuTienNpcEntity(type, lvl, TuTienNpcRole.TONG_MON_THUONG_VAN), MobCategory.MISC)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(10)
                            .build("tutien:npc_thuong_van"));

    public static final Supplier<EntityType<TuTienNpcEntity>> NPC_CON_LUAN =
            ENTITIES.register("npc_con_luan", () ->
                    EntityType.Builder.<TuTienNpcEntity>of((type, lvl) -> new TuTienNpcEntity(type, lvl, TuTienNpcRole.TONG_MON_CON_LUAN), MobCategory.MISC)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(10)
                            .build("tutien:npc_con_luan"));
}
