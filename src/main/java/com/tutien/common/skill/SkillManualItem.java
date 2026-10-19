package com.tutien.common.skill;

import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.registry.ModDataAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class SkillManualItem extends Item {
    public enum SkillTier {
        HOANG(1, "Hoàng Cấp", "§e"),
        HUYEN(2, "Huyền Cấp", "§9"),
        DIA(3, "Địa Cấp", "§5"),
        THIEN(4, "Thiên Cấp", "§6§l");

        private final int rank;
        private final String name;
        private final String color;

        SkillTier(int rank, String name, String color) {
            this.rank = rank;
            this.name = name;
            this.color = color;
        }

        public String getColoredName() { return color + name; }
    }

    public enum SkillType {
        VO_KY("Võ Kỹ", "Tấn công nhanh, hồi chiêu ngắn, tiêu hao ít linh khí"),
        THAN_PHAP("Thân Pháp", "Lướt, né tránh, dịch chuyển tức thời và tăng tốc"),
        TUYET_KY("Tuyệt Kỹ", "Sát thương lớn, hiệu ứng khống chế hoặc hủy diệt"),
        BI_PHAP("Bí Pháp", "Sát thương cực đại diện rộng, tiêu hao nhiều linh khí");

        private final String name;
        private final String description;

        SkillType(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
    }

    private final String skillId;
    private final String skillName;
    private final SkillTier tier;
    private final SkillType type;
    private final float qiCost;
    private final int cooldownSeconds;

    public SkillManualItem(String skillId, String skillName, SkillTier tier, SkillType type, float qiCost, int cooldownSeconds) {
        super(new Item.Properties().stacksTo(1));
        this.skillId = skillId;
        this.skillName = skillName;
        this.tier = tier;
        this.type = type;
        this.qiCost = qiCost;
        this.cooldownSeconds = cooldownSeconds;
    }

    public String getSkillId() { return skillId; }
    public String getSkillName() { return skillName; }
    public SkillTier getTier() { return tier; }
    public SkillType getType() { return type; }
    public float getQiCost() { return qiCost; }
    public int getCooldownSeconds() { return cooldownSeconds; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PlayerCultivationData data = serverPlayer.getData(ModDataAttachments.CULTIVATION_DATA);
            if (data == null) return InteractionResultHolder.pass(stack);

            if (data.hasLearnedSkill(skillId)) {
                serverPlayer.sendSystemMessage(Component.literal("§c[Tu Tiên] Đạo hữu đã lĩnh ngộ công pháp này rồi, không thể học lại!"));
                return InteractionResultHolder.fail(stack);
            }

            data.learnSkill(skillId);
            serverPlayer.sendSystemMessage(Component.literal("§a[Tu Tiên] Chúc mừng đạo hữu đã lĩnh ngộ " + tier.getColoredName() + " " + type.getName() + " - " + skillName + "!"));
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§7Đẳng cấp: " + tier.getColoredName()));
        tooltipComponents.add(Component.literal("§7Loại hình: §f" + type.getName()));
        tooltipComponents.add(Component.literal("§7Tiêu hao: §b" + (int)qiCost + " Linh khí"));
        tooltipComponents.add(Component.literal("§7Hồi chiêu: §e" + cooldownSeconds + " giây"));
        tooltipComponents.add(Component.literal("§8" + type.getDescription()));
        tooltipComponents.add(Component.literal("§aNhấn chuột phải để lĩnh ngộ công pháp"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
