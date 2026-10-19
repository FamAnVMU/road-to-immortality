package com.tutien.common.skill;

import com.tutien.common.cultivation.PlayerCultivationData;
import com.tutien.common.registry.ModDataAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
        HOANG(1, "tutien.tier.yellow", ChatFormatting.YELLOW),
        HUYEN(2, "tutien.tier.mystic", ChatFormatting.BLUE),
        DIA(3, "tutien.tier.earth", ChatFormatting.DARK_PURPLE),
        THIEN(4, "tutien.tier.heaven", ChatFormatting.GOLD);

        private final int rank;
        private final String translationKey;
        private final ChatFormatting color;

        SkillTier(int rank, String translationKey, ChatFormatting color) {
            this.rank = rank;
            this.translationKey = translationKey;
            this.color = color;
        }

        public MutableComponent getColoredName() {
            return Component.translatable(translationKey).withStyle(color);
        }
    }

    public enum SkillType {
        VO_KY("tutien.skill_type.martial", "tutien.skill_type.martial.desc"),
        THAN_PHAP("tutien.skill_type.movement", "tutien.skill_type.movement.desc"),
        TUYET_KY("tutien.skill_type.ultimate", "tutien.skill_type.ultimate.desc"),
        BI_PHAP("tutien.skill_type.secret", "tutien.skill_type.secret.desc");

        private final String nameKey;
        private final String descKey;

        SkillType(String nameKey, String descKey) {
            this.nameKey = nameKey;
            this.descKey = descKey;
        }

        public MutableComponent getNameComponent() { return Component.translatable(nameKey); }
        public MutableComponent getDescComponent() { return Component.translatable(descKey); }
    }

    private final String skillId;
    private final String skillNameKey;
    private final SkillTier tier;
    private final SkillType type;
    private final float qiCost;
    private final int cooldownSeconds;

    public SkillManualItem(String skillId, String skillNameKey, SkillTier tier, SkillType type, float qiCost, int cooldownSeconds) {
        super(new Item.Properties().stacksTo(1));
        this.skillId = skillId;
        this.skillNameKey = skillNameKey;
        this.tier = tier;
        this.type = type;
        this.qiCost = qiCost;
        this.cooldownSeconds = cooldownSeconds;
    }

    public String getSkillId() { return skillId; }
    public String getSkillNameKey() { return skillNameKey; }
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
                serverPlayer.sendSystemMessage(Component.translatable("message.tutien.skill_already_learned").withStyle(ChatFormatting.RED));
                return InteractionResultHolder.fail(stack);
            }

            data.learnSkill(skillId);
            serverPlayer.sendSystemMessage(Component.translatable("message.tutien.skill_learned",
                    tier.getColoredName(), type.getNameComponent(), Component.translatable(skillNameKey)).withStyle(ChatFormatting.GREEN));
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
        tooltipComponents.add(Component.translatable("tooltip.tutien.skill_tier", tier.getColoredName()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.tutien.skill_type", type.getNameComponent()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.tutien.skill_qi_cost", (int) qiCost).withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Component.translatable("tooltip.tutien.skill_cooldown", cooldownSeconds).withStyle(ChatFormatting.YELLOW));
        tooltipComponents.add(type.getDescComponent().withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(Component.translatable("tooltip.tutien.skill_learn_hint").withStyle(ChatFormatting.GREEN));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
