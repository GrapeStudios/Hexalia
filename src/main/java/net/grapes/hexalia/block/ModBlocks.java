package net.grapes.hexalia.block;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.custom.*;
import net.grapes.hexalia.block.custom.signs.ModHangingSignBlock;
import net.grapes.hexalia.block.custom.signs.ModStandingSignBlock;
import net.grapes.hexalia.block.custom.signs.ModWallHangingSignBlock;
import net.grapes.hexalia.block.custom.signs.ModWallSignBlock;
import net.grapes.hexalia.block.entity.ModFlammableRotatedPillarBlock;
import net.grapes.hexalia.effect.ModMobEffects;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.util.ModWoodTypes;
import net.grapes.hexalia.worldgen.tree.CottonwoodTreeGrower;
import net.grapes.hexalia.worldgen.tree.WillowTreeGrower;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static net.grapes.hexalia.block.custom.CandleSkullBlock.LIT;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, HexaliaMod.MOD_ID);

    // Natural Blocks
    public static final RegistryObject<Block> INFUSED_DIRT = registerBlock("infused_dirt",
            () -> new InfusedDirtBlock(BlockBehaviour.Properties.copy(Blocks.DIRT).sound(SoundType.MUD)));
    public static final RegistryObject<Block> INFUSED_FARMLAND = registerBlock("infused_farmland",
            () -> new InfusedFarmlandBlock(BlockBehaviour.Properties.copy(Blocks.DIRT).noOcclusion()
                    .sound(SoundType.MUD)));
    public static final RegistryObject<Block> SILKWORM_COCOON = registerBlock("silkworm_cocoon",
            () -> new CocoonBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .instrument(NoteBlockInstrument.BANJO).strength(0.5f).noCollission()));

    // Functional Plants
    public static final RegistryObject<Block> SPIRIT_BLOOM = registerBlock("spirit_bloom",
            () -> new HPlantBlock(() -> MobEffects.POISON, 6, BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> POTTED_SPIRIT_BLOOM = BLOCKS.register("potted_spirit_bloom",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), SPIRIT_BLOOM, BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM)));
    public static final RegistryObject<Block> DREAMSHROOM = registerBlock("dreamshroom",
            () -> new DreamshroomBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM).lightLevel(state -> 4)));
    public static final RegistryObject<Block> POTTED_DREAMSHROOM = BLOCKS.register("potted_dreamshroom",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), DREAMSHROOM, BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM)));
    public static final RegistryObject<Block> SIREN_KELP = BLOCKS.register("siren_kelp",
            () -> new SirenKelpBlock(BlockBehaviour.Properties.copy(Blocks.SEAGRASS)));
    public static final RegistryObject<Block> GHOST_FERN = registerBlock("ghost_fern",
            () -> new GhostFernBlock(() -> MobEffects.INVISIBILITY, 6, BlockBehaviour.Properties.copy(Blocks.AZALEA).noCollission()));

    // Decorative Plants
    public static final RegistryObject<Block> HENBANE = registerBlock("henbane",
            () -> new FlowerBlock(() -> MobEffects.ABSORPTION, 6, BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> POTTED_HENBANE = BLOCKS.register("potted_henbane",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), HENBANE, BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM)));
    public static final RegistryObject<Block> LOTUS_FLOWER = BLOCKS.register("lotus_flower",
            () -> new WaterPlantBlock(BlockBehaviour.Properties.copy(Blocks.LILY_PAD).lightLevel(state -> 6)));
    public static final RegistryObject<Block> PALE_MUSHROOM = registerBlock("pale_mushroom",
            () -> new HMushroomBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM).lightLevel(state -> 4)));
    public static final RegistryObject<Block> POTTED_PALE_MUSHROOM = BLOCKS.register("potted_pale_mushroom",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), PALE_MUSHROOM, BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM)));
    public static final RegistryObject<Block> WITCHWEED = registerBlock("witchweed",
            () -> new WitchweedBlock(() -> MobEffects.POISON, 6, BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> HEXED_BULRUSH = registerBlock("hexed_bulrush",
            () -> new HexedBulrushBlock(BlockBehaviour.Properties.copy(Blocks.AZALEA).noCollission().lightLevel(state -> 4)));
    public static final RegistryObject<Block> NIGHTSHADE_BUSH = registerBlock("nightshade_bush",
            () -> new FlowerBlock(() -> MobEffects.POISON, 6, BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> POTTED_NIGHTSHADE_BUSH = BLOCKS.register("potted_nightshade_bush",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), NIGHTSHADE_BUSH, BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM)));
    public static final RegistryObject<Block> DUCKWEED = BLOCKS.register("duckweed",
            () -> new WaterPlantBlock(BlockBehaviour.Properties.copy(Blocks.LILY_PAD).noCollission()));

    // Crop & Wild Crop Blocks
    public static final RegistryObject<Block> MANDRAKE_CROP = BLOCKS.register("mandrake_crop",
            () -> new MandrakeCropBlock(BlockBehaviour.Properties.copy(Blocks.POTATOES)));
    public static final RegistryObject<Block> WILD_MANDRAKE = registerBlock("wild_mandrake",
            () -> new FlowerBlock(ModMobEffects.STUNNED, 6, BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> SUNFIRE_TOMATO_CROP = BLOCKS.register("sunfire_tomato_crop",
            () -> new SunfireTomatoCropBlock(BlockBehaviour.Properties.copy(Blocks.POTATOES)));
    public static final RegistryObject<Block> WILD_SUNFIRE_TOMATO = registerBlock("wild_sunfire_tomato",
            () -> new WildSunfireTomatoBlock(BlockBehaviour.Properties.copy(Blocks.ALLIUM).lightLevel(state -> 4)));
    public static final RegistryObject<Block> CHILLBERRY_BUSH = BLOCKS.register("chillberry_bush",
            () -> new ChillberryBushBlock(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)));
    public static final RegistryObject<Block> RABBAGE_CROP = BLOCKS.register("rabbage_crop",
            () -> new RabbageCropBlock(BlockBehaviour.Properties.copy(Blocks.POTATOES)));
    public static final RegistryObject<Block> SALTSPROUT = BLOCKS.register("saltsprout",
            () -> new SaltsproutBlock(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)));

    //  Mineral-Related Blocks
    public static final RegistryObject<Block> SALT_ORE = registerBlock("salt_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> SALT_BLOCK = registerBlock("salt_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> SALT_LAMP = BLOCKS.register("salt_lamp",
            () -> new SaltLampBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE)
                    .instrument(NoteBlockInstrument.BANJO).strength(4f).requiresCorrectToolForDrops()
                    .lightLevel(state -> 12)));
    public static final RegistryObject<Block> SALT = BLOCKS.register("salt",
            () -> new SaltBlock(BlockBehaviour.Properties.copy(Blocks.TRIPWIRE)));

    // Functional Blocks
    public static final RegistryObject<Block> SMALL_CAULDRON = BLOCKS.register("small_cauldron",
            () -> new SmallCauldronBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> RUSTIC_OVEN = registerBlock("rustic_oven",
            () -> new RusticOvenBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BLACK)
                    .instrument(NoteBlockInstrument.BANJO).strength(4f).requiresCorrectToolForDrops()
                    .lightLevel(state -> 12)));
    public static final RegistryObject<Block> RITUAL_TABLE = BLOCKS.register("ritual_table",
            () -> new RitualTableBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion()));
    public static final RegistryObject<Block> BREW_SHELF = registerBlock("brew_shelf",
            () -> new BrewShelfBlock(BlockBehaviour.Properties.copy(Blocks.CHISELED_BOOKSHELF).noOcclusion()));

    // Decorative Blocks
    public static final RegistryObject<Block> PARCHMENT = registerBlock("parchment",
            () -> new HWallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                    .instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0f)));
    public static final RegistryObject<Block> DREAMCATCHER = registerBlock("dreamcatcher",
            () -> new HWallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                    .instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0f)));
    public static final RegistryObject<Block> CANDLE_SKULL = BLOCKS.register("candle_skull",
            () -> new CandleSkullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE)
                    .instrument(NoteBlockInstrument.GUITAR).strength(1.0f)
                    .lightLevel(state -> state.getValue(LIT) ? 12 : 0)));

    // Tree-Related Blocks
    public static final RegistryObject<Block> COTTONWOOD_CATKIN = registerBlock("cottonwood_catkin",
            () -> new CatkinBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).strength(0.2f)
                    .noCollission()));

    public static final RegistryObject<Block> COTTONWOOD_LEAVES = registerBlock("cottonwood_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).strength(0.2f)));
    public static final RegistryObject<Block> COTTONWOOD_SAPLING = registerBlock("cottonwood_sapling",
            () -> new SaplingBlock(new CottonwoodTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING).strength(0.2f)));
    public static final RegistryObject<Block> POTTED_COTTONWOOD_SAPLING = BLOCKS.register("potted_cottonwood_sapling",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), COTTONWOOD_SAPLING, BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM)));
    public static final RegistryObject<Block> COTTONWOOD_LOG = registerBlock("cottonwood_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
    public static final RegistryObject<Block> STRIPPED_COTTONWOOD_LOG = registerBlock("stripped_cottonwood_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final RegistryObject<Block> COTTONWOOD_WOOD = registerBlock("cottonwood_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));
    public static final RegistryObject<Block> STRIPPED_COTTONWOOD_WOOD = registerBlock("stripped_cottonwood_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final RegistryObject<Block> COTTONWOOD_PLANKS = registerBlock("cottonwood_planks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> COTTONWOOD_STAIRS = registerBlock("cottonwood_stairs",
            () -> new StairBlock(() -> ModBlocks.COTTONWOOD_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)));
    public static final RegistryObject<Block> COTTONWOOD_SLAB = registerBlock("cottonwood_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> COTTONWOOD_BUTTON = registerBlock("cottonwood_button",
            () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), BlockSetType.OAK, 10, true));
    public static final RegistryObject<Block> COTTONWOOD_PRESSURE_PLATE = registerBlock("cottonwood_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), BlockSetType.OAK));
    public static final RegistryObject<Block> COTTONWOOD_FENCE = registerBlock("cottonwood_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE)));
    public static final RegistryObject<Block> COTTONWOOD_FENCE_GATE = registerBlock("cottonwood_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE), WoodType.OAK));
    public static final RegistryObject<Block> COTTONWOOD_TRAPDOOR = registerBlock("cottonwood_trapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR).noOcclusion(), BlockSetType.OAK));
    public static final RegistryObject<Block> COTTONWOOD_DOOR = registerBlock("cottonwood_door",
            () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR).noOcclusion(), BlockSetType.OAK));

    public static final RegistryObject<Block> COTTONWOOD_SIGN = BLOCKS.register("cottonwood_sign",
            () -> new ModStandingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), ModWoodTypes.COTTONWOOD));
    public static final RegistryObject<Block> COTTONWOOD_WALL_SIGN  = BLOCKS.register("cottonwood_wall_sign",
            () -> new ModWallSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), ModWoodTypes.COTTONWOOD));
    public static final RegistryObject<Block> COTTONWOOD_HANGING_SIGN  = BLOCKS.register("cottonwood_hanging_sign",
            () -> new ModHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), ModWoodTypes.COTTONWOOD));
    public static final RegistryObject<Block> COTTONWOOD_HANGING_WALL_SIGN = BLOCKS.register("cottonwood_hanging_wall_sign",
            () -> new ModWallHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), ModWoodTypes.COTTONWOOD));

    public static final RegistryObject<Block> WILLOW_LEAVES = registerBlock("willow_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).strength(0.2f)));
    public static final RegistryObject<Block> WILLOW_SAPLING = registerBlock("willow_sapling",
            () -> new SaplingBlock(new WillowTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING).strength(0.2f)));
    public static final RegistryObject<Block> POTTED_WILLOW_SAPLING = BLOCKS.register("potted_willow_sapling",
            () -> new FlowerPotBlock((() -> (FlowerPotBlock) Blocks.FLOWER_POT), WILLOW_SAPLING, BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM)));
    public static final RegistryObject<Block> WILLOW_LOG = registerBlock("willow_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
    public static final RegistryObject<Block> STRIPPED_WILLOW_LOG = registerBlock("stripped_willow_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final RegistryObject<Block> WILLOW_WOOD = registerBlock("willow_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));
    public static final RegistryObject<Block> STRIPPED_WILLOW_WOOD = registerBlock("stripped_willow_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final RegistryObject<Block> WILLOW_PLANKS = registerBlock("willow_planks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> WILLOW_STAIRS = registerBlock("willow_stairs",
            () -> new StairBlock(() -> ModBlocks.WILLOW_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)));
    public static final RegistryObject<Block> WILLOW_SLAB = registerBlock("willow_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> WILLOW_BUTTON = registerBlock("willow_button",
            () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), BlockSetType.OAK, 10, true));
    public static final RegistryObject<Block> WILLOW_PRESSURE_PLATE = registerBlock("willow_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), BlockSetType.OAK));
    public static final RegistryObject<Block> WILLOW_FENCE = registerBlock("willow_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE)));
    public static final RegistryObject<Block> WILLOW_FENCE_GATE = registerBlock("willow_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE), WoodType.OAK));
    public static final RegistryObject<Block> WILLOW_TRAPDOOR = registerBlock("willow_trapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR).noOcclusion(), BlockSetType.OAK));
    public static final RegistryObject<Block> WILLOW_DOOR = registerBlock("willow_door",
            () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR).noOcclusion(), BlockSetType.OAK));

    public static final RegistryObject<Block> WILLOW_SIGN = BLOCKS.register("willow_sign",
            () -> new ModStandingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), ModWoodTypes.WILLOW));
    public static final RegistryObject<Block> WILLOW_WALL_SIGN  = BLOCKS.register("willow_wall_sign",
            () -> new ModWallSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), ModWoodTypes.WILLOW));
    public static final RegistryObject<Block> WILLOW_HANGING_SIGN  = BLOCKS.register("willow_hanging_sign",
            () -> new ModHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), ModWoodTypes.WILLOW));
    public static final RegistryObject<Block> WILLOW_HANGING_WALL_SIGN = BLOCKS.register("willow_hanging_wall_sign",
            () -> new ModWallHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), ModWoodTypes.WILLOW));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
