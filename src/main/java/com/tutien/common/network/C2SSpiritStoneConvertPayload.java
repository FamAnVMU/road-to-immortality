package com.tutien.common.network;

import com.tutien.TuTienMod;
import com.tutien.common.currency.SpiritStoneHelper;
import com.tutien.common.registry.ModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SSpiritStoneConvertPayload(int action) implements CustomPacketPayload {
    public static final Type<C2SSpiritStoneConvertPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TuTienMod.MOD_ID, "convert_spirit_stone"));

    public static final StreamCodec<ByteBuf, C2SSpiritStoneConvertPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    C2SSpiritStoneConvertPayload::action,
                    C2SSpiritStoneConvertPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SSpiritStoneConvertPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                switch (payload.action()) {
                    case 0 -> {
                        // 1000 Hạ -> 1 Trung
                        if (SpiritStoneHelper.hasEnough(player, 1000L)) {
                            SpiritStoneHelper.deductValue(player, 1000L);
                            give(player, new ItemStack(ModItems.TRUNG_PHAM_LINH_THACH.get(), 1));
                            player.sendSystemMessage(Component.literal("§a[Quy Đổi] Thành công đổi 1.000 Hạ Phẩm -> 1 Trung Phẩm Linh Thạch!"));
                        } else {
                            player.sendSystemMessage(Component.literal("§c[Quy Đổi] Bạn không đủ 1.000 Hạ Phẩm Linh Thạch!"));
                        }
                    }
                    case 1 -> {
                        // 1000 Trung -> 1 Thượng
                        if (countItems(player, ModItems.TRUNG_PHAM_LINH_THACH.get()) >= 1000) {
                            shrinkItems(player, ModItems.TRUNG_PHAM_LINH_THACH.get(), 1000);
                            give(player, new ItemStack(ModItems.THUONG_PHAM_LINH_THACH.get(), 1));
                            player.sendSystemMessage(Component.literal("§a[Quy Đổi] Thành công đổi 1.000 Trung Phẩm -> 1 Thượng Phẩm Linh Thạch!"));
                        } else {
                            player.sendSystemMessage(Component.literal("§c[Quy Đổi] Bạn không đủ 1.000 Trung Phẩm Linh Thạch!"));
                        }
                    }
                    case 2 -> {
                        // 1000 Thượng -> 1 Thần
                        if (countItems(player, ModItems.THUONG_PHAM_LINH_THACH.get()) >= 1000) {
                            shrinkItems(player, ModItems.THUONG_PHAM_LINH_THACH.get(), 1000);
                            give(player, new ItemStack(ModItems.THAN_PHAM_LINH_THACH.get(), 1));
                            player.sendSystemMessage(Component.literal("§6§l[Quy Đổi] Thành công đổi 1.000 Thượng Phẩm -> 1 THẦN PHẨM LINH THẠCH!"));
                        } else {
                            player.sendSystemMessage(Component.literal("§c[Quy Đổi] Bạn không đủ 1.000 Thượng Phẩm Linh Thạch!"));
                        }
                    }
                    case 3 -> {
                        // 1 Thần -> 1000 Thượng
                        if (countItems(player, ModItems.THAN_PHAM_LINH_THACH.get()) >= 1) {
                            shrinkItems(player, ModItems.THAN_PHAM_LINH_THACH.get(), 1);
                            SpiritStoneHelper.giveValue(player, 1000000000L);
                            player.sendSystemMessage(Component.literal("§a[Quy Đổi] Thành công phân rã 1 Thần Phẩm Linh Thạch!"));
                        } else {
                            player.sendSystemMessage(Component.literal("§c[Quy Đổi] Bạn không có Thần Phẩm Linh Thạch!"));
                        }
                    }
                    case 4 -> {
                        // 1 Thượng -> 1000 Trung
                        if (countItems(player, ModItems.THUONG_PHAM_LINH_THACH.get()) >= 1) {
                            shrinkItems(player, ModItems.THUONG_PHAM_LINH_THACH.get(), 1);
                            SpiritStoneHelper.giveValue(player, 1000000L);
                            player.sendSystemMessage(Component.literal("§a[Quy Đổi] Thành công phân rã 1 Thượng Phẩm Linh Thạch!"));
                        } else {
                            player.sendSystemMessage(Component.literal("§c[Quy Đổi] Bạn không có Thượng Phẩm Linh Thạch!"));
                        }
                    }
                    case 5 -> {
                        // 1 Trung -> 1000 Hạ
                        if (countItems(player, ModItems.TRUNG_PHAM_LINH_THACH.get()) >= 1) {
                            shrinkItems(player, ModItems.TRUNG_PHAM_LINH_THACH.get(), 1);
                            SpiritStoneHelper.giveValue(player, 1000L);
                            player.sendSystemMessage(Component.literal("§a[Quy Đổi] Thành công phân rã 1 Trung Phẩm Linh Thạch!"));
                        } else {
                            player.sendSystemMessage(Component.literal("§c[Quy Đổi] Bạn không có Trung Phẩm Linh Thạch!"));
                        }
                    }
                    case 6 -> {
                        // Tối ưu hóa toàn bộ linh thạch
                        long totalVal = SpiritStoneHelper.getTotalValue(player);
                        if (totalVal > 0) {
                            // Clear all existing stones
                            for (ItemStack stack : player.getInventory().items) {
                                if (stack.getItem() instanceof com.tutien.common.currency.SpiritStoneItem) {
                                    stack.setCount(0);
                                }
                            }
                            // Re-give compactly
                            SpiritStoneHelper.giveValue(player, totalVal);
                            player.sendSystemMessage(Component.literal("§a[Quy Đổi] Đã nén và tối ưu hóa toàn bộ linh thạch trong túi đồ!"));
                        }
                    }
                }
                player.getInventory().setChanged();
            }
        });
    }

    private static int countItems(ServerPlayer player, net.minecraft.world.item.Item item) {
        int count = 0;
        for (ItemStack s : player.getInventory().items) {
            if (s.is(item)) count += s.getCount();
        }
        return count;
    }

    private static void shrinkItems(ServerPlayer player, net.minecraft.world.item.Item item, int count) {
        int left = count;
        for (ItemStack s : player.getInventory().items) {
            if (s.is(item)) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
                if (left <= 0) break;
            }
        }
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}
