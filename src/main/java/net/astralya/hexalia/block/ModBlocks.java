package net.astralya.hexalia.block;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.custom.*;
import net.astralya.hexalia.block.custom.wood.*;
import net.astralya.hexalia.effect.ModMobEffects;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.ModWoodTypes;
import net.astralya.hexalia.worldgen.tree.ModTreeGrower;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static net.astralya.hexalia.block.custom.CandleSkullBlock.LIT;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(HexaliaMod.MODID);

    // Natural Blocks
    public static final DeferredBlock<Block> INFUSED_DIRT = registerBlock("infused_dirt",
            () -> new InfusedDirtBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).sound(SoundType.MUD)));
    public static final DeferredBlock<Block> INFUSED_FARMLAND = registerBlock("infused_farmland",
            () -> new InfusedFarmlandBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FARMLAND)
                    .randomTicks().sound(SoundType.MUD).noOcclusion()));
    public static final DeferredBlock<Block> SILKWORM_COCOON = registerBlock("silkworm_cocoon",
            () -> new CocoonBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)));
    public static final DeferredBlock<Block> EGG_CLUSTER = registerBlock("egg_cluster",
            () -> new EggClusterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK).noOcclusion()));

    // Herbs
    public static final DeferredBlock<Block> SPIRIT_BLOOM = registerBlock("spirit_bloom",
            () -> new HerbBlock(MobEffects.POISON, 6, BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY)));
    public static final DeferredBlock<Block> POTTED_SPIRIT_BLOOM = BLOCKS.register("potted_spirit_bloom",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), SPIRIT_BLOOM, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY)));
    public static final DeferredBlock<Block> DREAMSHROOM = registerBlock("dreamshroom",
            () -> new DreamshroomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM).lightLevel(state -> 4)));
    public static final DeferredBlock<Block> SIREN_KELP = BLOCKS.register("siren_kelp",
            () -> new SirenKelpBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SEAGRASS)));
    public static final DeferredBlock<Block> POTTED_DREAMSHROOM = BLOCKS.register("potted_dreamshroom",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), DREAMSHROOM, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY).lightLevel(state -> 4)));
    public static final DeferredBlock<Block> GHOST_FERN = registerBlock("ghost_fern",
            () -> new GhostFernBlock(MobEffects.INVISIBILITY, 6, BlockBehaviour.Properties.ofFullCopy(Blocks.FERN)));
    public static final DeferredBlock<Block> POTTED_GHOST_FERN = BLOCKS.register("potted_ghost_fern",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), GHOST_FERN, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY)));
    public static final DeferredBlock<Block> CELESTIAL_BLOOM = registerBlock("celestial_bloom",
            () -> new CelestialBloomBlock(MobEffects.NIGHT_VISION, 6, BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).lightLevel(state -> 6)));
    public static final DeferredBlock<Block> POTTED_CELESTIAL_BLOOM = BLOCKS.register("potted_celestial_bloom",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), CELESTIAL_BLOOM, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY).lightLevel(state -> 6)));
    public static final DeferredBlock<Block> WITHERED_CELESTIAL_BLOOM = registerBlock("withered_celestial_bloom",
            () -> new CelestialBloomBlock(MobEffects.NIGHT_VISION, 3, BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).lightLevel(state -> 6)));
    public static final DeferredBlock<Block> POTTED_WITHERED_CELESTIAL_BLOOM = BLOCKS.register("potted_withered_celestial_bloom",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), WITHERED_CELESTIAL_BLOOM, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY).lightLevel(state -> 6)));
    public static final DeferredBlock<Block> LOTUS_FLOWER = BLOCKS.register("lotus_flower",
            () -> new LotusFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LILY_PAD).lightLevel(state -> 6)));
    public static final DeferredBlock<Block> WITCHWEED = registerBlock("witchweed",
            () -> new WitchweedBlock(MobEffects.POISON, 6, BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).dynamicShape().noCollission()));

    // Enchanted Plants
    public static final DeferredBlock<Block> MORPHORA = registerBlock("morphora",
            () -> new MorphoraBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY)));
    public static final DeferredBlock<Block> POTTED_MORPHORA = BLOCKS.register("potted_morphora",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), MORPHORA, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY)));
    public static final DeferredBlock<Block> GRIMSHADE = registerBlock("grimshade",
            () -> new GrimshadeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).noCollission()));
    public static final DeferredBlock<Block> POTTED_GRIMSHADE = BLOCKS.register("potted_grimshade",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), GRIMSHADE, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY)));
    public static final DeferredBlock<Block> NAUTILITE = BLOCKS.register("nautilite",
            () -> new NautiliteBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SEAGRASS)));
    public static final DeferredBlock<Block> WINDSONG = registerBlock("windsong",
            () -> new WindsongBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).noCollission()));
    public static final DeferredBlock<Block> POTTED_WINDSONG = BLOCKS.register("potted_windsong",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), WINDSONG, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY)));
    public static final DeferredBlock<Block> ASTRYLIS = registerBlock("astrylis",
            () -> new AstrylisBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).noCollission()));
    public static final DeferredBlock<Block> POTTED_ASTRYLIS = BLOCKS.register("potted_astrylis",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), ASTRYLIS, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY)));
    public static final DeferredBlock<Block> LOURDES = registerBlock("lourdes",
            () -> new LourdesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).noCollission()));
    public static final DeferredBlock<Block> POTTED_LOURDES = BLOCKS.register("potted_lourdes",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), ASTRYLIS, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY)));
    public static final DeferredBlock<Block> AEGIFLORA = registerBlock("aegiflora",
            () -> new AegifloraBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).noCollission()));
    public static final DeferredBlock<Block> POTTED_AEGIFLORA = BLOCKS.register("potted_aegiflora",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), ASTRYLIS, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY)));
    public static final DeferredBlock<Block> WITHERED_AEGIFLORA = registerBlock("withered_aegiflora",
            () -> new AegifloraBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).noCollission()));
    public static final DeferredBlock<Block> POTTED_WITHERED_AEGIFLORA = BLOCKS.register("potted_withered_aegiflora",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), WITHERED_AEGIFLORA, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY)));

    // Decorative Flowers
    public static final DeferredBlock<Block> BEGONIA = registerBlock("begonia",
            () -> new FlowerBlock(MobEffects.REGENERATION, 6, BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY)));
    public static final DeferredBlock<Block> POTTED_BEGONIA = BLOCKS.register("potted_begonia",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), BEGONIA, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY)));
    public static final DeferredBlock<Block> LAVENDER = registerBlock("lavender",
            () -> new FlowerBlock(MobEffects.LUCK, 6, BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY)));
    public static final DeferredBlock<Block> POTTED_LAVENDER = BLOCKS.register("potted_lavender",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), LAVENDER, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY)));
    public static final DeferredBlock<Block> DAHLIA = registerBlock("dahlia",
            () -> new FlowerBlock(MobEffects.DAMAGE_BOOST, 6, BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY)));
    public static final DeferredBlock<Block> POTTED_DAHLIA = BLOCKS.register("potted_dahlia",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), DAHLIA, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY)));
    public static final DeferredBlock<Block> PALE_MUSHROOM = registerBlock("pale_mushroom",
            () -> new PaleMushroomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM).lightLevel(state -> 4)));
    public static final DeferredBlock<Block> NIGHTSHADE_BUSH = registerBlock("nightshade_bush",
            () -> new FlowerBlock(MobEffects.POISON, 6, BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY)));
    public static final DeferredBlock<Block> POTTED_NIGHTSHADE_BUSH = BLOCKS.register("potted_nightshade_bush",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), NIGHTSHADE_BUSH, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_POPPY)));
    
    // Crops
    public static final DeferredBlock<Block> MANDRAKE_CROP = BLOCKS.register("mandrake_crop",
            () -> new MandrakeCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POTATOES)));
    public static final DeferredBlock<Block> SUNFIRE_TOMATO_CROP = BLOCKS.register("sunfire_tomato_crop",
            () -> new SunfireTomatoCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POTATOES)));
    public static final DeferredBlock<Block> RABBAGE_CROP = BLOCKS.register("rabbage_crop",
            () -> new RabbageCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POTATOES)));
    public static final DeferredBlock<Block> WILD_MANDRAKE = registerBlock("wild_mandrake",
            () -> new FlowerBlock(ModMobEffects.STUNNED, 6, BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY)));
    public static final DeferredBlock<Block> WILD_SUNFIRE_TOMATO = registerBlock("wild_sunfire_tomato",
            () -> new WildSunfireTomatoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY).lightLevel(state -> 4)));
    public static final DeferredBlock<Block> CHILLBERRY_BUSH = BLOCKS.register("chillberry_bush",
            () -> new ChillberryBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).randomTicks()));
    public static final DeferredBlock<Block> SALTSPROUT = BLOCKS.register("saltsprout",
            () -> new SaltsproutBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).randomTicks()));
    public static final DeferredBlock<Block> GALEBERRIES_VINE = BLOCKS.register("galeberries_vine",
            () -> new GaleberriesVineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAVE_VINES)));
    public static final DeferredBlock<Block> GALEBERRIES_VINE_PLANT = BLOCKS.register("galeberries_vine_plant",
            () -> new GaleberriesVinePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAVE_VINES_PLANT)));

    // Mineral
    public static final DeferredBlock<Block> SALT_BLOCK = registerBlock("salt_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).sound(SoundType.SAND)));
    public static final DeferredBlock<Block> SALT_LAMP = BLOCKS.register("salt_lamp",
            () -> new SaltLampBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> 12)));
    public static final DeferredBlock<Block> CELESTIAL_CRYSTAL_BLOCK = registerBlock("celestial_crystal_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).lightLevel(state -> 6)));

    // Functional Blocks
    public static final DeferredBlock<Block> SMALL_CAULDRON = BLOCKS.register("small_cauldron",
            () -> new SmallCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).noOcclusion().lightLevel(state -> state.getValue(LIT) ? 12 : 0)));
    public static final DeferredBlock<Block> SHELF = registerBlock("shelf",
            () -> new ShelfBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS).noOcclusion()));
    public static final DeferredBlock<Block> RUSTIC_OVEN = registerBlock("rustic_oven",
            () -> new RusticOvenBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS).noOcclusion().lightLevel(state -> 10)));
    public static final DeferredBlock<Block> RITUAL_TABLE = registerBlock("ritual_table",
            () -> new RitualTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)));
    public static final DeferredBlock<Block> RITUAL_BRAZIER = registerBlock("ritual_brazier",
            () -> new RitualBrazierBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> CENSER = registerBlock("censer",
            () -> new CenserBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAMPFIRE).noOcclusion().lightLevel(state -> state.getValue(LIT) ? 12 : 0)));
    public static final DeferredBlock<Block> DREAMCATCHER = registerBlock("dreamcatcher",
            () -> new DreamcatcherBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> MORTAR_AND_PESTLE = BLOCKS.register("mortar_and_pestle",
            () -> new MortarAndPestleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MANGROVE_WOOD).noOcclusion()));
    public static final DeferredBlock<Block> NESTING_BLOCK = registerBlock("nesting_block",
            () -> new NestingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LOOM).noOcclusion()));
    
    // Decorative Blocks
    public static final DeferredBlock<Block> CANDLE_SKULL = BLOCKS.register("candle_skull",
            () -> new CandleSkullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE)
                    .strength(1.0f).lightLevel(state -> state.getValue(LIT) ? 12 : 0)));
    public static final DeferredBlock<Block> WITHER_CANDLE_SKULL = BLOCKS.register("wither_candle_skull",
            () -> new CandleSkullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BLACK)
                    .strength(1.0f).lightLevel(state -> state.getValue(LIT) ? 12 : 0)));

    // Tree-Related Blocks
    public static final DeferredBlock<Block> COTTONWOOD_CATKIN = registerBlock("cottonwood_catkin",
            () -> new CatkinBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).strength(0.2f).noCollission().noOcclusion()));
    public static final DeferredBlock<Block> COTTONWOOD_LEAVES = registerBlock("cottonwood_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).strength(0.2f).noOcclusion()));
    public static final DeferredBlock<Block> COTTONWOOD_SAPLING = registerBlock("cottonwood_sapling",
            () -> new SaplingBlock(ModTreeGrower.COTTONWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING).strength(0.2f)));
    public static final DeferredBlock<Block> POTTED_COTTONWOOD_SAPLING = BLOCKS.register("potted_cottonwood_sapling",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), COTTONWOOD_SAPLING, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)));
    public static final DeferredBlock<Block> COTTONWOOD_LOG = registerBlock("cottonwood_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> STRIPPED_COTTONWOOD_LOG = registerBlock("stripped_cottonwood_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> COTTONWOOD_WOOD = registerBlock("cottonwood_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> STRIPPED_COTTONWOOD_WOOD = registerBlock("stripped_cottonwood_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> COTTONWOOD_PLANKS = registerBlock("cottonwood_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> COTTONWOOD_STAIRS = registerBlock("cottonwood_stairs",
            () -> new StairBlock(ModBlocks.COTTONWOOD_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)));
    public static final DeferredBlock<Block> COTTONWOOD_SLAB = registerBlock("cottonwood_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB)));
    public static final DeferredBlock<Block> COTTONWOOD_BUTTON = registerBlock("cottonwood_button",
            () -> new ButtonBlock(BlockSetType.OAK, 10, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON)));
    public static final DeferredBlock<Block> COTTONWOOD_PRESSURE_PLATE = registerBlock("cottonwood_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)));
    public static final DeferredBlock<Block> COTTONWOOD_FENCE = registerBlock("cottonwood_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE)));
    public static final DeferredBlock<Block> COTTONWOOD_FENCE_GATE = registerBlock("cottonwood_fence_gate",
            () -> new FenceGateBlock(WoodType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE)));
    public static final DeferredBlock<Block> COTTONWOOD_TRAPDOOR = registerBlock("cottonwood_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)));
    public static final DeferredBlock<Block> COTTONWOOD_DOOR = registerBlock("cottonwood_door",
            () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR).noOcclusion()));
    public static final DeferredBlock<Block> COTTONWOOD_SIGN = BLOCKS.register("cottonwood_sign",
            () -> new ModStandingSignBlock(ModWoodTypes.COTTONWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> COTTONWOOD_WALL_SIGN  = BLOCKS.register("cottonwood_wall_sign",
            () -> new ModWallSignBlock(ModWoodTypes.COTTONWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> COTTONWOOD_HANGING_SIGN  = BLOCKS.register("cottonwood_hanging_sign",
            () -> new ModHangingSignBlock(ModWoodTypes.COTTONWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> COTTONWOOD_HANGING_WALL_SIGN = BLOCKS.register("cottonwood_hanging_wall_sign",
            () -> new ModWallHangingSignBlock(ModWoodTypes.COTTONWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));

    public static final DeferredBlock<Block> WILLOW_LEAVES = registerBlock("willow_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).strength(0.2f).noOcclusion()));
    public static final DeferredBlock<Block> WILLOW_SAPLING = registerBlock("willow_sapling",
            () -> new SaplingBlock(ModTreeGrower.WILLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING).strength(0.2f)));
    public static final DeferredBlock<Block> POTTED_WILLOW_SAPLING = BLOCKS.register("potted_willow_sapling",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), WILLOW_SAPLING, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)));
    public static final DeferredBlock<Block> WILLOW_LOG = registerBlock("willow_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> STRIPPED_WILLOW_LOG = registerBlock("stripped_willow_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> WILLOW_WOOD = registerBlock("willow_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> STRIPPED_WILLOW_WOOD = registerBlock("stripped_willow_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> WILLOW_PLANKS = registerBlock("willow_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> WILLOW_STAIRS = registerBlock("willow_stairs",
            () -> new StairBlock(ModBlocks.WILLOW_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)));
    public static final DeferredBlock<Block> WILLOW_SLAB = registerBlock("willow_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB)));
    public static final DeferredBlock<Block> WILLOW_BUTTON = registerBlock("willow_button",
            () -> new ButtonBlock(BlockSetType.OAK, 10, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON)));
    public static final DeferredBlock<Block> WILLOW_PRESSURE_PLATE = registerBlock("willow_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)));
    public static final DeferredBlock<Block> WILLOW_FENCE = registerBlock("willow_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE)));
    public static final DeferredBlock<Block> WILLOW_FENCE_GATE = registerBlock("willow_fence_gate",
            () -> new FenceGateBlock(WoodType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE)));
    public static final DeferredBlock<Block> WILLOW_TRAPDOOR = registerBlock("willow_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)));
    public static final DeferredBlock<Block> WILLOW_DOOR = registerBlock("willow_door",
            () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR).noOcclusion()));
    public static final DeferredBlock<Block> WILLOW_SIGN = BLOCKS.register("willow_sign",
            () -> new ModStandingSignBlock(ModWoodTypes.WILLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> WILLOW_WALL_SIGN  = BLOCKS.register("willow_wall_sign",
            () -> new ModWallSignBlock(ModWoodTypes.WILLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> WILLOW_HANGING_SIGN  = BLOCKS.register("willow_hanging_sign",
            () -> new ModHangingSignBlock(ModWoodTypes.WILLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<Block> WILLOW_HANGING_WALL_SIGN = BLOCKS.register("willow_hanging_wall_sign",
            () -> new ModWallHangingSignBlock(ModWoodTypes.WILLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
