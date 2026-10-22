package com.tutien.common.compat.jei;

import com.tutien.TuTienMod;
import com.tutien.common.refining.RefiningRecipe;
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

public class RefiningRecipeCategory implements IRecipeCategory<RefiningRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final Component title;

    public RefiningRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 72);
        this.icon = guiHelper.createDrawableItemLike(ModBlocks.LO_LUYEN_KHI.get());
        this.arrow = guiHelper.createAnimatedRecipeArrow(120);
        this.title = Component.translatable("block.tutien.lo_luyen_khi");
    }

    @Override
    public RecipeType<RefiningRecipe> getRecipeType() {
        return TuTienJeiPlugin.REFINING_JEI_TYPE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, RefiningRecipe recipe, IFocusGroup focuses) {
        // Blueprint slot (left)
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 20)
                .addIngredients(recipe.getBlueprint())
                .setStandardSlotBackground();

        // Material slots (3 slots)
        List<Ingredient> materials = recipe.getMaterials();
        for (int i = 0; i < 3; i++) {
            var slot = builder.addSlot(RecipeIngredientRole.INPUT, 30 + i * 20, 10).setStandardSlotBackground();
            if (i < materials.size()) {
                slot.addIngredients(materials.get(i));
            }
        }

        // Catalyst slot
        builder.addSlot(RecipeIngredientRole.INPUT, 50, 34)
                .addIngredients(recipe.getCatalyst())
                .setStandardSlotBackground();

        // Output slot
        ItemStack result = recipe.getResultItem(Minecraft.getInstance().level != null ? Minecraft.getInstance().level.registryAccess() : null);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 126, 20)
                .addItemStack(result)
                .setOutputSlotBackground();
    }

    @Override
    public void draw(RefiningRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.arrow.draw(guiGraphics, 95, 21);

        Minecraft mc = Minecraft.getInstance();
        String tierName = switch (recipe.getRequiredForgeTier()) {
            case 1 -> "Hoàng";
            case 2 -> "Huyền";
            case 3 -> "Địa";
            case 4 -> "Thiên";
            default -> "Cấp " + recipe.getRequiredForgeTier();
        };
        String tierStr = "Lò: " + tierName;
        String lvlStr = "Luyện khí: Cấp " + recipe.getRequiredRefiningLevel();
        String timeStr = (recipe.getCraftingTime() / 20) + "s";

        guiGraphics.drawString(mc.font, tierStr, 6, 58, 0x888888, false);
        guiGraphics.drawString(mc.font, lvlStr, 62, 58, 0x888888, false);
        guiGraphics.drawString(mc.font, timeStr, 136, 58, 0x888888, false);
    }
}
