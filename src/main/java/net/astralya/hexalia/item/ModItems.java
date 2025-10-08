package net.astralya.hexalia.item;

import com.terraformersmc.terraform.boat.api.item.TerraformBoatItemHelper;
import net.astralya.hexalia.entity.custom.projectile.FoulSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.FrostSacProjectile;
import net.astralya.hexalia.util.ModArmorMaterials;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.effect.ModEffects;
import net.astralya.hexalia.entity.ModBoats;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.item.custom.*;
import net.astralya.hexalia.util.ModToolMaterials;
import net.astralya.hexalia.util.ModUtil;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    // Resources
    public static final Item SALT = registerItem("salt", new Item(new FabricItemSettings()));
    public static final Item TREE_RESIN = registerItem("tree_resin", new Item(new FabricItemSettings()));
    public static final Item SILK_FIBER = registerItem("silk_fiber", new Item(new FabricItemSettings()));
    public static final Item SILKWORM = registerItem("silkworm", new Item(new FabricItemSettings()));
    public static final Item CELESTIAL_CRYSTAL = registerItem("celestial_crystal", new Item(new FabricItemSettings()));
    public static final Item FIRE_NODE = registerItem("fire_node", new Item(new FabricItemSettings()));
    public static final Item WATER_NODE = registerItem("water_node", new Item(new FabricItemSettings()));
    public static final Item AIR_NODE = registerItem("air_node", new Item(new FabricItemSettings()));
    public static final Item EARTH_NODE = registerItem("earth_node", new Item(new FabricItemSettings()));
    public static final Item ANCIENT_SEED = registerItem("ancient_seed", new Item(new FabricItemSettings().rarity(Rarity.RARE)));

    // Crops, Plants & Seeds
    public static final Item SUNFIRE_TOMATO = registerItem("sunfire_tomato",
            new Item(new FabricItemSettings().food(ModFoodComponents.SUNFIRE_TOMATO)));
    public static final Item SUNFIRE_TOMATO_SEEDS = registerItem("sunfire_tomato_seeds",
            new AliasedBlockItem(ModBlocks.SUNFIRE_TOMATO_CROP, new FabricItemSettings()));
    public static final Item MANDRAKE = registerItem("mandrake",
            new MandrakeItem(new FabricItemSettings()));
    public static final Item MANDRAKE_SEEDS = registerItem("mandrake_seeds",
            new AliasedBlockItem(ModBlocks.MANDRAKE_CROP, new FabricItemSettings()));
    public static final Item RABBAGE = registerItem("rabbage",
            new RabbageItem(new FabricItemSettings().maxCount(16)));
    public static final Item RABBAGE_SEEDS = registerItem("rabbage_seeds",
            new AliasedBlockItem(ModBlocks.RABBAGE_CROP, new FabricItemSettings()));
    public static final Item CHILLBERRIES = registerItem("chillberries",
            new BlockItem(ModBlocks.CHILLBERRY_BUSH, new FabricItemSettings().food(ModFoodComponents.CHILLBERRIES)));
    public static final Item GALEBERRIES = registerItem("galeberries",
            new AliasedBlockItem(ModBlocks.GALEBERRIES_VINE, new FabricItemSettings().food(ModFoodComponents.GALEBERRIES)));
    public static final Item SALTSPROUT = registerItem("saltsprout",
            new BlockItem(ModBlocks.SALTSPROUT, new FabricItemSettings().food(ModFoodComponents.SALTSPROUT)));

    // Plants
    public static final Item SIREN_KELP = registerItem("siren_kelp",
            new BlockItem(ModBlocks.SIREN_KELP, new FabricItemSettings().food(ModFoodComponents.SIREN_KELP)));
    public static final Item LOTUS_FLOWER = registerItem("lotus_flower",
            new PlaceableOnWaterItem(ModBlocks.LOTUS_FLOWER, new FabricItemSettings()));
    public static final Item NAUTILITE = registerItem("nautilite",
            new BlockItem(ModBlocks.NAUTILITE, new FabricItemSettings()));
    public static final Item LOTUS_BLOSSOM = registerItem("lotus_blossom",
            new Item(new FabricItemSettings()));

    // Refined Resources
    public static final Item SIREN_PASTE = registerItem("siren_paste", new Item(new FabricItemSettings()));
    public static final Item SPIRIT_POWDER = registerItem("spirit_powder", new Item(new FabricItemSettings()));
    public static final Item DREAM_PASTE = registerItem("dream_paste", new Item(new FabricItemSettings()));
    public static final Item GHOST_POWDER = registerItem("ghost_powder", new Item(new FabricItemSettings()));
    
    // Food Items
    public static final Item SPICY_SANDWICH = registerItem("spicy_sandwich",
            new Item(new FabricItemSettings().food(ModFoodComponents.SPICY_SANDWICH)));
    public static final Item CHILLBERRY_PIE = registerItem("chillberry_pie",
            new Item(new FabricItemSettings().food(ModFoodComponents.CHILLBERRY_PIE)));
    public static final Item MANDRAKE_STEW = registerItem("mandrake_stew",
            new StewItem(new FabricItemSettings().food(ModFoodComponents.MANDRAKE_STEW).maxCount(1)));
    public static final Item GALEBERRIES_COOKIE = registerItem("galeberries_cookie",
            new Item(new FabricItemSettings().food(ModFoodComponents.GALEBERRIES_COOKIE)));
    
    // Tools
    public static final Item HEX_FOCUS = registerItem("hex_focus",
            new HexFocusItem(new FabricItemSettings().maxCount(1)));
    public static final Item MORTAR_AND_PESTLE = registerItem("mortar_and_pestle",
            new MortarAndPestleItem(new FabricItemSettings().maxDamage(64)));
    public static final Item ATHAME = registerItem("athame",
            new AthameItem(new FabricItemSettings().maxDamage(64)));
    public static final Item PURIFYING_SAC = registerItem("purifying_sac",
            new PurifyingSacItem(new FabricItemSettings().maxCount(16)));
    public static final Item FOUL_SAC = registerItem("foul_sac",
            new ThrownSacItem(new FabricItemSettings().maxCount(16), FoulSacProjectile::new));
    public static final Item FROST_SAC = registerItem("frost_sac",
            new ThrownSacItem(new FabricItemSettings().maxCount(16), FrostSacProjectile::new));
    public static final Item SAGE_PENDANT = registerItem("sage_pendant",
            new Item(new FabricItemSettings().maxCount(1).maxDamage(64).rarity(Rarity.UNCOMMON)));
    public static final Item SILK_IDOL = registerItem("silk_idol",
            new Item(new FabricItemSettings()));
    public static final Item RAINFALL_IDOL = registerItem("rainfall_idol",
            new WeatherIdolItem(new FabricItemSettings()));
    public static final Item CLARITY_IDOL = registerItem("clarity_idol",
            new WeatherIdolItem(new FabricItemSettings()));
    public static final Item TEMPEST_IDOL = registerItem("tempest_idol",
            new WeatherIdolItem(new FabricItemSettings()));
    public static final Item PURITY_IDOL = registerItem("purity_idol",
            new PurityIdolItem(new FabricItemSettings()));
    public static final Item MUTAVIS = registerItem("mutavis",
            new MutavisItem(new FabricItemSettings()));
    
    // Block Items
    public static final Item CANDLE_SKULL = registerItem("candle_skull",
            new BlockItem(ModBlocks.CANDLE_SKULL, new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    public static final Item WITHER_CANDLE_SKULL = registerItem("wither_candle_skull",
            new BlockItem(ModBlocks.WITHER_CANDLE_SKULL, new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    public static final Item SALT_LAMP = registerItem("salt_lamp",
            new BlockItem(ModBlocks.SALT_LAMP, new FabricItemSettings()));
    public static final Item SMALL_CAULDRON = registerItem("small_cauldron",
            new BlockItem(ModBlocks.SMALL_CAULDRON, new FabricItemSettings()));
    public static final Item RITUAL_TABLE = registerItem("ritual_table",
            new BlockItem(ModBlocks.RITUAL_TABLE, new FabricItemSettings()));
    
    // Weapons & Armor
    public static final Item KELPWEAVE_BLADE = registerItem("kelpweave_blade",
            new KelpweaveBladeItem(ModToolMaterials.ANCIENT, 3, -2f,
                    new FabricItemSettings().rarity(Rarity.RARE)));
    public static final Item GHOSTVEIL = registerItem("ghostveil",
            new GhostVeilItem(ModArmorMaterials.GHOST, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().maxDamage(96)));
    public static final Item EARPLUGS = registerItem("earplugs",
            new ArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new FabricItemSettings().maxDamage(96)));
    public static final Item BOGGED_BOOTS = registerItem("bogged_boots",
            new BoggedBootsItem(ModArmorMaterials.BOGGED, ArmorItem.Type.BOOTS, new FabricItemSettings().maxDamage(96)));
    
    // Brews
    public static final Item RUSTIC_BOTTLE = registerItem("rustic_bottle",
            new Item(new FabricItemSettings()));
    public static final Item BREW_OF_SPIKESKIN = registerItem("brew_of_spikeskin",
            new BrewItem(new FabricItemSettings().maxCount(4), () -> ModEffects.SPIKESKIN, 20 * 240, 0,
                    Text.translatable("tooltip.hexalia.spikeskin_brew").formatted(Formatting.BLUE)));
    public static final Item BREW_OF_BLOODLUST = registerItem("brew_of_bloodlust",
            new BrewItem(new FabricItemSettings().maxCount(4), () -> ModEffects.BLOODLUST, 20 * 240, 0,
                    Text.translatable("tooltip.hexalia.bloodlust_brew").formatted(Formatting.BLUE)));
    public static final Item BREW_OF_SLIMEWALKER = registerItem("brew_of_slimewalker",
            new BrewItem(new FabricItemSettings().maxCount(4), () -> ModEffects.SLIMEWALKER, 20 * 240, 0,
                    Text.translatable("tooltip.hexalia.slimewalker_brew").formatted(Formatting.BLUE)));
    public static final Item BREW_OF_SIPHON = registerItem("brew_of_siphon",
            new BrewItem(new FabricItemSettings().maxCount(4).maxCount(4), () -> ModEffects.SIPHON, 20 * 240, 0,
                    Text.translatable("tooltip.hexalia.siphon_brew").formatted(Formatting.BLUE)));
    public static final Item BREW_OF_DAYBLOOM = registerItem("brew_of_daybloom",
            new BrewItem(new FabricItemSettings().maxCount(4).maxCount(4), () -> ModEffects.DAYBLOOM, 20 * 240, 0,
                    Text.translatable("tooltip.hexalia.daybloom").formatted(Formatting.BLUE)));
    public static final Item BREW_OF_HOMESTEAD = registerItem("brew_of_homestead",
            new HomesteadBrewItem(new FabricItemSettings().recipeRemainder(ModItems.RUSTIC_BOTTLE).maxCount(4)));
    public static final Item BREW_OF_ARACHNID_GRACE = registerItem("brew_of_arachnid_grace",
            new BrewItem(new FabricItemSettings().maxCount(4), () -> ModEffects.ARACHNID_GRACE, 20 * 240, 0,
                    Text.translatable("tooltip.hexalia.arachnid_grace").formatted(Formatting.BLUE)));

    // Entity-related Items
    public static final Item BOTTLED_MOTH = registerItem("bottled_moth",
            new BottledMothItem(new FabricItemSettings().maxCount(1)));
    public static final Item SILK_MOTH_SPAWN_EGG = registerItem("silk_moth_spawn_egg",
            new SpawnEggItem(ModEntities.SILK_MOTH,
                    0xAE8f7A, 0x846552, new FabricItemSettings()));

    // Wooden-related Items
    public static final Item COTTONWOOD_BOAT = TerraformBoatItemHelper.registerBoatItem(ModBoats.COTTONWOOD_BOAT_ID,
            ModBoats.COTTONWOOD_BOAT_KEY, false);
    public static final Item COTTONWOOD_CHEST_BOAT = TerraformBoatItemHelper.registerBoatItem(ModBoats.COTTONWOOD_CHEST_BOAT_ID,
            ModBoats.COTTONWOOD_BOAT_KEY, true);
    public static final Item COTTONWOOD_SIGN = registerItem("cottonwood_sign",
            new SignItem(new FabricItemSettings().maxCount(16), ModBlocks.COTTONWOOD_SIGN, ModBlocks.COTTONWOOD_WALL_SIGN));
    public static final Item COTTONWOOD_HANGING_SIGN = registerItem("cottonwood_hanging_sign",
            new HangingSignItem(ModBlocks.COTTONWOOD_HANGING_SIGN, ModBlocks.COTTONWOOD_HANGING_WALL_SIGN, new FabricItemSettings().maxCount(16)));
    public static final Item WILLOW_BOAT = TerraformBoatItemHelper.registerBoatItem(ModBoats.WILLOW_BOAT_ID,
            ModBoats.WILLOW_BOAT_KEY, false);
    public static final Item WILLOW_CHEST_BOAT = TerraformBoatItemHelper.registerBoatItem(ModBoats.WILLOW_CHEST_BOAT_ID,
            ModBoats.WILLOW_BOAT_KEY, true);
    public static final Item WILLOW_SIGN = registerItem("willow_sign",
            new SignItem(new FabricItemSettings().maxCount(16), ModBlocks.WILLOW_SIGN, ModBlocks.WILLOW_WALL_SIGN));
    public static final Item WILLOW_HANGING_SIGN = registerItem("willow_hanging_sign",
            new HangingSignItem(ModBlocks.WILLOW_HANGING_SIGN, ModBlocks.WILLOW_HANGING_WALL_SIGN, new FabricItemSettings().maxCount(16)));

    // Addon/Compat Items
    public static Item VERDANT_GRIMOIRE;
    static {
        if (ModUtil.isModLoaded("patchouli")) {
            VERDANT_GRIMOIRE = registerItem("verdant_grimoire",
                    new GuideBookItem(new FabricItemSettings()));
        }
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(HexaliaMod.MODID, name), item);
    }

    public static void registerModItems() {
        HexaliaMod.LOGGER.info("Registering Items for " + HexaliaMod.MODID);
    }
}