package com.tutien.common.structure;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.tutien.TuTienMod;
import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.network.ModPayloads;
import com.tutien.common.registry.ModDataAttachments;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.brigadier.tree.CommandNode;
import com.tutien.common.npc.TuTienNpcRole;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = TuTienMod.MOD_ID)
public class TuTienCommands {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("tutien")
                .requires(s -> s.hasPermission(2))
                .then(Commands.literal("structure")
                        .then(Commands.literal("spawn")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            builder.suggest("thitran");
                                            builder.suggest("thuongvan");
                                            builder.suggest("conluan");
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            String name = StringArgumentType.getString(ctx, "name");
                                            StructureSpawner.spawnStructure(player.serverLevel(), name, player.blockPosition(), player);
                                            return 1;
                                        })
                                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                .executes(ctx -> {
                                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                                    String name = StringArgumentType.getString(ctx, "name");
                                                    BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
                                                    StructureSpawner.spawnStructure(player.serverLevel(), name, pos, player);
                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("locate")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            builder.suggest("thitran");
                                            builder.suggest("thuongvan");
                                            builder.suggest("conluan");
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            String name = StringArgumentType.getString(ctx, "name");
                                            StructureSpawner.locateStructure(player.serverLevel(), name, player.blockPosition(), player);
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("locate")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    builder.suggest("thitran");
                                    builder.suggest("thuongvan");
                                    builder.suggest("conluan");
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    String name = StringArgumentType.getString(ctx, "name");
                                    StructureSpawner.locateStructure(player.serverLevel(), name, player.blockPosition(), player);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("npc")
                        .then(Commands.literal("spawn")
                                .then(Commands.argument("role", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            builder.suggest("danduoc");
                                            builder.suggest("danphuong");
                                            builder.suggest("phapbao");
                                            builder.suggest("banve");
                                            builder.suggest("congphap");
                                            builder.suggest("nhiemvu");
                                            builder.suggest("thuongvan");
                                            builder.suggest("conluan");
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            String roleStr = StringArgumentType.getString(ctx, "role");
                                            spawnNpcByRole(player.serverLevel(), roleStr, player.blockPosition(), player);
                                            return 1;
                                        })
                                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                .executes(ctx -> {
                                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                                    String roleStr = StringArgumentType.getString(ctx, "role");
                                                    BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
                                                    spawnNpcByRole(player.serverLevel(), roleStr, pos, player);
                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
                .then(Commands.literal("tuvi")
                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(1.0))
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            double amount = DoubleArgumentType.getDouble(ctx, "amount");
                                            PlayerCultivationData data = player.getData(ModDataAttachments.CULTIVATION_DATA);
                                            if (data != null) {
                                                data.addTuVi(amount);
                                                player.sendSystemMessage(Component.translatable("tutien.command.tuvi_added", (long)amount));
                                                ModPayloads.syncToPlayer(player);
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("realm")
                        .then(Commands.literal("set")
                                .then(Commands.argument("realmId", IntegerArgumentType.integer(0, 8))
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            int r = IntegerArgumentType.getInteger(ctx, "realmId");
                                            PlayerCultivationData data = player.getData(ModDataAttachments.CULTIVATION_DATA);
                                            if (data != null) {
                                                data.setRealmId(r);
                                                data.setLayer(1);
                                                data.syncAttributes(player);
                                                player.sendSystemMessage(Component.translatable("tutien.command.realm_set", data.getRealm().getDisplayNameComponent()));
                                                ModPayloads.syncToPlayer(player);
                                            }
                                            return 1;
                                        })
                                        .then(Commands.argument("layer", IntegerArgumentType.integer(1, 9))
                                                .executes(ctx -> {
                                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                                    int r = IntegerArgumentType.getInteger(ctx, "realmId");
                                                    int l = IntegerArgumentType.getInteger(ctx, "layer");
                                                    PlayerCultivationData data = player.getData(ModDataAttachments.CULTIVATION_DATA);
                                                    if (data != null) {
                                                        data.setRealmId(r);
                                                        data.setLayer(l);
                                                        data.syncAttributes(player);
                                                        player.sendSystemMessage(Component.translatable("tutien.command.realm_set_layer", data.getRealm().getDisplayNameComponent(), l));
                                                        ModPayloads.syncToPlayer(player);
                                                    }
                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
                .then(Commands.literal("qi")
                        .then(Commands.literal("restore")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    PlayerCultivationData data = player.getData(ModDataAttachments.CULTIVATION_DATA);
                                    if (data != null) {
                                        data.setLingQi(data.getMaxLingQi());
                                        player.sendSystemMessage(Component.translatable("tutien.command.qi_restored"));
                                        ModPayloads.syncToPlayer(player);
                                    }
                                    return 1;
                                })
                        )
                )
        );

        // Hook directly into /locate so /locate thitran, /locate thuongvan, /locate conluan, and /locate tutien work
        CommandNode<CommandSourceStack> locateNode = dispatcher.getRoot().getChild("locate");
        if (locateNode != null) {
            locateNode.addChild(Commands.literal("thitran")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        StructureSpawner.locateStructure(player.serverLevel(), "thitran", player.blockPosition(), player);
                        return 1;
                    }).build());
            locateNode.addChild(Commands.literal("thuongvan")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        StructureSpawner.locateStructure(player.serverLevel(), "thuongvan", player.blockPosition(), player);
                        return 1;
                    }).build());
            locateNode.addChild(Commands.literal("conluan")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        StructureSpawner.locateStructure(player.serverLevel(), "conluan", player.blockPosition(), player);
                        return 1;
                    }).build());
            locateNode.addChild(Commands.literal("tutien")
                    .then(Commands.argument("name", StringArgumentType.word())
                            .suggests((ctx, builder) -> {
                                builder.suggest("thitran");
                                builder.suggest("thuongvan");
                                builder.suggest("conluan");
                                return builder.buildFuture();
                            })
                            .executes(ctx -> {
                                ServerPlayer player = ctx.getSource().getPlayerOrException();
                                String name = StringArgumentType.getString(ctx, "name");
                                StructureSpawner.locateStructure(player.serverLevel(), name, player.blockPosition(), player);
                                return 1;
                            })
                    ).build());
        }
    }

    private static void spawnNpcByRole(ServerLevel level, String roleStr, BlockPos pos, ServerPlayer player) {
        TuTienNpcRole role = switch (roleStr.toLowerCase()) {
            case "danduoc", "dan_duoc" -> TuTienNpcRole.DAN_DUOC;
            case "danphuong", "dan_phuong" -> TuTienNpcRole.DAN_PHUONG;
            case "phapbao", "phap_bao" -> TuTienNpcRole.PHAP_BAO;
            case "banve", "ban_ve" -> TuTienNpcRole.BAN_VE;
            case "congphap", "cong_phap" -> TuTienNpcRole.CONG_PHAP;
            case "nhiemvu", "nhiem_vu" -> TuTienNpcRole.NHIEM_VU_THI_TRAN;
            case "thuongvan", "thuong_van" -> TuTienNpcRole.TONG_MON_THUONG_VAN;
            case "conluan", "con_luan" -> TuTienNpcRole.TONG_MON_CON_LUAN;
            default -> null;
        };

        if (role == null) {
            player.sendSystemMessage(Component.translatable("tutien.npc.invalid_role", roleStr));
            return;
        }

        StructureSpawner.spawnNpc(level, pos, role);
        player.sendSystemMessage(Component.translatable("tutien.npc.spawned_success", role.getTitleComponent(), pos.getX(), pos.getY(), pos.getZ()));
    }
}
