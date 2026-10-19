package com.tutien.common.refining;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class TuTienArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, "tutien");

    public static final Holder<ArmorMaterial> HOANG_DAO_BAO = ARMOR_MATERIALS.register("hoang_dao_bao", () ->
            createMaterial("hoang_dao_bao", 25, Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 3);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.CHESTPLATE, 8);
                map.put(ArmorItem.Type.HELMET, 3);
            }), 15, 1.0f, 0.0f));

    public static final Holder<ArmorMaterial> HUYEN_DAO_BAO = ARMOR_MATERIALS.register("huyen_dao_bao", () ->
            createMaterial("huyen_dao_bao", 40, Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 5);
                map.put(ArmorItem.Type.LEGGINGS, 8);
                map.put(ArmorItem.Type.CHESTPLATE, 12);
                map.put(ArmorItem.Type.HELMET, 5);
            }), 20, 2.5f, 0.1f));

    public static final Holder<ArmorMaterial> DIA_DAO_BAO = ARMOR_MATERIALS.register("dia_dao_bao", () ->
            createMaterial("dia_dao_bao", 70, Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 8);
                map.put(ArmorItem.Type.LEGGINGS, 12);
                map.put(ArmorItem.Type.CHESTPLATE, 18);
                map.put(ArmorItem.Type.HELMET, 8);
            }), 25, 4.0f, 0.2f));

    public static final Holder<ArmorMaterial> THIEN_DAO_BAO = ARMOR_MATERIALS.register("thien_dao_bao", () ->
            createMaterial("thien_dao_bao", 120, Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 12);
                map.put(ArmorItem.Type.LEGGINGS, 18);
                map.put(ArmorItem.Type.CHESTPLATE, 28);
                map.put(ArmorItem.Type.HELMET, 12);
            }), 30, 6.0f, 0.3f));

    private static ArmorMaterial createMaterial(String name, int durabilityMultiplier, EnumMap<ArmorItem.Type, Integer> defense, int enchantability, float toughness, float knockbackResistance) {
        return new ArmorMaterial(
                defense,
                enchantability,
                SoundEvents.ARMOR_EQUIP_NETHERITE,
                () -> Ingredient.EMPTY,
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("tutien", name))),
                toughness,
                knockbackResistance
        );
    }
}
