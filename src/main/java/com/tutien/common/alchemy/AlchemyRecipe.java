package com.tutien.common.alchemy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tutien.common.registry.ModRecipeSerializers;
import com.tutien.common.registry.ModRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public class AlchemyRecipe implements Recipe<AlchemyRecipe.AlchemyInput> {
    private final NonNullList<Ingredient> ingredients;
    private final Ingredient catalyst;
    private final ItemStack result;
    private final int requiredCauldronTier;
    private final int requiredAlchemyLevel;
    private final int craftingTime;

    public AlchemyRecipe(NonNullList<Ingredient> ingredients, Ingredient catalyst, ItemStack result,
                         int requiredCauldronTier, int requiredAlchemyLevel, int craftingTime) {
        this.ingredients = ingredients;
        this.catalyst = catalyst;
        this.result = result;
        this.requiredCauldronTier = requiredCauldronTier;
        this.requiredAlchemyLevel = requiredAlchemyLevel;
        this.craftingTime = craftingTime;
    }

    public Ingredient getCatalyst() { return catalyst; }
    public int getRequiredCauldronTier() { return requiredCauldronTier; }
    public int getRequiredAlchemyLevel() { return requiredAlchemyLevel; }
    public int getCraftingTime() { return craftingTime; }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> all = NonNullList.create();
        all.addAll(ingredients);
        all.add(catalyst);
        return all;
    }

    public NonNullList<Ingredient> getHerbIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(AlchemyInput input, Level level) {
        if (!catalyst.test(input.catalyst())) return false;
        if (input.cauldronTier() < requiredCauldronTier) return false;

        // Check 3 herb inputs against ingredients list
        List<ItemStack> items = List.of(input.herb1(), input.herb2(), input.herb3());
        boolean[] matched = new boolean[ingredients.size()];

        for (ItemStack item : items) {
            if (item.isEmpty()) continue;
            boolean found = false;
            for (int i = 0; i < ingredients.size(); i++) {
                if (!matched[i] && ingredients.get(i).test(item)) {
                    matched[i] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }

        for (boolean m : matched) {
            if (!m) return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(AlchemyInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.ALCHEMY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.ALCHEMY_TYPE.get();
    }

    public record AlchemyInput(ItemStack herb1, ItemStack herb2, ItemStack herb3, ItemStack catalyst, int cauldronTier) implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return switch (index) {
                case 0 -> herb1;
                case 1 -> herb2;
                case 2 -> herb3;
                case 3 -> catalyst;
                default -> ItemStack.EMPTY;
            };
        }

        @Override
        public int size() {
            return 4;
        }
    }

    public static class Serializer implements RecipeSerializer<AlchemyRecipe> {
        public static final MapCodec<AlchemyRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(r -> r.ingredients),
                Ingredient.CODEC.fieldOf("catalyst").forGetter(r -> r.catalyst),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result),
                Codec.INT.optionalFieldOf("requiredCauldronTier", 1).forGetter(r -> r.requiredCauldronTier),
                Codec.INT.optionalFieldOf("requiredAlchemyLevel", 1).forGetter(r -> r.requiredAlchemyLevel),
                Codec.INT.optionalFieldOf("craftingTime", 100).forGetter(r -> r.craftingTime)
        ).apply(inst, (ing, cat, res, cTier, aLvl, time) -> {
            NonNullList<Ingredient> list = NonNullList.create();
            list.addAll(ing);
            return new AlchemyRecipe(list, cat, res, cTier, aLvl, time);
        }));

        public static final StreamCodec<RegistryFriendlyByteBuf, AlchemyRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeVarInt(recipe.ingredients.size());
                    for (Ingredient ing : recipe.ingredients) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
                    }
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.catalyst);
                    ItemStack.STREAM_CODEC.encode(buf, recipe.result);
                    buf.writeVarInt(recipe.requiredCauldronTier);
                    buf.writeVarInt(recipe.requiredAlchemyLevel);
                    buf.writeVarInt(recipe.craftingTime);
                },
                buf -> {
                    int size = buf.readVarInt();
                    NonNullList<Ingredient> ingredients = NonNullList.create();
                    for (int i = 0; i < size; i++) {
                        ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                    }
                    Ingredient catalyst = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                    int cTier = buf.readVarInt();
                    int aLvl = buf.readVarInt();
                    int time = buf.readVarInt();
                    return new AlchemyRecipe(ingredients, catalyst, result, cTier, aLvl, time);
                }
        );

        @Override
        public MapCodec<AlchemyRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AlchemyRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
