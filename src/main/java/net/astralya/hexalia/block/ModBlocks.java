package net.astralya.hexalia.block;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.custom.*;
import net.astralya.hexalia.block.custom.wood.ModHangingSignBlock;
import net.astralya.hexalia.block.custom.wood.ModSignBlock;
import net.astralya.hexalia.block.custom.wood.ModWallHangingSignBlock;
import net.astralya.hexalia.block.custom.wood.ModWallSignBlock;
import net.astralya.hexalia.effect.ModMobEffects;
import net.astralya.hexalia.util.ModWoodTypes;
import net.astralya.hexalia.worldgen.ModConfiguredFeatures;
import net.astralya.hexalia.worldgen.tree.ModSaplingGenerators;
import net.minecraft.block.*;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    // Natural Blocks
    public static final Block INFUSED_DIRT = registerBlock("infused_dirt",
            new InfusedDirtBlock(AbstractBlock.Settings.copy(Blocks.DIRT).sounds(BlockSoundGroup.MUD)));
    public static final Block INFUSED_FARMLAND = registerBlock("infused_farmland",
            new InfusedFarmlandBlock(AbstractBlock.Settings.copy(Blocks.FARMLAND)
                    .sounds(BlockSoundGroup.MUD).ticksRandomly().nonOpaque()));
    public static final Block SILKWORM_COCOON = registerBlock("silkworm_cocoon",
            new SilkwormCocoonBlock(AbstractBlock.Settings.copy(Blocks.WHITE_WOOL)));
    public static final Block EGG_CLUSTER = registerBlock("egg_cluster",
            new EggClusterBlock(AbstractBlock.Settings.copy(Blocks.MOSS_BLOCK).nonOpaque()));

    // Herbs
    public static final Block SPIRIT_BLOOM = registerBlock("spirit_bloom",
            new HerbBlock(StatusEffects.POISON, 6, AbstractBlock.Settings.copy(Blocks.POPPY)));
    public static final Block POTTED_SPIRIT_BLOOM = registerBlockWithoutBlockItem("potted_spirit_bloom",
            new FlowerPotBlock(SPIRIT_BLOOM, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY)));
    public static final Block DREAMSHROOM = registerBlock("dreamshroom",
            new DreamshroomBlock(AbstractBlock.Settings.copy(Blocks.BROWN_MUSHROOM).luminance(state -> 4)));
    public static final Block POTTED_DREAMSHROOM = registerBlockWithoutBlockItem("potted_dreamshroom",
            new FlowerPotBlock(DREAMSHROOM, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY).luminance(state -> 4)));
    public static final Block SIREN_KELP = registerBlockWithoutBlockItem("siren_kelp",
            new SirenKelpBlock(AbstractBlock.Settings.copy(Blocks.SEAGRASS)));
    public static final Block GHOST_FERN = registerBlock("ghost_fern",
            new GhostFernBlock(StatusEffects.INVISIBILITY, 6, AbstractBlock.Settings.copy(Blocks.POPPY)));
    public static final Block POTTED_GHOST_FERN = registerBlockWithoutBlockItem("potted_ghost_fern",
            new FlowerPotBlock(GHOST_FERN, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY)));
    public static final Block CELESTIAL_BLOOM = registerBlock("celestial_bloom",
            new CelestialBloomBlock(StatusEffects.NIGHT_VISION, 6, AbstractBlock.Settings.copy(Blocks.POPPY).luminance(state -> 6)));
    public static final Block POTTED_CELESTIAL_BLOOM = registerBlockWithoutBlockItem("potted_celestial_bloom",
            new FlowerPotBlock(CELESTIAL_BLOOM, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY).luminance(state -> 6)));
    public static final Block WITHERED_CELESTIAL_BLOOM = registerBlock("withered_celestial_bloom",
            new CelestialBloomBlock(StatusEffects.NIGHT_VISION, 3, AbstractBlock.Settings.copy(Blocks.POPPY).luminance(state -> 6)));
    public static final Block POTTED_WITHERED_CELESTIAL_BLOOM = registerBlockWithoutBlockItem("potted_withered_celestial_bloom",
            new FlowerPotBlock(WITHERED_CELESTIAL_BLOOM, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY).luminance(state -> 6)));
    public static final Block LOTUS_FLOWER = registerBlockWithoutBlockItem("lotus_flower",
            new LotusFlowerBlock(AbstractBlock.Settings.copy(Blocks.LILY_PAD).luminance(state -> 6)));
    public static final Block WITCHWEED = registerBlock("witchweed",
            new WitchweedBlock(StatusEffects.POISON, 6, AbstractBlock.Settings.copy(Blocks.POPPY).dynamicBounds().noCollision()));

    // Enchanted Plants
    public static final Block MORPHORA = registerBlock("morphora",
            new MorphoraBlock(AbstractBlock.Settings.copy(Blocks.POPPY)));
    public static final Block POTTED_MORPHORA = registerBlockWithoutBlockItem("potted_morphora",
            new FlowerPotBlock(MORPHORA, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY)));
    public static final Block GRIMSHADE = registerBlock("grimshade",
            new GrimshadeBlock(AbstractBlock.Settings.copy(Blocks.POPPY).noCollision()));
    public static final Block POTTED_GRIMSHADE = registerBlockWithoutBlockItem("potted_grimshade",
            new FlowerPotBlock(GRIMSHADE, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY)));
    public static final Block NAUTILITE = registerBlockWithoutBlockItem("nautilite",
            new NautiliteBlock(AbstractBlock.Settings.copy(Blocks.SEAGRASS)));
    public static final Block WINDSONG = registerBlock("windsong",
            new WindsongBlock(AbstractBlock.Settings.copy(Blocks.POPPY).noCollision()));
    public static final Block POTTED_WINDSONG = registerBlockWithoutBlockItem("potted_windsong",
            new FlowerPotBlock(WINDSONG, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY)));
    public static final Block ASTRYLIS = registerBlock("astrylis",
            new AstrylisBlock(AbstractBlock.Settings.copy(Blocks.POPPY).noCollision()));
    public static final Block POTTED_ASTRYLIS = registerBlockWithoutBlockItem("potted_astrylis",
            new FlowerPotBlock(ASTRYLIS, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY)));
    public static final Block LOURDES = registerBlock("lourdes",
            new LourdesBlock(AbstractBlock.Settings.copy(Blocks.POPPY).noCollision()));
    public static final Block POTTED_LOURDES = registerBlockWithoutBlockItem("potted_lourdes",
            new FlowerPotBlock(LOURDES, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY)));
    public static final Block AEGIFLORA = registerBlock("aegiflora",
            new AegifloraBlock(AbstractBlock.Settings.copy(Blocks.POPPY).noCollision()));
    public static final Block POTTED_AEGIFLORA = registerBlockWithoutBlockItem("potted_aegiflora",
            new FlowerPotBlock(AEGIFLORA, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY)));
    public static final Block WITHERED_AEGIFLORA = registerBlock("withered_aegiflora",
            new AegifloraBlock(AbstractBlock.Settings.copy(Blocks.POPPY).noCollision()));
    public static final Block POTTED_WITHERED_AEGIFLORA = registerBlockWithoutBlockItem("potted_withered_aegiflora",
            new FlowerPotBlock(WITHERED_AEGIFLORA, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY)));

    // Decorative Flora
    public static final Block BEGONIA = registerBlock("begonia",
            new FlowerBlock(StatusEffects.REGENERATION, 6, AbstractBlock.Settings.copy(Blocks.POPPY)));
    public static final Block POTTED_BEGONIA = registerBlockWithoutBlockItem("potted_begonia",
            new FlowerPotBlock(BEGONIA, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY)));
    public static final Block LAVENDER = registerBlock("lavender",
            new FlowerBlock(StatusEffects.LUCK, 6, AbstractBlock.Settings.copy(Blocks.POPPY)));
    public static final Block POTTED_LAVENDER = registerBlockWithoutBlockItem("potted_lavender",
            new FlowerPotBlock(LAVENDER, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY)));
    public static final Block DAHLIA = registerBlock("dahlia",
            new FlowerBlock(StatusEffects.STRENGTH, 6, AbstractBlock.Settings.copy(Blocks.POPPY)));
    public static final Block POTTED_DAHLIA = registerBlockWithoutBlockItem("potted_dahlia",
            new FlowerPotBlock(DAHLIA, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY)));
    public static final Block PALE_MUSHROOM = registerBlock("pale_mushroom",
            new PaleMushroomBlock(AbstractBlock.Settings.copy(Blocks.BROWN_MUSHROOM).noCollision().nonOpaque().luminance(state -> 4)));
    public static final Block NIGHTSHADE_BUSH = registerBlock("nightshade_bush",
            new FlowerBlock(StatusEffects.POISON, 6, AbstractBlock.Settings.copy(Blocks.POPPY)));
    public static final Block POTTED_NIGHTSHADE_BUSH = registerBlockWithoutBlockItem("potted_nightshade_bush",
            new FlowerPotBlock(NIGHTSHADE_BUSH, AbstractBlock.Settings.copy(Blocks.POTTED_POPPY)));

    // Crops
    public static final Block MANDRAKE_CROP = registerBlockWithoutBlockItem("mandrake_crop",
            new MandrakeCropBlock(AbstractBlock.Settings.copy(Blocks.POTATOES)));
    public static final Block SUNFIRE_TOMATO_CROP = registerBlockWithoutBlockItem("sunfire_tomato_crop",
            new SunfireTomatoCropBlock(AbstractBlock.Settings.copy(Blocks.POTATOES)));
    public static final Block RABBAGE_CROP = registerBlockWithoutBlockItem("rabbage_crop",
            new RabbageCropBlock(AbstractBlock.Settings.copy(Blocks.POTATOES)));
    public static final Block WILD_MANDRAKE = registerBlock("wild_mandrake",
            new FlowerBlock(ModMobEffects.STUNNED, 6, AbstractBlock.Settings.copy(Blocks.POPPY).nonOpaque().noCollision()));
    public static final Block WILD_SUNFIRE_TOMATO = registerBlock("wild_sunfire_tomato",
            new WildSunfireTomatoBlock(AbstractBlock.Settings.copy(Blocks.CORNFLOWER).luminance(state -> 4).nonOpaque()));
    public static final Block CHILLBERRY_BUSH = registerBlockWithoutBlockItem("chillberry_bush",
            new ChillberryBushBlock(AbstractBlock.Settings.copy(Blocks.SWEET_BERRY_BUSH).ticksRandomly()));
    public static final Block SALTSPROUT = registerBlockWithoutBlockItem("saltsprout",
            new SaltsproutBlock(AbstractBlock.Settings.copy(Blocks.SWEET_BERRY_BUSH).ticksRandomly()));
    public static final Block GALEBERRIES_VINE = registerBlockWithoutBlockItem("galeberries_vine",
            new GaleberriesVineBlock(AbstractBlock.Settings.copy(Blocks.CAVE_VINES)));
    public static final Block GALEBERRIES_VINE_PLANT = registerBlockWithoutBlockItem("galeberries_vine_plant",
            new GaleberriesVinePlantBlock(AbstractBlock.Settings.copy(Blocks.CAVE_VINES_PLANT)));

    // Mineral-Related Blocks
    public static final Block SALT_BLOCK = registerBlock("salt_block",
            new Block(AbstractBlock.Settings.copy(Blocks.STONE).sounds(BlockSoundGroup.SAND)));
    public static final Block SALT_LAMP = registerBlockWithoutBlockItem("salt_lamp",
            new SaltLampBlock(AbstractBlock.Settings.copy(Blocks.LANTERN).luminance(state -> 12)));
    public static final Block CELESTIAL_CRYSTAL_BLOCK = registerBlock("celestial_crystal_block",
            new Block(AbstractBlock.Settings.copy(Blocks.AMETHYST_BLOCK).luminance(state -> 6)));

    // Functional Blocks
    public static final Block SMALL_CAULDRON = registerBlockWithoutBlockItem("small_cauldron",
            new SmallCauldronBlock(AbstractBlock.Settings.copy(Blocks.CAULDRON).nonOpaque()
                    .luminance(state -> state.get(SmallCauldronBlock.LIT) ? 12 : 0)));
    public static final Block SHELF = registerBlock("shelf",
            new ShelfBlock(AbstractBlock.Settings.copy(Blocks.DEEPSLATE_BRICKS).nonOpaque()));
    public static final Block RUSTIC_OVEN = registerBlock("rustic_oven",
            new RusticOvenBlock(AbstractBlock.Settings.copy(Blocks.DEEPSLATE_BRICKS).nonOpaque().luminance(state -> 10)));
    public static final Block RITUAL_TABLE = registerBlockWithoutBlockItem("ritual_table",
            new RitualTableBlock(AbstractBlock.Settings.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final Block RITUAL_BRAZIER = registerBlock("ritual_brazier",
            new RitualBrazierBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final Block CENSER = registerBlock("censer",
            new CenserBlock(AbstractBlock.Settings.copy(Blocks.CAMPFIRE).nonOpaque()
                    .luminance(state -> state.get(CenserBlock.LIT) ? 12 : 0)));
    public static final Block DREAMCATCHER = registerBlock("dreamcatcher",
            new DreamcatcherBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final Block MORTAR_AND_PESTLE = registerBlockWithoutBlockItem("mortar_and_pestle",
            new MortarAndPestleBlock(AbstractBlock.Settings.copy(Blocks.MANGROVE_WOOD).nonOpaque()));
    public static final Block NESTING_BLOCK = registerBlock("nesting_block",
            new NestingBlock(AbstractBlock.Settings.copy(Blocks.LOOM).nonOpaque()));

    // Decorative Blocks
    public static final Block CANDLE_SKULL = registerBlockWithoutBlockItem("candle_skull",
            new CandleSkullBlock(AbstractBlock.Settings.create().mapColor(net.minecraft.block.MapColor.TERRACOTTA_WHITE)
                    .strength(1.0f).luminance(state -> state.get(CandleSkullBlock.LIT) ? 12 : 0)));
    public static final Block WITHER_CANDLE_SKULL = registerBlockWithoutBlockItem("wither_candle_skull",
            new CandleSkullBlock(AbstractBlock.Settings.create().mapColor(net.minecraft.block.MapColor.TERRACOTTA_BLACK)
                    .strength(1.0f).luminance(state -> state.get(CandleSkullBlock.LIT) ? 12 : 0)));

    // Tree-Related Blocks
    public static final Block COTTONWOOD_CATKIN = registerBlock("cottonwood_catkin",
            new CatkinBlock(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES).strength(0.2f).noCollision().nonOpaque()));
    public static final Block COTTONWOOD_LEAVES = registerBlock("cottonwood_leaves",
            new LeavesBlock(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES).strength(0.2f).nonOpaque()));
    public static final Block COTTONWOOD_SAPLING = registerBlock("cottonwood_sapling",
            new CocoonSaplingBlock(ModSaplingGenerators.COTTONWOOD, ModConfiguredFeatures.COTTONWOOD, ModConfiguredFeatures.COTTONWOOD_COCOON,
                    AbstractBlock.Settings.copy(Blocks.OAK_SAPLING).strength(0.2f)));
    public static final Block POTTED_COTTONWOOD_SAPLING = registerBlockWithoutBlockItem("potted_cottonwood_sapling",
            new FlowerPotBlock(COTTONWOOD_SAPLING, AbstractBlock.Settings.copy(Blocks.POTTED_ALLIUM)));
    public static final Block COTTONWOOD_LOG = registerBlock("cottonwood_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block STRIPPED_COTTONWOOD_LOG = registerBlock("stripped_cottonwood_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block COTTONWOOD_WOOD = registerBlock("cottonwood_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block STRIPPED_COTTONWOOD_WOOD = registerBlock("stripped_cottonwood_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block COTTONWOOD_PLANKS = registerBlock("cottonwood_planks",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final Block COTTONWOOD_STAIRS = registerBlock("cottonwood_stairs",
            new StairsBlock(ModBlocks.COTTONWOOD_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(Blocks.OAK_STAIRS)));
    public static final Block COTTONWOOD_SLAB = registerBlock("cottonwood_slab",
            new SlabBlock(AbstractBlock.Settings.copy(Blocks.OAK_SLAB)));
    public static final Block COTTONWOOD_BUTTON = registerBlock("cottonwood_button",
            new ButtonBlock(BlockSetType.OAK, 10, AbstractBlock.Settings.copy(Blocks.OAK_BUTTON)));
    public static final Block COTTONWOOD_PRESSURE_PLATE = registerBlock("cottonwood_pressure_plate",
            new PressurePlateBlock(BlockSetType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_PRESSURE_PLATE)));
    public static final Block COTTONWOOD_FENCE = registerBlock("cottonwood_fence",
            new FenceBlock(AbstractBlock.Settings.copy(Blocks.OAK_FENCE)));
    public static final Block COTTONWOOD_FENCE_GATE = registerBlock("cottonwood_fence_gate",
            new FenceGateBlock(WoodType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_FENCE_GATE)));
    public static final Block COTTONWOOD_TRAPDOOR = registerBlock("cottonwood_trapdoor",
            new TrapdoorBlock(BlockSetType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_TRAPDOOR).nonOpaque()));
    public static final Block COTTONWOOD_DOOR = registerBlock("cottonwood_door",
            new DoorBlock(BlockSetType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_DOOR).nonOpaque()));
    public static final Block COTTONWOOD_SIGN = registerBlockWithoutBlockItem("cottonwood_sign",
            new ModSignBlock(ModWoodTypes.COTTONWOOD_WOOD_TYPE, AbstractBlock.Settings.copy(Blocks.OAK_SIGN).nonOpaque()));
    public static final Block COTTONWOOD_WALL_SIGN = registerBlockWithoutBlockItem("cottonwood_wall_sign",
            new ModWallSignBlock(ModWoodTypes.COTTONWOOD_WOOD_TYPE, AbstractBlock.Settings.copy(Blocks.OAK_WALL_SIGN).nonOpaque()));
    public static final Block COTTONWOOD_HANGING_SIGN = registerBlockWithoutBlockItem("cottonwood_hanging_sign",
            new ModHangingSignBlock(ModWoodTypes.COTTONWOOD_WOOD_TYPE, AbstractBlock.Settings.copy(Blocks.OAK_SIGN).nonOpaque()));
    public static final Block COTTONWOOD_HANGING_WALL_SIGN = registerBlockWithoutBlockItem("cottonwood_hanging_wall_sign",
            new ModWallHangingSignBlock(ModWoodTypes.COTTONWOOD_WOOD_TYPE, AbstractBlock.Settings.copy(Blocks.OAK_SIGN).nonOpaque()));

    public static final Block WILLOW_LEAVES = registerBlock("willow_leaves",
            new LeavesBlock(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES).strength(0.2f).nonOpaque()));
    public static final Block WILLOW_SAPLING = registerBlock("willow_sapling",
            new SaplingBlock(ModSaplingGenerators.WILLOW, AbstractBlock.Settings.copy(Blocks.OAK_SAPLING).strength(0.2f)));
    public static final Block POTTED_WILLOW_SAPLING = registerBlockWithoutBlockItem("potted_willow_sapling",
            new FlowerPotBlock(WILLOW_SAPLING, AbstractBlock.Settings.copy(Blocks.POTTED_ALLIUM)));
    public static final Block WILLOW_LOG = registerBlock("willow_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block STRIPPED_WILLOW_LOG = registerBlock("stripped_willow_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block WILLOW_WOOD = registerBlock("willow_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block STRIPPED_WILLOW_WOOD = registerBlock("stripped_willow_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block WILLOW_PLANKS = registerBlock("willow_planks",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final Block WILLOW_STAIRS = registerBlock("willow_stairs",
            new StairsBlock(ModBlocks.WILLOW_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(Blocks.OAK_STAIRS)));
    public static final Block WILLOW_SLAB = registerBlock("willow_slab",
            new SlabBlock(AbstractBlock.Settings.copy(Blocks.OAK_SLAB)));
    public static final Block WILLOW_BUTTON = registerBlock("willow_button",
            new ButtonBlock(BlockSetType.OAK, 10, AbstractBlock.Settings.copy(Blocks.OAK_BUTTON)));
    public static final Block WILLOW_PRESSURE_PLATE = registerBlock("willow_pressure_plate",
            new PressurePlateBlock(BlockSetType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_PRESSURE_PLATE)));
    public static final Block WILLOW_FENCE = registerBlock("willow_fence",
            new FenceBlock(AbstractBlock.Settings.copy(Blocks.OAK_FENCE)));
    public static final Block WILLOW_FENCE_GATE = registerBlock("willow_fence_gate",
            new FenceGateBlock(WoodType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_FENCE_GATE)));
    public static final Block WILLOW_TRAPDOOR = registerBlock("willow_trapdoor",
            new TrapdoorBlock(BlockSetType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_TRAPDOOR).nonOpaque()));
    public static final Block WILLOW_DOOR = registerBlock("willow_door",
            new DoorBlock(BlockSetType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_DOOR).nonOpaque()));
    public static final Block WILLOW_SIGN = registerBlockWithoutBlockItem("willow_sign",
            new ModSignBlock(ModWoodTypes.WILLOW_WOOD_TYPE, AbstractBlock.Settings.copy(Blocks.OAK_SIGN).nonOpaque()));
    public static final Block WILLOW_WALL_SIGN = registerBlockWithoutBlockItem("willow_wall_sign",
            new ModWallSignBlock(ModWoodTypes.WILLOW_WOOD_TYPE, AbstractBlock.Settings.copy(Blocks.OAK_WALL_SIGN).nonOpaque()));
    public static final Block WILLOW_HANGING_SIGN = registerBlockWithoutBlockItem("willow_hanging_sign",
            new ModHangingSignBlock(ModWoodTypes.WILLOW_WOOD_TYPE, AbstractBlock.Settings.copy(Blocks.OAK_SIGN).nonOpaque()));
    public static final Block WILLOW_HANGING_WALL_SIGN = registerBlockWithoutBlockItem("willow_hanging_wall_sign",
            new ModWallHangingSignBlock(ModWoodTypes.WILLOW_WOOD_TYPE, AbstractBlock.Settings.copy(Blocks.OAK_SIGN).nonOpaque()));

    private static Block registerBlockWithoutBlockItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(HexaliaMod.MODID, name), block);
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(HexaliaMod.MODID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(HexaliaMod.MODID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        HexaliaMod.LOGGER.info("Registering Mod Blocks for " + HexaliaMod.MODID);
    }
}