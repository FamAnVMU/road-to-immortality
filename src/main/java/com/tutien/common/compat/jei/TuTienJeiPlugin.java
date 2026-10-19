package com.tutien.common.compat.jei;

import com.tutien.TuTienMod;
import com.tutien.common.alchemy.AlchemyRecipe;
import com.tutien.common.refining.RefiningRecipe;
import com.tutien.common.registry.ModBlocks;
import com.tutien.common.registry.ModRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

@JeiPlugin
public class TuTienJeiPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_UID =
            ResourceLocation.fromNamespaceAndPath(TuTienMod.MOD_ID, "jei_plugin");

    public static final RecipeType<AlchemyRecipe> ALCHEMY_JEI_TYPE =
            RecipeType.create(TuTienMod.MOD_ID, "alchemy", AlchemyRecipe.class);

    public static final RecipeType<RefiningRecipe> REFINING_JEI_TYPE =
            RecipeType.create(TuTienMod.MOD_ID, "refining", RefiningRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.LO_LUYEN_DAN.get()), ALCHEMY_JEI_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.LO_LUYEN_KHI.get()), REFINING_JEI_TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            List<AlchemyRecipe> alchemyRecipes = mc.level.getRecipeManager()
                    .getAllRecipesFor(ModRecipeTypes.ALCHEMY_TYPE.get())
                    .stream().map(RecipeHolder::value).toList();
            registration.addRecipes(ALCHEMY_JEI_TYPE, alchemyRecipes);

            List<RefiningRecipe> refiningRecipes = mc.level.getRecipeManager()
                    .getAllRecipesFor(ModRecipeTypes.REFINING_TYPE.get())
                    .stream().map(RecipeHolder::value).toList();
            registration.addRecipes(REFINING_JEI_TYPE, refiningRecipes);
        }
    }
}
