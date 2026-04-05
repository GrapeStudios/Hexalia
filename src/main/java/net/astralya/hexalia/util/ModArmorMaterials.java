package net.astralya.hexalia.util;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ModArmorMaterials {

    private static final Map<ArmorItem.Type, Integer> BASE_DURABILITY = createBaseDurability();
    private static final Map<ArmorItem.Type, Integer> SILKWEAVE_PROTECTION = createSilkweaveProtection();
    private static final Map<ArmorItem.Type, Integer> MOONWEAVE_PROTECTION = createMoonweaveProtection();

    private ModArmorMaterials() {
    }

    public static final ArmorMaterial SILKWEAVE = create(
            "silkweave",
            22,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            () -> Ingredient.ofItems(ModItems.SILK_FIBER),
            SILKWEAVE_PROTECTION,
            15,
            0.0F,
            0.0F
    );

    public static final ArmorMaterial MOONWEAVE = create(
            "moonweave",
            22,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            () -> Ingredient.ofItems(ModItems.CELESTIAL_CRYSTAL),
            MOONWEAVE_PROTECTION,
            15,
            0.0F,
            0.0F
    );

    private static ArmorMaterial create(String name, int durabilityMultiplier, SoundEvent equipSound, Supplier<Ingredient> repairIngredient, Map<ArmorItem.Type, Integer> protectionAmounts, int enchantability, float toughness, float knockbackResistance) {
        return new ArmorMaterial() {
            @Override
            public int getDurability(ArmorItem.Type type) {
                return BASE_DURABILITY.get(type) * durabilityMultiplier;
            }

            @Override
            public int getProtection(ArmorItem.Type type) {
                return protectionAmounts.get(type);
            }

            @Override
            public int getEnchantability() {
                return enchantability;
            }

            @Override
            public SoundEvent getEquipSound() {
                return equipSound;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return repairIngredient.get();
            }

            @Override
            public String getName() {
                return HexaliaMod.MODID + ":" + name;
            }

            @Override
            public float getToughness() {
                return toughness;
            }

            @Override
            public float getKnockbackResistance() {
                return knockbackResistance;
            }
        };
    }

    private static Map<ArmorItem.Type, Integer> createBaseDurability() {
        Map<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.BOOTS, 13);
        map.put(ArmorItem.Type.LEGGINGS, 15);
        map.put(ArmorItem.Type.CHESTPLATE, 16);
        map.put(ArmorItem.Type.HELMET, 11);
        return map;
    }

    private static Map<ArmorItem.Type, Integer> createSilkweaveProtection() {
        Map<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.BOOTS, 2);
        map.put(ArmorItem.Type.LEGGINGS, 5);
        map.put(ArmorItem.Type.CHESTPLATE, 6);
        map.put(ArmorItem.Type.HELMET, 2);
        return map;
    }

    private static Map<ArmorItem.Type, Integer> createMoonweaveProtection() {
        Map<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.BOOTS, 3);
        map.put(ArmorItem.Type.LEGGINGS, 6);
        map.put(ArmorItem.Type.CHESTPLATE, 8);
        map.put(ArmorItem.Type.HELMET, 3);
        return map;
    }

    public static void registerModArmorMaterials() {
        HexaliaMod.LOGGER.info("Registering Armor Materials for " + HexaliaMod.MODID);
    }
}