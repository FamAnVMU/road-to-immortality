package com.tutien.common.compat.jei;

import com.tutien.TuTienMod;
import com.tutien.common.alchemy.AlchemyRecipe;
import com.tutien.common.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class AlchemyRecipeCategory implements IRecipeCategory<AlchemyRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final Component title;

    public AlchemyRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 72);
        this.icon = guiHelper.createDrawableItemLike(ModBlocks.LO_LUYEN_DAN.get());
        this.arrow = guiHelper.createAnimatedRecipeArrow(100);
        this.title = Component.translatable("block.tutien.lo_luyen_dan");
    }

    @Override
    public RecipeType<AlchemyRecipe> getRecipeType() {
        return TuTienJeiPlugin.ALCHEMY_JEI_TYPE;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public int getWidth() {
        return 160;
    }

    @Override
    public int getHeight() {
        return 72;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlchemyRecipe recipe, IFocusGroup focuses) {
        List<Ingredient> herbs = recipe.getHerbIngredients();
        for (int i = 0; i < 3; i++) {
            var slot = builder.addSlot(RecipeIngredientRole.INPUT, 12 + i * 22, 10).setStandardSlotBackground();
            if (i < herbs.size()) {
                slot.addIngredients(herbs.get(i));
            }
        }

        builder.addSlot(RecipeIngredientRole.INPUT, 34, 34)
                .addIngredients(recipe.getCatalyst())
                .setStandardSlotBackground();

        ItemStack result = recipe.getResultItem(Minecraft.getInstance().level != null ? Minecraft.getInstance().level.registryAccess() : null);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 126, 20)
                .addItemStack(result)
                .setOutputSlotBackground();
    }

    @Override
    public void draw(AlchemyRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.arrow.draw(guiGraphics, 85, 21);

        Minecraft mc = Minecraft.getInstance();
        String tierStr = "Lò: Cấp " + recipe.getRequiredCauldronTier();
        String lvlStr = "Luyện đan: Cấp " + recipe.getRequiredAlchemyLevel();
        String timeStr = (recipe.getCraftingTime() / 20) + "s";

        guiGraphics.drawString(mc.font, tierStr, 8, 58, 0x888888, false);
        guiGraphics.drawString(mc.font, lvlStr, 65, 58, 0x888888, false);
        guiGraphics.drawString(mc.font, timeStr, 136, 58, 0x888888, false);
    }
}
