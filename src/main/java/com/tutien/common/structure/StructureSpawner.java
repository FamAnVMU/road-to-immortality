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
        String resourceName = switch (structureName.toLowerCase()) {
            case "thitran", "thi_tran" -> "thitran.litematic";
            case "thuongvan", "thuong_van" -> "thuongvan.litematic";
            case "conluan", "con_luan" -> "conluan.litematic";
            default -> "";
        };

        String legacyFileName = switch (structureName.toLowerCase()) {
            case "thitran", "thi_tran" -> "Thị trấn.litematic";
            case "thuongvan", "thuong_van" -> "Thương Vân Tông.litematic";
            case "conluan", "con_luan" -> "Côn Luận Thánh Địa.litematic";
            default -> "";
        };

        if (resourceName.isEmpty()) {
            if (player != null) player.sendSystemMessage(Component.translatable("tutien.structure.invalid_name"));
            return false;
        }

        if (player != null) player.sendSystemMessage(Component.translatable("tutien.structure.spawning", structureName));

        int placed = 0;
        // 1. First attempt: Load directly from bundled mod resources inside the JAR
        java.io.InputStream stream = StructureSpawner.class.getResourceAsStream("/assets/tutien/structures/" + resourceName);
        if (stream == null) {
            stream = StructureSpawner.class.getClassLoader().getResourceAsStream("assets/tutien/structures/" + resourceName);
        }

        if (stream != null) {
            try (java.io.InputStream in = stream) {
                placed = LitematicParser.placeLitematic(level, in, pos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 2. Fallback: Search on filesystem (for local custom override)
            File file = new File("structures/" + legacyFileName);
            if (!file.exists()) file = new File("structures/" + resourceName);
            if (!file.exists()) file = new File("../structures/" + legacyFileName);
            if (!file.exists()) file = new File("../structures/" + resourceName);

            if (file.exists()) {
                placed = LitematicParser.placeLitematic(level, file, pos);
            } else {
                if (player != null) player.sendSystemMessage(Component.translatable("tutien.structure.file_not_found", resourceName));
                return false;
            }
        }

        // Populate Facilities and NPCs
        populateStructureFacilities(level, structureName.toLowerCase(), pos);

        // Generate Thematic Loot Chests
        StructureLootGenerator.generateStructureLoot(level, structureName.toLowerCase(), pos);

        // Record structure in world data
        StructureTracker.get(level).recordStructure(structureName.toLowerCase(), pos);

        if (player != null) {
            player.sendSystemMessage(Component.translatable("tutien.structure.success", structureName, placed));
        }
        return true;
    }

    public static boolean spawnStructure(ServerLevel level, String structureName, BlockPos pos) {
        return spawnStructure(level, structureName, pos, null);
    }

    public static boolean locateStructure(ServerLevel level, String structureName, BlockPos currentPos, ServerPlayer player) {
        String key = switch (structureName.toLowerCase()) {
            case "thitran", "thi_tran" -> "thitran";
            case "thuongvan", "thuong_van" -> "thuongvan";
            case "conluan", "con_luan" -> "conluan";
            default -> structureName.toLowerCase();
        };

        BlockPos nearest = StructureTracker.get(level).findNearest(key, currentPos);
        if (nearest == null) {
            nearest = StructureWorldGenHandler.findNearestCandidate(level, key, currentPos);
        }

        if (nearest == null) {
            Component spawnLink = Component.literal(" [")
                    .append(Component.translatable("tutien.structure.spawn_now_button"))
                    .append(Component.literal("]"))
                    .withStyle(style -> style
                            .withColor(net.minecraft.ChatFormatting.GOLD)
                            .withUnderlined(true)
                            .withClickEvent(new net.minecraft.network.chat.ClickEvent(net.minecraft.network.chat.ClickEvent.Action.RUN_COMMAND, "/tutien structure spawn " + key))
                            .withHoverEvent(new net.minecraft.network.chat.HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.translatable("tutien.structure.spawn_now_hover"))));

            player.sendSystemMessage(Component.translatable("tutien.structure.not_spawned_yet", structureName).append(spawnLink));
            return false;
        }

        int surfaceY = nearest.getY() > 0 ? nearest.getY() : 70;
        if (level.hasChunk(nearest.getX() >> 4, nearest.getZ() >> 4)) {
            int calcY = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, nearest.getX(), nearest.getZ());
            if (calcY > level.getMinBuildHeight() + 10) {
                surfaceY = calcY;
            }
        }
        BlockPos finalPos = new BlockPos(nearest.getX(), surfaceY, nearest.getZ());

        int dist = (int) Math.sqrt(finalPos.distSqr(currentPos));
        Component message = Component.translatable("tutien.structure.locate_found", structureName, finalPos.getX(), finalPos.getY(), finalPos.getZ(), dist)
                .append(Component.literal(" ")
                        .append(Component.translatable("tutien.structure.teleport_button")
                                .withStyle(style -> style
                                        .withColor(net.minecraft.ChatFormatting.GREEN)
                                        .withUnderlined(true)
                                        .withClickEvent(new net.minecraft.network.chat.ClickEvent(net.minecraft.network.chat.ClickEvent.Action.RUN_COMMAND, "/tp @s " + finalPos.getX() + " " + finalPos.getY() + " " + finalPos.getZ()))
                                        .withHoverEvent(new net.minecraft.network.chat.HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.translatable("tutien.structure.teleport_hover"))))));

        player.sendSystemMessage(message);
        return true;
    }

    private static void populateStructureFacilities(ServerLevel level, String structureName, BlockPos pos) {
        switch (structureName) {
            case "thitran", "thi_tran" -> {
                // Town Square & Market (Center of town path layout is at X=42, Z=68)
                spawnNpc(level, pos.offset(38, 2, 65), TuTienNpcRole.DAN_DUOC);
                spawnNpc(level, pos.offset(38, 2, 68), TuTienNpcRole.DAN_PHUONG);
                spawnNpc(level, pos.offset(46, 2, 65), TuTienNpcRole.PHAP_BAO);
                spawnNpc(level, pos.offset(46, 2, 68), TuTienNpcRole.BAN_VE);
                spawnNpc(level, pos.offset(42, 2, 63), TuTienNpcRole.CONG_PHAP);
                spawnNpc(level, pos.offset(42, 2, 72), TuTienNpcRole.NHIEM_VU_THI_TRAN);

                // Fixed Cauldron & Forge & Altar in Town Square
                setFixedCauldron(level, pos.offset(36, 2, 68), 1);
                setFixedForge(level, pos.offset(48, 2, 68), 1);
                setFixedAltar(level, pos.offset(42, 2, 67), RecoveryAltarBlockEntity.AltarTier.TOWN);
            }
            case "thuongvan", "thuong_van" -> {
                // Cangyun Sect Main Terrace & Elder Pavilion (X=81, Z=57, Y=11)
                spawnNpc(level, pos.offset(81, 11, 57), TuTienNpcRole.TONG_MON_THUONG_VAN);
                spawnNpc(level, pos.offset(81, 11, 63), TuTienNpcRole.NHIEM_VU_THI_TRAN);

                setFixedAltar(level, pos.offset(81, 11, 52), RecoveryAltarBlockEntity.AltarTier.CANGYUN);
                setFixedCauldron(level, pos.offset(77, 11, 60), 3);
                setFixedForge(level, pos.offset(85, 11, 60), 2);
            }
            case "conluan", "con_luan" -> {
                // Kunlun Sacred Ground: High Celestial Sanctum & Altar Dais (X=29, Z=90, Y=47)
                spawnNpc(level, pos.offset(29, 47, 92), TuTienNpcRole.TONG_MON_CON_LUAN);
                spawnNpc(level, pos.offset(65, 4, 30), TuTienNpcRole.CONG_PHAP); // Gate Elder

                setFixedAltar(level, pos.offset(29, 47, 88), RecoveryAltarBlockEntity.AltarTier.KUNLUN);
                setFixedCauldron(level, pos.offset(25, 47, 90), 7);
                setFixedForge(level, pos.offset(33, 47, 90), 4);
            }
        }
    }

    public static void spawnNpc(ServerLevel level, BlockPos pos, TuTienNpcRole role) {
        net.minecraft.world.entity.EntityType<TuTienNpcEntity> type = switch (role) {
            case DAN_DUOC -> ModEntities.NPC_DAN_DUOC.get();
            case DAN_PHUONG -> ModEntities.NPC_DAN_PHUONG.get();
            case PHAP_BAO -> ModEntities.NPC_PHAP_BAO.get();
            case BAN_VE -> ModEntities.NPC_BAN_VE.get();
            case CONG_PHAP -> ModEntities.NPC_CONG_PHAP.get();
            case NHIEM_VU_THI_TRAN -> ModEntities.NPC_NHIEM_VU.get();
            case TONG_MON_THUONG_VAN -> ModEntities.NPC_THUONG_VAN.get();
            case TONG_MON_CON_LUAN -> ModEntities.NPC_CON_LUAN.get();
        };

        TuTienNpcEntity npc = type.create(level);
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
