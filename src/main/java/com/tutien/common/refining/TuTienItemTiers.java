package com.tutien.common.refining;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public enum TuTienItemTiers implements Tier {
    HOANG(1500, 7.0f, 6.0f, 2, 15, () -> Ingredient.EMPTY, BlockTags.INCORRECT_FOR_DIAMOND_TOOL),
    HUYEN(3000, 9.0f, 12.0f, 3, 20, () -> Ingredient.EMPTY, BlockTags.INCORRECT_FOR_NETHERITE_TOOL),
    DIA(6000, 12.0f, 24.0f, 4, 25, () -> Ingredient.EMPTY, BlockTags.INCORRECT_FOR_NETHERITE_TOOL),
    THIEN(15000, 16.0f, 50.0f, 5, 30, () -> Ingredient.EMPTY, BlockTags.INCORRECT_FOR_NETHERITE_TOOL);

    private final int uses;
    private final float speed;
    private final float damage;
    private final int level;
    private final int enchantmentValue;
    private final java.util.function.Supplier<Ingredient> repairIngredient;
    private final TagKey<Block> incorrectBlocksForDrops;

    TuTienItemTiers(int uses, float speed, float damage, int level, int enchantmentValue,
                    java.util.function.Supplier<Ingredient> repairIngredient, TagKey<Block> incorrectBlocksForDrops) {
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.level = level;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
    }

    @Override
    public int getUses() { return uses; }

    @Override
    public float getSpeed() { return speed; }

    @Override
    public float getAttackDamageBonus() { return damage; }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() { return incorrectBlocksForDrops; }

    @Override
    public int getEnchantmentValue() { return enchantmentValue; }

    @Override
    public Ingredient getRepairIngredient() { return repairIngredient.get(); }
}
