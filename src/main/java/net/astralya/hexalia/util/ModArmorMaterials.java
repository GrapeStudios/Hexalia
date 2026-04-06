package net.astralya.hexalia.util;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.List;

public final class ModArmorMaterials {

    private ModArmorMaterials() {
    }

    public static final RegistryEntry<ArmorMaterial> SILKWEAVE = register("silkweave",
            new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 2);
                        map.put(ArmorItem.Type.LEGGINGS, 5);
                        map.put(ArmorItem.Type.CHESTPLATE, 6);
                        map.put(ArmorItem.Type.HELMET, 2);
                        map.put(ArmorItem.Type.BODY, 0);
                    }),
                    22,
                    SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.ofItems(ModItems.SILK_FIBER),
                    List.of(new ArmorMaterial.Layer(Identifier.of(HexaliaMod.MODID, "silkweave"))),
                    0.0F,
                    0.0F
            ));

    public static final RegistryEntry<ArmorMaterial> MOONWEAVE = register("moonweave",
            new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 3);
                        map.put(ArmorItem.Type.LEGGINGS, 6);
                        map.put(ArmorItem.Type.CHESTPLATE, 8);
                        map.put(ArmorItem.Type.HELMET, 3);
                        map.put(ArmorItem.Type.BODY, 0);
                    }),
                    22,
                    SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.ofItems(ModItems.CELESTIAL_CRYSTAL),
                    List.of(new ArmorMaterial.Layer(Identifier.of(HexaliaMod.MODID, "moonweave"))),
                    0.0F,
                    0.0F
            ));

    private static RegistryEntry<ArmorMaterial> register(String name, ArmorMaterial material) {
        return Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of(HexaliaMod.MODID, name), material);
    }

    public static void registerModArmorMaterials() {
        HexaliaMod.LOGGER.info("Registering Armor Materials for " + HexaliaMod.MODID);
    }
}