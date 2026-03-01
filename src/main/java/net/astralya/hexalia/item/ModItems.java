package net.astralya.hexalia.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.wood.ModBoatItem;
import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.effect.ModMobEffects;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.boat.ModBoatEntity;
import net.astralya.hexalia.entity.custom.projectile.FoulSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.FrostSacProjectile;
import net.astralya.hexalia.entity.custom.projectile.SearingSacProjectile;
import net.astralya.hexalia.item.custom.*;
import net.astralya.hexalia.item.custom.armor.*;
import net.astralya.hexalia.util.ModArmorMaterials;
import net.astralya.hexalia.util.ModToolTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(HexaliaMod.MODID);

    private static final ResourceLocation SILKWEAVE_SET_ID = ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "silkweave");

    private static Item.Properties silkweaveResistProps() {
        return new Item.Properties()
                .component(ModComponents.MAGIC_RESIST_PCT.get(), 0.05f)
                .component(ModComponents.ARMOR_SET_ID.get(), SILKWEAVE_SET_ID)
                .component(ModComponents.FULL_SET_BONUS_PCT.get(), 0.10f);
    }

    // Resources
    public static final DeferredItem<Item> SALT = ITEMS.registerSimpleItem("salt");
    public static final DeferredItem<Item> TREE_RESIN = ITEMS.registerSimpleItem("tree_resin");
    public static final DeferredItem<Item> SILK_FIBER = ITEMS.registerSimpleItem("silk_fiber");
    public static final DeferredItem<Item> SILKWORM = ITEMS.registerItem("silkworm",
            SilkwormItem::new, new Item.Properties());
    public static final DeferredItem<Item> CELESTIAL_CRYSTAL = ITEMS.registerSimpleItem("celestial_crystal");
    public static final DeferredItem<Item> FIRE_NODE = ITEMS.registerSimpleItem("fire_node");
    public static final DeferredItem<Item> WATER_NODE = ITEMS.registerSimpleItem("water_node");
    public static final DeferredItem<Item> AIR_NODE = ITEMS.registerSimpleItem("air_node");
    public static final DeferredItem<Item> EARTH_NODE = ITEMS.registerSimpleItem("earth_node");
    public static final DeferredItem<Item> ANCIENT_SEED = ITEMS.registerItem("ancient_seed",
            Item::new, new Item.Properties().rarity(Rarity.RARE));

    // Crops & Seeds
    public static final DeferredItem<Item> SUNFIRE_TOMATO = ITEMS.registerItem("sunfire_tomato",
            Item::new, new Item.Properties().food(ModFoodProperties.SUNFIRE_TOMATO));
    public static final DeferredItem<Item> SUNFIRE_TOMATO_SEEDS = ITEMS.register("sunfire_tomato_seeds",
            () -> new BlockItem(ModBlocks.SUNFIRE_TOMATO_CROP.get(), new Item.Properties()));
    public static final DeferredItem<Item> MANDRAKE = ITEMS.registerItem("mandrake",
            MandrakeItem::new, new Item.Properties());
    public static final DeferredItem<Item> MANDRAKE_SEEDS = ITEMS.register("mandrake_seeds",
            () -> new BlockItem(ModBlocks.MANDRAKE_CROP.get(), new Item.Properties()));
    public static final DeferredItem<Item> RABBAGE = ITEMS.registerItem("rabbage",
            RabbageItem::new, new Item.Properties());
    public static final DeferredItem<Item> RABBAGE_SEEDS = ITEMS.register("rabbage_seeds",
            () -> new BlockItem(ModBlocks.RABBAGE_CROP.get(), new Item.Properties()));
    public static final DeferredItem<Item> CHILLBERRIES = ITEMS.register("chillberries",
            () -> new BlockItem(ModBlocks.CHILLBERRY_BUSH.get(), new Item.Properties().food(ModFoodProperties.CHILLBERRIES)));
    public static final DeferredItem<Item> SALTSPROUT = ITEMS.register("saltsprout",
            () -> new BlockItem(ModBlocks.SALTSPROUT.get(), new Item.Properties().food(ModFoodProperties.SALTSPROUT)));
    public static final DeferredItem<Item> GALEBERRIES = ITEMS.register("galeberries",
            () -> new ItemNameBlockItem(ModBlocks.GALEBERRIES_VINE.get(), new Item.Properties().food(ModFoodProperties.GALEBERRIES)));

    // Plants
    public static final DeferredItem<Item> SIREN_KELP = ITEMS.register("siren_kelp",
            () -> new BlockItem(ModBlocks.SIREN_KELP.get(), new Item.Properties().food(ModFoodProperties.SIREN_KELP)));
    public static final DeferredItem<Item> NAUTILITE = ITEMS.register("nautilite",
            () -> new BlockItem(ModBlocks.NAUTILITE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> LOTUS_FLOWER = ITEMS.register("lotus_flower",
            () -> new PlaceOnWaterBlockItem(ModBlocks.LOTUS_FLOWER.get(), new Item.Properties()));
    public static final DeferredItem<Item> LOTUS_BLOSSOM = ITEMS.registerSimpleItem("lotus_blossom");

    // Refined Resources
    public static final DeferredItem<Item> SPIRIT_POWDER = ITEMS.registerSimpleItem("spirit_powder");
    public static final DeferredItem<Item> SIREN_PASTE = ITEMS.registerSimpleItem("siren_paste");
    public static final DeferredItem<Item> DREAM_PASTE = ITEMS.registerSimpleItem("dream_paste");
    public static final DeferredItem<Item> GHOST_POWDER = ITEMS.registerSimpleItem("ghost_powder");
    public static final DeferredItem<Item> FRAGRANT_NECTAR = ITEMS.registerSimpleItem("fragrant_nectar");

    // Food Items
    public static final DeferredItem<Item> SPICY_SANDWICH = ITEMS.registerItem("spicy_sandwich",
            Item::new, new Item.Properties().food(ModFoodProperties.SPICY_SANDWICH));
    public static final DeferredItem<Item> CHILLBERRY_PIE = ITEMS.registerItem("chillberry_pie",
            Item::new, new Item.Properties().food(ModFoodProperties.CHILLBERRY_PIE));
    public static final DeferredItem<Item> MANDRAKE_STEW = ITEMS.registerItem("mandrake_stew",
            Item::new, new Item.Properties().food(ModFoodProperties.MANDRAKE_STEW).stacksTo(1));
    public static final DeferredItem<Item> GALEBERRIES_COOKIE = ITEMS.registerItem("galeberries_cookie",
            Item::new, new Item.Properties().food(ModFoodProperties.GALEBERRIES_COOKIE));

    // Tools
    public static final DeferredItem<Item> HEX_FOCUS = ITEMS.registerItem("hex_focus",
            HexFocusItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ATHAME = ITEMS.registerItem("athame",
            AthameItem::new, new Item.Properties().durability(64));
    public static final DeferredItem<Item> PURIFYING_SAC = ITEMS.registerItem("purifying_sac",
            PurifyingSacItem::new, new Item.Properties().durability(6));
    public static final DeferredItem<Item> FOUL_SAC = ITEMS.register("foul_sac",
            () -> new ThrownSacItem(new Item.Properties(), FoulSacProjectile::new));
    public static final DeferredItem<Item> FROST_SAC = ITEMS.register("frost_sac",
            () -> new ThrownSacItem(new Item.Properties(), FrostSacProjectile::new));
    public static final DeferredItem<Item> SEARING_SAC = ITEMS.register("searing_sac",
            () -> new ThrownSacItem(new Item.Properties(), SearingSacProjectile::new));
    public static final DeferredItem<Item> SAGE_PENDANT = ITEMS.registerItem("sage_pendant",
            Item::new, new Item.Properties().rarity(Rarity.UNCOMMON).durability(64));
    public static final DeferredItem<Item> SILK_IDOL = ITEMS.registerSimpleItem("silk_idol");
    public static final DeferredItem<Item> CLARITY_IDOL = ITEMS.registerItem("clarity_idol",
            WeatherIdolItem::new, new Item.Properties());
    public static final DeferredItem<Item> RAINFALL_IDOL = ITEMS.registerItem("rainfall_idol",
            WeatherIdolItem::new, new Item.Properties());
    public static final DeferredItem<Item> TEMPEST_IDOL = ITEMS.registerItem("tempest_idol",
            WeatherIdolItem::new, new Item.Properties());
    public static final DeferredItem<Item> PURITY_IDOL = ITEMS.registerItem("purity_idol",
            PurityIdolItem::new, new Item.Properties());
    public static final DeferredItem<Item> MUTAVIS = ITEMS.registerItem("mutavis",
            MutavisItem::new, new Item.Properties());
    public static final DeferredItem<Item> BRIAR_SICKLE = ITEMS.registerItem("briar_sickle",
            BriarSickleItem::new, new Item.Properties().durability(256)
                    .attributes(BriarSickleItem.createAttributes()));
    public static final DeferredItem<Item> ROOTSHAPER = ITEMS.registerItem("rootshaper",
            RootshaperItem::new, new Item.Properties().rarity(Rarity.RARE).durability(1561)
                    .component(DataComponents.TOOL, RootshaperItem.createTool())
                    .attributes(RootshaperItem.createAttributes()));
    public static final DeferredItem<Item> SPIRITROOT_TETHER = ITEMS.registerItem("spiritroot_tether",
            SpiritrootTetherItem::new, new Item.Properties());
    public static final DeferredItem<Item> LADLE = ITEMS.registerItem("ladle",
            Item::new, new Item.Properties().stacksTo(1));

    // Block Items
    public static final DeferredItem<Item> CANDLE_SKULL = ITEMS.register("candle_skull",
            () -> new BlockItem(ModBlocks.CANDLE_SKULL.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> WITHER_CANDLE_SKULL = ITEMS.register("wither_candle_skull",
            () -> new BlockItem(ModBlocks.WITHER_CANDLE_SKULL.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> SALT_LAMP = ITEMS.register("salt_lamp",
            () -> new BlockItem(ModBlocks.SALT_LAMP.get(), new Item.Properties()));
    public static final DeferredItem<Item> SMALL_CAULDRON = ITEMS.register("small_cauldron",
            () -> new BlockItem(ModBlocks.SMALL_CAULDRON.get(), new Item.Properties()));
    public static final DeferredItem<Item> MORTAR_AND_PESTLE = ITEMS.register("mortar_and_pestle",
            () -> new BlockItem(ModBlocks.MORTAR_AND_PESTLE.get(), new Item.Properties()));

    // Weapons & Armor
    public static final DeferredItem<Item> KELPWEAVE_BLADE = ITEMS.register("kelpweave_blade",
            () -> new KelpweaveBlade(ModToolTiers.ANCIENT,
                    new Item.Properties().attributes(SwordItem.createAttributes(ModToolTiers.ANCIENT, 3, -2f)).rarity(Rarity.RARE)));
    public static final DeferredItem<Item> GHOSTVEIL = ITEMS.register("ghostveil",
            () -> new GhostVeilItem(ModArmorMaterials.SILKWEAVE, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(96)));
    public static final DeferredItem<Item> EARPLUGS = ITEMS.register("earplugs",
            () -> new EarplugsItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final DeferredItem<Item> BOGGED_BOOTS = ITEMS.register("bogged_boots",
            () -> new BoggedBootsItem(ModArmorMaterials.SILKWEAVE, ArmorItem.Type.BOOTS, new Item.Properties().durability(96)));
    public static final DeferredItem<Item> THORNBOW = ITEMS.register("thornbow",
            () -> new ThornbowItem(new Item.Properties().durability(128)));

    public static final DeferredItem<Item> SILKWEAVE_HOOD = ITEMS.register("silkweave_hood",
            () -> new SilkweaveHoodItem(ModArmorMaterials.SILKWEAVE, ArmorItem.Type.HELMET, silkweaveResistProps().durability(240)));
    public static final DeferredItem<Item> SILKWEAVE_MANTLE = ITEMS.register("silkweave_mantle",
            () -> new SilkweaveMantleItem(ModArmorMaterials.SILKWEAVE, ArmorItem.Type.CHESTPLATE, silkweaveResistProps().durability(360)));
    public static final DeferredItem<Item> SILKWEAVE_BINDINGS = ITEMS.register("silkweave_bindings",
            () -> new SilkweaveBindingsItem(ModArmorMaterials.SILKWEAVE, ArmorItem.Type.LEGGINGS, silkweaveResistProps().durability(330)));
    public static final DeferredItem<Item> SILKWEAVE_FOOTWRAPS = ITEMS.register("silkweave_footwraps",
            () -> new SilkweaveFootwrapsItem(ModArmorMaterials.SILKWEAVE, ArmorItem.Type.BOOTS, silkweaveResistProps().durability(300)));

    // Brews
    public static final DeferredItem<Item> RUSTIC_BOTTLE = ITEMS.registerSimpleItem("rustic_bottle");
    public static final DeferredItem<Item> BREW_OF_SPIKESKIN = ITEMS.register("brew_of_spikeskin",
            () -> new BrewItem(new Item.Properties().stacksTo(4), () -> ModMobEffects.SPIKESKIN, 20 * 240, 0,
                    Component.translatable("tooltip.hexalia.spikeskin_brew").withStyle(ChatFormatting.BLUE)));
    public static final DeferredItem<Item> BREW_OF_BLOODLUST = ITEMS.register("brew_of_bloodlust",
            () -> new BrewItem(new Item.Properties().stacksTo(4), () -> ModMobEffects.BLOODLUST, 20 * 240, 0,
                    Component.translatable("tooltip.hexalia.bloodlust_brew").withStyle(ChatFormatting.BLUE)));
    public static final DeferredItem<Item> BREW_OF_SLIMEWALKER = ITEMS.register("brew_of_slimewalker",
            () -> new BrewItem(new Item.Properties().stacksTo(4), () -> ModMobEffects.SLIMEWALKER, 20 * 240, 0,
                    Component.translatable("tooltip.hexalia.slimewalker_brew").withStyle(ChatFormatting.BLUE)));
    public static final DeferredItem<Item> BREW_OF_SIPHON = ITEMS.register("brew_of_siphon",
            () -> new BrewItem(new Item.Properties().stacksTo(4), () -> ModMobEffects.SIPHON, 20 * 240, 0,
                    Component.translatable("tooltip.hexalia.siphon_brew").withStyle(ChatFormatting.BLUE)));
    public static final DeferredItem<Item> BREW_OF_DAYBLOOM = ITEMS.register("brew_of_daybloom",
            () -> new BrewItem(new Item.Properties().stacksTo(4), () -> ModMobEffects.DAYBLOOM, 20 * 240, 0,
                    Component.translatable("tooltip.hexalia.daybloom").withStyle(ChatFormatting.BLUE)));
    public static final DeferredItem<Item> BREW_OF_ARACHNID_GRACE = ITEMS.register("brew_of_arachnid_grace",
            () -> new BrewItem(new Item.Properties().stacksTo(4), () -> ModMobEffects.ARACHNID_GRACE, 20 * 240, 0,
                    Component.translatable("tooltip.hexalia.arachnid_grace").withStyle(ChatFormatting.BLUE)));
    public static final DeferredItem<Item> BREW_OF_HOMESTEAD = ITEMS.registerItem("brew_of_homestead",
            HomesteadBrewItem::new, new Item.Properties());
    public static final DeferredItem<Item> BREW_OF_HOLLOW_SILENCE = ITEMS.register("brew_of_hollow_silence",
            () -> new BrewItem(new Item.Properties().stacksTo(4), () -> ModMobEffects.HOLLOW_SILENCE, 20 * 240, 0,
                    Component.translatable("tooltip.hexalia.hollow_silence").withStyle(ChatFormatting.BLUE)));

    // Salves
    public static final DeferredItem<Item> BRAMBLEGUARD_SALVE = ITEMS.register("brambleguard_salve",
            () -> new SalveItem(new Item.Properties().stacksTo(4), () -> ModMobEffects.BRAMBLEGUARD, 20 * 120, 0,
                    60, Component.translatable("tooltip.hexalia.brambleguard_salve").withStyle(ChatFormatting.BLUE)));
    public static final DeferredItem<Item> MENDERS_SALVE = ITEMS.register("menders_salve",
            () -> new SalveItem(new Item.Properties().stacksTo(4), () -> MobEffects.REGENERATION, 20 * 120, 0,
                    60, Component.translatable("tooltip.hexalia.menders_salve").withStyle(ChatFormatting.BLUE)));

    // Entity Related Items
    public static final DeferredItem<Item> BOTTLED_MOTH = ITEMS.register("bottled_moth",
            () -> new BottleMothItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> SILK_MOTH_SPAWN_EGG = ITEMS.register("silk_moth_spawn_egg",
            () -> new CustomModelSpawnEggItem(ModEntities.SILK_MOTH_ENTITY.get(), new Item.Properties()));

    // Wood-Related Items
    public static final DeferredItem<Item> WILLOW_BOAT = ITEMS.register("willow_boat",
            () -> new ModBoatItem(false, ModBoatEntity.Type.WILLOW, new Item.Properties()));
    public static final DeferredItem<Item> WILLOW_CHEST_BOAT = ITEMS.register("willow_chest_boat",
            () -> new ModBoatItem(true, ModBoatEntity.Type.WILLOW, new Item.Properties()));
    public static final DeferredItem<Item> COTTONWOOD_BOAT = ITEMS.register("cottonwood_boat",
            () -> new ModBoatItem(false, ModBoatEntity.Type.COTTONWOOD, new Item.Properties()));
    public static final DeferredItem<Item> COTTONWOOD_CHEST_BOAT = ITEMS.register("cottonwood_chest_boat",
            () -> new ModBoatItem(true, ModBoatEntity.Type.COTTONWOOD, new Item.Properties()));

    public static final DeferredItem<Item> WILLOW_SIGN = ITEMS.register("willow_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.WILLOW_SIGN.get(), ModBlocks.WILLOW_WALL_SIGN.get()));
    public static final DeferredItem<Item> COTTONWOOD_SIGN = ITEMS.register("cottonwood_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.COTTONWOOD_SIGN.get(), ModBlocks.COTTONWOOD_WALL_SIGN.get()));

    public static final DeferredItem<Item> WILLOW_HANGING_SIGN = ITEMS.register("willow_hanging_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.WILLOW_HANGING_SIGN.get(), ModBlocks.WILLOW_HANGING_WALL_SIGN.get()));
    public static final DeferredItem<Item> COTTONWOOD_HANGING_SIGN = ITEMS.register("cottonwood_hanging_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), ModBlocks.COTTONWOOD_HANGING_SIGN.get(), ModBlocks.COTTONWOOD_HANGING_WALL_SIGN.get()));

    // Compat Items
    public static DeferredItem<Item> VERDANT_GRIMOIRE;
    static {
        if (ModList.get().isLoaded("patchouli")) {
            VERDANT_GRIMOIRE = ITEMS.register("verdant_grimoire",
                    () -> new GuideBookItem(new Item.Properties()));
        }
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
