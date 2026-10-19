package com.tutien.common.refining;

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

public class RefiningRecipe implements Recipe<RefiningRecipe.RefiningInput> {
    private final Ingredient blueprint;
    private final NonNullList<Ingredient> materials;
    private final Ingredient catalyst;
    private final ItemStack result;
    private final int requiredForgeTier;
    private final int requiredRefiningLevel;
    private final int craftingTime;

    public RefiningRecipe(Ingredient blueprint, NonNullList<Ingredient> materials, Ingredient catalyst,
                          ItemStack result, int requiredForgeTier, int requiredRefiningLevel, int craftingTime) {
        this.blueprint = blueprint;
        this.materials = materials;
        this.catalyst = catalyst;
        this.result = result;
        this.requiredForgeTier = requiredForgeTier;
        this.requiredRefiningLevel = requiredRefiningLevel;
        this.craftingTime = craftingTime;
    }

    public Ingredient getBlueprint() { return blueprint; }
    public NonNullList<Ingredient> getMaterials() { return materials; }
    public Ingredient getCatalyst() { return catalyst; }
    public int getRequiredForgeTier() { return requiredForgeTier; }
    public int getRequiredRefiningLevel() { return requiredRefiningLevel; }
    public int getCraftingTime() { return craftingTime; }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> all = NonNullList.create();
        all.add(blueprint);
        all.addAll(materials);
        all.add(catalyst);
        return all;
    }

    @Override
    public boolean matches(RefiningInput input, Level level) {
        if (!blueprint.test(input.blueprint())) return false;
        if (!catalyst.test(input.catalyst())) return false;
        if (input.forgeTier() < requiredForgeTier) return false;

        List<ItemStack> items = List.of(input.mat1(), input.mat2(), input.mat3());
        boolean[] matched = new boolean[materials.size()];

        for (ItemStack item : items) {
            if (item.isEmpty()) continue;
            boolean found = false;
            for (int i = 0; i < materials.size(); i++) {
                if (!matched[i] && materials.get(i).test(item)) {
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
    public ItemStack assemble(RefiningInput input, HolderLookup.Provider registries) {
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
        return ModRecipeSerializers.REFINING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.REFINING_TYPE.get();
    }

    public record RefiningInput(ItemStack blueprint, ItemStack mat1, ItemStack mat2, ItemStack mat3, ItemStack catalyst, int forgeTier) implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return switch (index) {
                case 0 -> blueprint;
                case 1 -> mat1;
                case 2 -> mat2;
                case 3 -> mat3;
                case 4 -> catalyst;
                default -> ItemStack.EMPTY;
            };
        }

        @Override
        public int size() {
            return 5;
        }
    }

    public static class Serializer implements RecipeSerializer<RefiningRecipe> {
        public static final MapCodec<RefiningRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("blueprint").forGetter(r -> r.blueprint),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("materials").forGetter(r -> r.materials),
                Ingredient.CODEC.fieldOf("catalyst").forGetter(r -> r.catalyst),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result),
                Codec.INT.optionalFieldOf("requiredForgeTier", 1).forGetter(r -> r.requiredForgeTier),
                Codec.INT.optionalFieldOf("requiredRefiningLevel", 1).forGetter(r -> r.requiredRefiningLevel),
                Codec.INT.optionalFieldOf("craftingTime", 120).forGetter(r -> r.craftingTime)
        ).apply(inst, (bp, mats, cat, res, fTier, rLvl, time) -> {
            NonNullList<Ingredient> list = NonNullList.create();
            list.addAll(mats);
            return new RefiningRecipe(bp, list, cat, res, fTier, rLvl, time);
        }));

        public static final StreamCodec<RegistryFriendlyByteBuf, RefiningRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.blueprint);
                    buf.writeVarInt(recipe.materials.size());
                    for (Ingredient ing : recipe.materials) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
                    }
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.catalyst);
                    ItemStack.STREAM_CODEC.encode(buf, recipe.result);
                    buf.writeVarInt(recipe.requiredForgeTier);
                    buf.writeVarInt(recipe.requiredRefiningLevel);
                    buf.writeVarInt(recipe.craftingTime);
                },
                buf -> {
                    Ingredient blueprint = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    int size = buf.readVarInt();
                    NonNullList<Ingredient> materials = NonNullList.create();
                    for (int i = 0; i < size; i++) {
                        materials.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                    }
                    Ingredient catalyst = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                    int fTier = buf.readVarInt();
                    int rLvl = buf.readVarInt();
                    int time = buf.readVarInt();
                    return new RefiningRecipe(blueprint, materials, catalyst, result, fTier, rLvl, time);
                }
        );

        @Override
        public MapCodec<RefiningRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RefiningRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
