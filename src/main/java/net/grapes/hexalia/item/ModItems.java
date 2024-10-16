package net.grapes.hexalia.item;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.item.custom.*;
import net.grapes.hexalia.item.custom.brews.*;
import net.grapes.hexalia.util.ModToolTiers;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, HexaliaMod.MOD_ID);

    // Resources
    public static final RegistryObject<Item> SALT = ITEMS.register("salt",
            () -> new BlockItem(ModBlocks.SALT.get(), new Item.Properties()));
    public static final RegistryObject<Item> RESIN = ITEMS.register("resin",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ANCIENT_SEED = ITEMS.register("ancient_seed",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> SILK_FIBER = ITEMS.register("silk_fiber",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SILKWORM = ITEMS.register("silkworm",
            () -> new Item(new Item.Properties()));

    // Crops, Plants & Seeds
    public static final RegistryObject<Item> SIREN_KELP = ITEMS.register("siren_kelp",
            () -> new BlockItem(ModBlocks.SIREN_KELP.get(), new Item.Properties().food(ModFoodProperties.SIREN_KELP)));
    public static final RegistryObject<Item> MANDRAKE = ITEMS.register("mandrake",
            () -> new MandrakeItem(new Item.Properties()));
    public static final RegistryObject<Item> MANDRAKE_SEEDS = ITEMS.register("mandrake_seeds",
            () -> new ItemNameBlockItem(ModBlocks.MANDRAKE_CROP.get(), new Item.Properties()));
    public static final RegistryObject<Item> CHILLBERRIES = ITEMS.register("chillberries",
            () -> new BlockItem(ModBlocks.CHILLBERRY_BUSH.get(), new Item.Properties().food(ModFoodProperties.CHILLBERRIES)));
    public static final RegistryObject<Item> SUNFIRE_TOMATO = ITEMS.register("sunfire_tomato",
            () -> new Item(new Item.Properties().food(ModFoodProperties.SUNFIRE_TOMATO)));
    public static final RegistryObject<Item> SUNFIRE_TOMATO_SEEDS = ITEMS.register("sunfire_tomato_seeds",
            () -> new ItemNameBlockItem(ModBlocks.SUNFIRE_TOMATO_CROP.get(), new Item.Properties()));
    public static final RegistryObject<Item> RABBAGE_SEEDS = ITEMS.register("rabbage_seeds",
            () -> new ItemNameBlockItem(ModBlocks.RABBAGE_CROP.get(), new Item.Properties()));
    public static final RegistryObject<Item> LOTUS_FLOWER = ITEMS.register("lotus_flower",
            () -> new PlaceOnWaterBlockItem(ModBlocks.LOTUS_FLOWER.get(), new Item.Properties()));
    public static final RegistryObject<Item> SALTSPROUT = ITEMS.register("saltsprout",
            () -> new BlockItem(ModBlocks.SALTSPROUT.get(), new Item.Properties().food(ModFoodProperties.SALTSPROUT)));
    public static final RegistryObject<Item> DUCKWEED = ITEMS.register("duckweed",
            () -> new PlaceOnWaterBlockItem(ModBlocks.DUCKWEED.get(), new Item.Properties()));

    // Refined Resources
    public static final RegistryObject<Item> SIREN_KELP_PASTE = ITEMS.register("siren_kelp_paste",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPIRIT_BLOOM_POWDER = ITEMS.register("spirit_bloom_powder",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DREAMSHROOM_PASTE = ITEMS.register("dreamshroom_paste",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GHOST_FERN_POWDER = ITEMS.register("ghost_fern_powder",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PURIFYING_SALTS = ITEMS.register("purifying_salts",
            () -> new PurifyingSaltsItem(new Item.Properties()));
    public static final RegistryObject<Item> SPICY_SANDWICH = ITEMS.register("spicy_sandwich",
            () -> new Item(new Item.Properties().food(ModFoodProperties.SPICY_SANDWICH)));
    public static final RegistryObject<Item> CHILLBERRY_PIE = ITEMS.register("chillberry_pie",
            () -> new Item(new Item.Properties().food(ModFoodProperties.CHILLBERRY_PIE)));
    public static final RegistryObject<Item> MANDRAKE_STEW = ITEMS.register("mandrake_stew",
            () -> new Item(new Item.Properties().food(ModFoodProperties.MANDRAKE_STEW)));

    // Brews
    public static final RegistryObject<Item> RUSTIC_BOTTLE = ITEMS.register("rustic_bottle",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BREW_OF_SPIKESKIN = ITEMS.register("brew_of_spikeskin",
            () -> new SpikeskinBrewItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> BREW_OF_BLOODLUST = ITEMS.register("brew_of_bloodlust",
            () -> new BoodlustBrewItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> BREW_OF_SLIMEWALKER = ITEMS.register("brew_of_slimewalker",
            () -> new SlimewalkerBrewItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> BREW_OF_HOMESTEAD = ITEMS.register("brew_of_homestead",
            () -> new HomesteadBrewItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> BREW_OF_SIPHON = ITEMS.register("brew_of_siphon",
            () -> new SiphonBrewItem(new Item.Properties().stacksTo(16)));

    // Tools & Others
    public static final RegistryObject<Item> MORTAR_AND_PESTLE = ITEMS.register("mortar_and_pestle",
            () -> new MortarAndPestleItem(new Item.Properties()));
    public static final RegistryObject<Item> SMALL_CAULDRON = ITEMS.register("small_cauldron",
            () -> new BlockItem(ModBlocks.SMALL_CAULDRON.get(), new Item.Properties()));
    public static final RegistryObject<Item> SALT_LAMP = ITEMS.register("salt_lamp",
            () -> new BlockItem(ModBlocks.SALT_LAMP.get(), new Item.Properties()));
    public static final RegistryObject<Item> STONE_DAGGER = ITEMS.register("stone_dagger",
            () -> new StoneDaggerItem(new Item.Properties().durability(16)));
    public static final RegistryObject<Item> RITUAL_TABLE = ITEMS.register("ritual_table",
            () -> new BlockItem(ModBlocks.RITUAL_TABLE.get(),new Item.Properties()));
    public static final RegistryObject<Item> CANDLE_SKULL = ITEMS.register("candle_skull",
            () -> new BlockItem(ModBlocks.CANDLE_SKULL.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> HEX_FOCUS = ITEMS.register("hex_focus",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WISDOM_GEM = ITEMS.register("wisdom_gem",
            () -> new Item(new Item.Properties().durability(60).rarity(Rarity.UNCOMMON)));

    // Entity
    public static final RegistryObject<Item> BOTTLED_MOTH = ITEMS.register("bottled_moth",
            () -> new Item(new Item.Properties()));

    // Weapons & Armor
    public static final RegistryObject<Item> KELPWEAVE_BLADE = ITEMS.register("kelpweave_blade",
            () -> new KelpweaveBlade(ModToolTiers.ANCIENT, 3, -2f,
                    new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> RABBAGE = ITEMS.register("rabbage",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GHOSTVEIL = ITEMS.register("ghostveil",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EARPLUGS = ITEMS.register("earplugs",
            () -> new ArmorItem(ModArmorMaterials.EARPLUGS, ArmorItem.Type.HELMET, new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
