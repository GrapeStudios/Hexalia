package net.grapes.hexalia.item;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.effect.ModMobEffects;
import net.grapes.hexalia.entity.ModEntities;
import net.grapes.hexalia.entity.custom.ModBoatEntity;
import net.grapes.hexalia.item.custom.*;
import net.grapes.hexalia.util.ModToolTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, HexaliaMod.MOD_ID);

    // Resources
    public static final RegistryObject<Item> SALT = ITEMS.register("salt",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TREE_RESIN = ITEMS.register("tree_resin",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ANCIENT_SEED = ITEMS.register("ancient_seed",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> SILK_FIBER = ITEMS.register("silk_fiber",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SILKWORM = ITEMS.register("silkworm",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MOON_CRYSTAL = ITEMS.register("moon_crystal",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FIRE_NODE = ITEMS.register("fire_node",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WATER_NODE = ITEMS.register("water_node",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> AIR_NODE = ITEMS.register("air_node",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EARTH_NODE = ITEMS.register("earth_node",
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
    public static final RegistryObject<Item> MOON_BERRIES = ITEMS.register("moon_berries",
            () -> new ItemNameBlockItem(ModBlocks.MOON_BERRIES_VINES.get(), new Item.Properties().food(ModFoodProperties.MOON_BERRIES)));
    public static final RegistryObject<Item> LOTUS_FLOWER = ITEMS.register("lotus_flower",
            () -> new PlaceOnWaterBlockItem(ModBlocks.LOTUS_FLOWER.get(), new Item.Properties()));
    public static final RegistryObject<Item> SALTSPROUT = ITEMS.register("saltsprout",
            () -> new BlockItem(ModBlocks.SALTSPROUT.get(), new Item.Properties().food(ModFoodProperties.SALTSPROUT)));
    public static final RegistryObject<Item> DUCKWEED = ITEMS.register("duckweed",
            () -> new PlaceOnWaterBlockItem(ModBlocks.DUCKWEED.get(), new Item.Properties()));

    // Refined Resources
    public static final RegistryObject<Item> SIREN_PASTE = ITEMS.register("siren_paste",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPIRIT_POWDER = ITEMS.register("spirit_powder",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DREAM_PASTE = ITEMS.register("dream_paste",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GHOST_POWDER = ITEMS.register("ghost_powder",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PURIFYING_SALTS = ITEMS.register("purifying_salts",
            () -> new PurifyingSaltsItem(new Item.Properties().durability(6)));
    public static final RegistryObject<Item> SPICY_SANDWICH = ITEMS.register("spicy_sandwich",
            () -> new Item(new Item.Properties().food(ModFoodProperties.SPICY_SANDWICH)));
    public static final RegistryObject<Item> CHILLBERRY_PIE = ITEMS.register("chillberry_pie",
            () -> new Item(new Item.Properties().food(ModFoodProperties.CHILLBERRY_PIE)));
    public static final RegistryObject<Item> MANDRAKE_STEW = ITEMS.register("mandrake_stew",
            () -> new BowlFoodItem(new Item.Properties().food(ModFoodProperties.MANDRAKE_STEW)));
    public static final RegistryObject<Item> MOON_BERRY_COOKIE = ITEMS.register("moon_berry_cookie",
            () -> new Item(new Item.Properties().food(ModFoodProperties.MOON_BERRY_COOKIE)));

    // Brews
    public static final RegistryObject<Item> RUSTIC_BOTTLE = ITEMS.register("rustic_bottle",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BREW_OF_SPIKESKIN = ITEMS.register("brew_of_spikeskin",
            () -> new BrewItem(new Item.Properties().stacksTo(16), ModMobEffects.SPIKESKIN, 2400, 0,
                    Component.translatable("tooltip.hexalia.spikeskin_brew").withStyle(ChatFormatting.BLUE)));
    public static final RegistryObject<Item> BREW_OF_BLOODLUST = ITEMS.register("brew_of_bloodlust",
            () -> new BrewItem(new Item.Properties().stacksTo(16), ModMobEffects.BLOODLUST, 2400, 0,
                    Component.translatable("tooltip.hexalia.bloodlust_brew").withStyle(ChatFormatting.BLUE)));
    public static final RegistryObject<Item> BREW_OF_SLIMEWALKER = ITEMS.register("brew_of_slimewalker",
            () -> new BrewItem(new Item.Properties().stacksTo(16), ModMobEffects.SLIMEWALKER, 2400, 0,
                    Component.translatable("tooltip.hexalia.slimewalker_brew").withStyle(ChatFormatting.BLUE)));
    public static final RegistryObject<Item> BREW_OF_HOMESTEAD = ITEMS.register("brew_of_homestead",
            () -> new HomesteadBrewItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> BREW_OF_SIPHON = ITEMS.register("brew_of_siphon",
            () -> new BrewItem(new Item.Properties().stacksTo(16), ModMobEffects.SIPHON, 2400, 0,
                    Component.translatable("tooltip.hexalia.siphon_brew").withStyle(ChatFormatting.BLUE)));
    public static final RegistryObject<Item> BREW_OF_DAYBLOOM = ITEMS.register("brew_of_daybloom",
            () -> new BrewItem(new Item.Properties().stacksTo(16), ModMobEffects.DAYBLOOM, 2400, 0,
                    Component.translatable("tooltip.hexalia.daybloom").withStyle(ChatFormatting.BLUE)));
    public static final RegistryObject<Item> BREW_OF_ARACHNID_GRACE = ITEMS.register("brew_of_arachnid_grace",
            () -> new BrewItem(new Item.Properties().stacksTo(16), ModMobEffects.ARACHNID_GRACE, 2400, 0,
                    Component.translatable("tooltip.hexalia.arachnid_grace").withStyle(ChatFormatting.BLUE)));

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
    public static final RegistryObject<Item> WITHER_CANDLE_SKULL = ITEMS.register("wither_candle_skull",
            () -> new BlockItem(ModBlocks.WITHER_CANDLE_SKULL.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> HEX_FOCUS = ITEMS.register("hex_focus",
            () -> new HexFocusItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SAGE_PENDANT = ITEMS.register("sage_pendant",
            () -> new Item(new Item.Properties().durability(60).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SILK_IDOL = ITEMS.register("silk_idol",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CLEAR_IDOL = ITEMS.register("clear_idol",
            () -> new WeatherIdol(new Item.Properties()));
    public static final RegistryObject<Item> RAIN_IDOL = ITEMS.register("rain_idol",
            () -> new WeatherIdol(new Item.Properties()));
    public static final RegistryObject<Item> STORM_IDOL = ITEMS.register("storm_idol",
            () -> new WeatherIdol(new Item.Properties()));

    // Entity
    public static final RegistryObject<Item> BOTTLED_MOTH = ITEMS.register("bottled_moth",
            () -> new BottledMothItem(new Item.Properties()));
    public static final RegistryObject<Item> SILK_MOTH_SPAWN_EGG = ITEMS.register("silk_moth_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SILK_MOTH_ENTITY,
                    0xAE8f7A, 0x846552, new Item.Properties()));

    // Weapons & Armor
    public static final RegistryObject<Item> KELPWEAVE_BLADE = ITEMS.register("kelpweave_blade",
            () -> new KelpweaveBlade(ModToolTiers.ANCIENT, 3, -2f,
                    new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> RABBAGE = ITEMS.register("rabbage",
            () -> new RabbageEntity(new Item.Properties()));
    public static final RegistryObject<Item> GHOSTVEIL = ITEMS.register("ghostveil",
            () -> new GhostVeilItem(ModArmorMaterials.GHOST, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> EARPLUGS = ITEMS.register("earplugs",
            () -> new ArmorItem(ModArmorMaterials.EARPLUGS, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> BOGGED_BOOTS = ITEMS.register("bogged_boots",
            () -> new BoggedBootsItem(ModArmorMaterials.BOGGED, ArmorItem.Type.BOOTS, new Item.Properties()));

    // Wood-related Items
    public static final RegistryObject<Item> COTTONWOOD_SIGN = ITEMS.register("cottonwood_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.COTTONWOOD_SIGN.get(),
                    ModBlocks.COTTONWOOD_WALL_SIGN.get()));
    public static final RegistryObject<Item> COTTONWOOD_HANGING_SIGN = ITEMS.register("cottonwood_hanging_sign",
            () -> new HangingSignItem(ModBlocks.COTTONWOOD_HANGING_SIGN.get(),
                    ModBlocks.COTTONWOOD_HANGING_WALL_SIGN.get(), new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> COTTONWOOD_BOAT = ITEMS.register("cottonwood_boat",
            () -> new ModBoatItem(false, ModBoatEntity.Type.COTTONWOOD, new Item.Properties()));
    public static final RegistryObject<Item> COTTONWOOD_CHEST_BOAT = ITEMS.register("cottonwood_chest_boat",
            () -> new ModBoatItem(true, ModBoatEntity.Type.COTTONWOOD, new Item.Properties()));
    
    public static final RegistryObject<Item> WILLOW_SIGN = ITEMS.register("willow_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.WILLOW_SIGN.get(),
                    ModBlocks.WILLOW_WALL_SIGN.get()));
    public static final RegistryObject<Item> WILLOW_HANGING_SIGN = ITEMS.register("willow_hanging_sign",
            () -> new HangingSignItem(ModBlocks.WILLOW_HANGING_SIGN.get(),
                    ModBlocks.WILLOW_HANGING_WALL_SIGN.get(), new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> WILLOW_BOAT = ITEMS.register("willow_boat",
            () -> new ModBoatItem(false, ModBoatEntity.Type.WILLOW, new Item.Properties()));
    public static final RegistryObject<Item> WILLOW_CHEST_BOAT = ITEMS.register("willow_chest_boat",
            () -> new ModBoatItem(true, ModBoatEntity.Type.WILLOW, new Item.Properties()));

    // Addon/Compat Items

    public static RegistryObject<Item> WITCH_SALAD;
    static {
        if (ModList.get().isLoaded("farmersdelight")) {
            WITCH_SALAD = ITEMS.register("witch_salad",
                    () -> new Item(new Item.Properties().food(ModFoodProperties.WITCH_SALAD)));
        }
    }

    public static RegistryObject<Item> VERDANT_GRIMOIRE;
    static {
        if (ModList.get().isLoaded("patchouli")) {
            VERDANT_GRIMOIRE = ITEMS.register("verdant_grimoire",
                    () -> new GrimoireItem(new Item.Properties()));
        }
    }

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
