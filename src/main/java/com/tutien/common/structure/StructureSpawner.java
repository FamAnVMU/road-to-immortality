package com.tutien.common.structure;

import com.tutien.common.altar.RecoveryAltarBlockEntity;
import com.tutien.common.alchemy.AlchemyCauldronBlockEntity;
import com.tutien.common.npc.TuTienNpcEntity;
import com.tutien.common.npc.TuTienNpcRole;
import com.tutien.common.refining.RefiningForgeBlockEntity;
import com.tutien.common.registry.ModBlocks;
import com.tutien.common.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.io.File;

public class StructureSpawner {

    public static boolean spawnStructure(ServerLevel level, String structureName, BlockPos pos, ServerPlayer player) {
        String fileName = switch (structureName.toLowerCase()) {
            case "thitran", "thi_tran" -> "Thị trấn.litematic";
            case "thuongvan", "thuong_van" -> "Thương Vân Tông.litematic";
            case "conluan", "con_luan" -> "Côn Luận Thánh Địa.litematic";
            default -> "";
        };

        if (fileName.isEmpty()) {
            player.sendSystemMessage(Component.translatable("tutien.structure.invalid_name"));
            return false;
        }

        // Try current run directory or parent structures folder
        File file = new File("structures/" + fileName);
        if (!file.exists()) {
            file = new File("../structures/" + fileName);
        }

        if (!file.exists()) {
            player.sendSystemMessage(Component.translatable("tutien.structure.file_not_found", fileName));
            return false;
        }

        player.sendSystemMessage(Component.translatable("tutien.structure.spawning", structureName));
        int placed = LitematicParser.placeLitematic(level, file, pos);

        // Populate Facilities and NPCs
        populateStructureFacilities(level, structureName.toLowerCase(), pos);

        player.sendSystemMessage(Component.translatable("tutien.structure.success", structureName, placed));
        return true;
    }

    private static void populateStructureFacilities(ServerLevel level, String structureName, BlockPos pos) {
        switch (structureName) {
            case "thitran", "thi_tran" -> {
                // Town: Spawn 6 NPCs
                spawnNpc(level, pos.offset(2, 1, 2), TuTienNpcRole.DAN_DUOC);
                spawnNpc(level, pos.offset(5, 1, 2), TuTienNpcRole.DAN_PHUONG);
                spawnNpc(level, pos.offset(8, 1, 2), TuTienNpcRole.PHAP_BAO);
                spawnNpc(level, pos.offset(11, 1, 2), TuTienNpcRole.BAN_VE);
                spawnNpc(level, pos.offset(14, 1, 2), TuTienNpcRole.CONG_PHAP);
                spawnNpc(level, pos.offset(17, 1, 2), TuTienNpcRole.NHIEM_VU_THI_TRAN);

                // Fixed Cauldron & Forge & Altar in Town
                setFixedCauldron(level, pos.offset(3, 1, 5), 1);
                setFixedForge(level, pos.offset(6, 1, 5), 1);
                setFixedAltar(level, pos.offset(9, 1, 5), RecoveryAltarBlockEntity.AltarTier.TOWN);
            }
            case "thuongvan", "thuong_van" -> {
                // Cangyun Sect: Elder NPC, mid tier forge and altar
                spawnNpc(level, pos.offset(3, 1, 3), TuTienNpcRole.TONG_MON_THUONG_VAN);
                setFixedCauldron(level, pos.offset(6, 1, 6), 3);
                setFixedForge(level, pos.offset(8, 1, 6), 2);
                setFixedAltar(level, pos.offset(10, 1, 6), RecoveryAltarBlockEntity.AltarTier.CANGYUN);
            }
            case "conluan", "con_luan" -> {
                // Kunlun Sacred Ground: Sacred Elder NPC, high tier forge and altar
                spawnNpc(level, pos.offset(5, 1, 5), TuTienNpcRole.TONG_MON_CON_LUAN);
                setFixedCauldron(level, pos.offset(10, 1, 10), 7);
                setFixedForge(level, pos.offset(12, 1, 10), 4);
                setFixedAltar(level, pos.offset(14, 1, 10), RecoveryAltarBlockEntity.AltarTier.KUNLUN);
            }
        }
    }

    private static void spawnNpc(ServerLevel level, BlockPos pos, TuTienNpcRole role) {
        TuTienNpcEntity npc = ModEntities.TUTIEN_NPC.get().create(level);
        if (npc != null) {
            npc.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
            npc.setNpcRole(role);
            level.addFreshEntity(npc);
        }
    }

    private static void setFixedCauldron(ServerLevel level, BlockPos pos, int tier) {
        level.setBlock(pos, ModBlocks.LO_LUYEN_DAN.get().defaultBlockState(), 3);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AlchemyCauldronBlockEntity cauldron) {
            cauldron.setTier(tier);
            cauldron.setFixedInWorld(true);
        }
    }

    private static void setFixedForge(ServerLevel level, BlockPos pos, int tier) {
        level.setBlock(pos, ModBlocks.LO_LUYEN_KHI.get().defaultBlockState(), 3);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RefiningForgeBlockEntity forge) {
            forge.setTier(tier);
            forge.setFixedInWorld(true);
        }
    }

    private static void setFixedAltar(ServerLevel level, BlockPos pos, RecoveryAltarBlockEntity.AltarTier tier) {
        level.setBlock(pos, ModBlocks.TE_DAN_HOI_PHUC.get().defaultBlockState(), 3);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RecoveryAltarBlockEntity altar) {
            altar.setTier(tier);
            altar.setFixedInWorld(true);
        }
    }
}
