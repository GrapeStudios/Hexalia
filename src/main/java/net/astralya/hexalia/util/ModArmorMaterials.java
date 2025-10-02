package net.astralya.hexalia.util;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ModArmorMaterials {

    public static final RegistryEntry<ArmorMaterial> BOGGED = registerArmorMaterial("bogged",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 1);
                map.put(ArmorItem.Type.LEGGINGS, 4);
                map.put(ArmorItem.Type.CHESTPLATE, 5);
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.BODY, 0);
            }), 12, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, () -> Ingredient.ofItems(Items.DRIED_KELP),
                    List.of(new ArmorMaterial.Layer(Identifier.of(HexaliaMod.MODID, "bogged"))), 0.0f, 0));

    public static final RegistryEntry<ArmorMaterial> GHOST = registerArmorMaterial("ghost",
            () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 1);
                        map.put(ArmorItem.Type.LEGGINGS, 1);
                        map.put(ArmorItem.Type.CHESTPLATE, 3);
                        map.put(ArmorItem.Type.HELMET, 1);
                        map.put(ArmorItem.Type.BODY, 0);
                    }),
                    10,
                    SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.ofItems(ModBlocks.GHOST_FERN.asItem()),
                    List.of(new ArmorMaterial.Layer(Identifier.of(HexaliaMod.MODID, "ghost"))),
                    0.0f,
                    0.0f
            )
    );
    public static RegistryEntry<ArmorMaterial> registerArmorMaterial(String name, Supplier<ArmorMaterial> material) {
        return Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of(HexaliaMod.MODID, name), material.get());
    }
}