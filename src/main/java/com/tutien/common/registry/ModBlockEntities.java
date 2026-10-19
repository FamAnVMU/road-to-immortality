package com.tutien.common.registry;

import com.tutien.TuTienMod;
import com.tutien.common.alchemy.AlchemyCauldronBlockEntity;
import com.tutien.common.altar.RecoveryAltarBlockEntity;
import com.tutien.common.array.CultivationArrayBlockEntity;
import com.tutien.common.refining.RefiningForgeBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TuTienMod.MOD_ID);

    public static final Supplier<BlockEntityType<AlchemyCauldronBlockEntity>> ALCHEMY_CAULDRON =
            BLOCK_ENTITIES.register("alchemy_cauldron", () ->
                    BlockEntityType.Builder.of(AlchemyCauldronBlockEntity::new, ModBlocks.LO_LUYEN_DAN.get()).build(null));

    public static final Supplier<BlockEntityType<RefiningForgeBlockEntity>> REFINING_FORGE =
            BLOCK_ENTITIES.register("refining_forge", () ->
                    BlockEntityType.Builder.of(RefiningForgeBlockEntity::new, ModBlocks.LO_LUYEN_KHI.get()).build(null));

    public static final Supplier<BlockEntityType<CultivationArrayBlockEntity>> CULTIVATION_ARRAY =
            BLOCK_ENTITIES.register("cultivation_array", () ->
                    BlockEntityType.Builder.of(CultivationArrayBlockEntity::new, ModBlocks.TRAN_PHAP_CORE.get()).build(null));

    public static final Supplier<BlockEntityType<RecoveryAltarBlockEntity>> RECOVERY_ALTAR =
            BLOCK_ENTITIES.register("recovery_altar", () ->
                    BlockEntityType.Builder.of(RecoveryAltarBlockEntity::new, ModBlocks.TE_DAN_HOI_PHUC.get()).build(null));
}
