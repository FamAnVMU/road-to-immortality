package com.tutien.common.currency;

import com.tutien.common.registry.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SpiritStoneHelper {
    public static final long TRUNG_PHAM_VALUE = 1000L;
    public static final long THUONG_PHAM_VALUE = 1000000L;
    public static final long THAN_PHAM_VALUE = 1000000000L;

    public static long getTotalValue(Player player) {
        long total = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof SpiritStoneItem stoneItem) {
                total += stoneItem.getQuality().getBaseValue() * stack.getCount();
            }
        }
        return total;
    }

    public static boolean hasEnough(Player player, long amount) {
        return getTotalValue(player) >= amount;
    }

    /**
     * Khấu trừ linh thạch an toàn và trả lại tiền thối (nếu có).
     * Trả về true nếu thành công, false nếu không đủ tiền.
     */
    public static boolean deductValue(Player player, long amount) {
        if (!hasEnough(player, amount)) {
            return false;
        }

        long remainingToDeduct = amount;

        // Bắt đầu trừ từ phẩm cấp thấp lên cao (Hạ phẩm -> Trung phẩm -> Thượng phẩm -> Thần phẩm)
        Item[] stonesInOrder = new Item[]{
                ModItems.HA_PHAM_LINH_THACH.get(),
                ModItems.TRUNG_PHAM_LINH_THACH.get(),
                ModItems.THUONG_PHAM_LINH_THACH.get(),
                ModItems.THAN_PHAM_LINH_THACH.get()
        };

        for (Item stone : stonesInOrder) {
            if (remainingToDeduct <= 0) break;
            long stoneVal = ((SpiritStoneItem) stone).getQuality().getBaseValue();

            for (int i = 0; i < player.getInventory().items.size(); i++) {
                if (remainingToDeduct <= 0) break;
                ItemStack stack = player.getInventory().items.get(i);
                if (stack.is(stone)) {
                    long neededStones = (remainingToDeduct + stoneVal - 1) / stoneVal; // Ceil division
                    int takeCount = (int) Math.min(stack.getCount(), neededStones);

                    long deductedVal = takeCount * stoneVal;
                    stack.shrink(takeCount);

                    if (deductedVal > remainingToDeduct) {
                        long change = deductedVal - remainingToDeduct;
                        giveValue(player, change);
                        remainingToDeduct = 0;
                    } else {
                        remainingToDeduct -= deductedVal;
                    }
                }
            }
        }

        player.getInventory().setChanged();
        return true;
    }

    /**
     * Trao linh thạch theo giá trị quy đổi tối ưu nhất cho người chơi.
     */
    public static void giveValue(Player player, long value) {
        if (value <= 0) return;

        long divineCount = value / THAN_PHAM_VALUE;
        value %= THAN_PHAM_VALUE;

        long highCount = value / THUONG_PHAM_VALUE;
        value %= THUONG_PHAM_VALUE;

        long midCount = value / TRUNG_PHAM_VALUE;
        value %= TRUNG_PHAM_VALUE;

        long lowCount = value;

        giveStones(player, ModItems.THAN_PHAM_LINH_THACH.get(), divineCount);
        giveStones(player, ModItems.THUONG_PHAM_LINH_THACH.get(), highCount);
        giveStones(player, ModItems.TRUNG_PHAM_LINH_THACH.get(), midCount);
        giveStones(player, ModItems.HA_PHAM_LINH_THACH.get(), lowCount);
    }

    private static void giveStones(Player player, Item item, long count) {
        while (count > 0) {
            int stackCount = (int) Math.min(64, count);
            ItemStack stack = new ItemStack(item, stackCount);
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
            count -= stackCount;
        }
    }
}
