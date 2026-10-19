package com.tutien.common.registry;

import com.tutien.TuTienMod;
import com.tutien.common.alchemy.AlchemyRecipe;
import com.tutien.common.refining.RefiningRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, TuTienMod.MOD_ID);

    public static final Supplier<RecipeSerializer<AlchemyRecipe>> ALCHEMY_SERIALIZER =
            RECIPE_SERIALIZERS.register("alchemy", AlchemyRecipe.Serializer::new);

    public static final Supplier<RecipeSerializer<RefiningRecipe>> REFINING_SERIALIZER =
            RECIPE_SERIALIZERS.register("refining", RefiningRecipe.Serializer::new);
}
