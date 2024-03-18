package net.grapes.hexalia.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.item.custom.*;
import net.grapes.hexalia.item.custom.brews.SlimeyStepBrewItem;
import net.grapes.hexalia.item.custom.brews.VigorBrewItem;
import net.grapes.hexalia.item.custom.brews.WardingBrewItem;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.StewItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    // Resources
    public static final Item SALT = registerItem("salt",
            new Item(new FabricItemSettings()));
    public static final Item SIREN_KELP = registerItem("siren_kelp",
            new BlockItem(ModBlocks.SIREN_KELP, new FabricItemSettings()));
    public static final Item MANDRAKE = registerItem("mandrake",
            new MandrakeItem(new FabricItemSettings()));
    public static final Item MANDRAKE_SEEDS = registerItem("mandrake_seeds",
            new AliasedBlockItem(ModBlocks.MANDRAKE_CROP, new FabricItemSettings()));
    public static final Item MANDRAKE_STEW = registerItem("mandrake_stew",
            new StewItem(new FabricItemSettings().food(ModFoodComponents.MANDRAKE_STEW).maxCount(1)));
    public static final Item CHILLBERRIES = registerItem("chillberries",
            new BlockItem(ModBlocks.CHILLBERRY_BUSH, new FabricItemSettings().food(ModFoodComponents.CHILLBERRIES)));
    public static final Item CHILLBERRY_CUPCAKE = registerItem("chillberry_cupcake",
            new Item(new FabricItemSettings().food(ModFoodComponents.CHILLBERRY_CUPCAKE)));
    public static final Item SUNFIRE_TOMATO = registerItem("sunfire_tomato",
            new Item(new FabricItemSettings().food(ModFoodComponents.SUNFIRE_TOMATO)));
    public static final Item SUNFIRE_TOMATO_SEEDS = registerItem("sunfire_tomato_seeds",
            new AliasedBlockItem(ModBlocks.SUNFIRE_TOMATO_CROP, new FabricItemSettings()));
    public static final Item SPICY_SANDWICH = registerItem("spicy_sandwich",
            new Item(new FabricItemSettings().food(ModFoodComponents.SPICY_SANDWICH)));

    // Refined Resources
    public static final Item SIREN_KELP_PASTE = registerItem("siren_kelp_paste",
            new Item(new FabricItemSettings()));
    public static final Item SPIRIT_BLOOM_DUST = registerItem("spirit_bloom_dust",
            new Item(new FabricItemSettings()));
    public static final Item DREAMSHROOM_PASTE = registerItem("dreamshroom_paste",
            new Item(new FabricItemSettings()));

    // Brews
    public static final Item BREW_OF_WARDING = registerItem("brew_of_warding",
            new WardingBrewItem(new FabricItemSettings().recipeRemainder(ModItems.RUSTIC_BOTTLE).maxCount(16)));
    public static final Item BREW_OF_VIGOR = registerItem("brew_of_vigor",
            new VigorBrewItem(new FabricItemSettings().recipeRemainder(ModItems.RUSTIC_BOTTLE).maxCount(16)));
    public static final Item BREW_OF_SLIMEY_STEP = registerItem("brew_of_slimey_step",
            new SlimeyStepBrewItem(new FabricItemSettings().recipeRemainder(ModItems.RUSTIC_BOTTLE).maxCount(16)));

    // Tools & Others
    public static final Item MORTAR_AND_PESTLE = registerItem("mortar_and_pestle",
            new MortarAndPestleItem(new FabricItemSettings()));
    public static final Item SMALL_CAULDRON = registerItem("small_cauldron",
            new BlockItem(ModBlocks.SMALL_CAULDRON, new FabricItemSettings()));
    public static final Item SALT_LAMP = registerItem("salt_lamp",
            new BlockItem(ModBlocks.SALT_LAMP, new FabricItemSettings()));
    public static final Item RUSTIC_BOTTLE = registerItem("rustic_bottle",
            new Item(new FabricItemSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(HexaliaMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        HexaliaMod.LOGGER.info("Registering Items for " + HexaliaMod.MOD_ID);
    }
}