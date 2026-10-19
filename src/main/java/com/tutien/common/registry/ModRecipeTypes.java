package com.tutien.common.registry;

import com.tutien.TuTienMod;
import com.tutien.common.alchemy.AlchemyRecipe;
import com.tutien.common.refining.RefiningRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, TuTienMod.MOD_ID);

    public static final Supplier<RecipeType<AlchemyRecipe>> ALCHEMY_TYPE =
            RECIPE_TYPES.register("alchemy", () -> RecipeType.simple(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(TuTienMod.MOD_ID, "alchemy")));

    public static final Supplier<RecipeType<RefiningRecipe>> REFINING_TYPE =
            RECIPE_TYPES.register("refining", () -> RecipeType.simple(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(TuTienMod.MOD_ID, "refining")));
}
