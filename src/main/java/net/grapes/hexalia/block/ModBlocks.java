package net.grapes.hexalia.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.custom.*;
import net.grapes.hexalia.block.custom.signs.ModHangingSignBlock;
import net.grapes.hexalia.block.custom.signs.ModStandingSignBlock;
import net.grapes.hexalia.block.custom.signs.ModWallHangingSignBlock;
import net.grapes.hexalia.block.custom.signs.ModWallSignBlock;
import net.grapes.hexalia.util.ModWoodTypes;
import net.minecraft.block.*;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.sapling.OakSaplingGenerator;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    // Natural Blocks
    public static final Block SPIRIT_BLOOM = registerBlock("spirit_bloom",
            new InfusableFlowerBlock(StatusEffects.LEVITATION, 4, FabricBlockSettings.copyOf(Blocks.ALLIUM).nonOpaque().noCollision()));
    public static final Block POTTED_SPIRIT_BLOOM = registerBlockWithoutBlockItem("potted_spirit_bloom",
            new FlowerPotBlock(SPIRIT_BLOOM, FabricBlockSettings.copyOf(Blocks.POTTED_ALLIUM)));
    public static final Block DREAMSHROOM = registerBlock("dreamshroom",
            new DreamshroomBlock(FabricBlockSettings.copyOf(Blocks.BROWN_MUSHROOM).luminance(4).nonOpaque().noCollision()));
    public static final Block POTTED_DREAMSHROOM = registerBlockWithoutBlockItem("potted_dreamshroom",
            new FlowerPotBlock(DREAMSHROOM, FabricBlockSettings.copyOf(Blocks.POTTED_ALLIUM).luminance(4)));
    public static final Block SIREN_KELP = registerBlockWithoutBlockItem("siren_kelp",
            new SirenKelpBlock(FabricBlockSettings.copyOf(Blocks.SEAGRASS)));
    public static final Block INFUSED_DIRT = registerBlock("infused_dirt",
            new InfusedDirtBlock(FabricBlockSettings.copyOf(Blocks.DIRT).sounds(BlockSoundGroup.MUD)));
    public static final Block INFUSED_FARMLAND = registerBlock("infused_farmland",
            new InfusedFarmlandBlock(FabricBlockSettings.copyOf(Blocks.FARMLAND).sounds(BlockSoundGroup.MUD)));
    public static final Block MANDRAKE_CROP = registerBlockWithoutBlockItem("mandrake_crop",
            new MandrakeCropBlock(FabricBlockSettings.copyOf(Blocks.POTATOES)));
    public static final Block WILD_MANDRAKE = registerBlock("wild_mandrake",
            new FlowerBlock(StatusEffects.LEVITATION, 4, FabricBlockSettings.copyOf(Blocks.ALLIUM).nonOpaque().noCollision()));
    public static final Block SUNFIRE_TOMATO_CROP = registerBlockWithoutBlockItem("sunfire_tomato_crop",
            new SunfireTomatoCropBlock(FabricBlockSettings.copyOf(Blocks.POTATOES)));
    public static final Block WILD_SUNFIRE_TOMATO = registerBlock("wild_sunfire_tomato",
            new WildSunfireTomatoBlock(FabricBlockSettings.copyOf(Blocks.CORNFLOWER).luminance(state -> 12).nonOpaque()));
    public static final Block CHILLBERRY_BUSH = registerBlockWithoutBlockItem("chillberry_bush",
            new ChillberryBushBlock(FabricBlockSettings.copyOf(Blocks.SWEET_BERRY_BUSH)));
    public static final Block RABBAGE_CROP = registerBlockWithoutBlockItem("rabbage_crop",
            new RabbageCropBlock(FabricBlockSettings.copyOf(Blocks.POTATOES)));
    public static final Block HENBANE = registerBlock("henbane",
            new FlowerBlock(StatusEffects.POISON, 4, FabricBlockSettings.copyOf(Blocks.ALLIUM).nonOpaque().noCollision()));
    public static final Block POTTED_HENBANE = registerBlockWithoutBlockItem("potted_henbane",
            new FlowerPotBlock(HENBANE, FabricBlockSettings.copyOf(Blocks.POTTED_ALLIUM)));
    public static final Block SILKWORM_COCOON = registerBlock("silkworm_cocoon",
            new SilkwormCocoonBlock(FabricBlockSettings.create().mapColor(MapColor.WHITE_GRAY).instrument(Instrument.BANJO)
                    .strength(0.5f).noCollision()));

    // Mineral-Related Blocks
    public static final Block SALT_ORE = registerBlock("salt_ore",
            new Block(FabricBlockSettings.copyOf(Blocks.COAL_ORE)));
    public static final Block SALT_BLOCK = registerBlock("salt_block",
            new Block(FabricBlockSettings.copyOf(Blocks.COAL_BLOCK)));
    public static final Block SALT_LAMP = registerBlockWithoutBlockItem("salt_lamp",
            new SaltLampBlock(FabricBlockSettings.create().mapColor(MapColor.WHITE).instrument(Instrument.BANJO)
                    .strength(4f).requiresTool().luminance(state -> 12)));
    public static final Block SALT = registerBlockWithoutBlockItem("salt",
            new SaltBlock(FabricBlockSettings.copyOf(Blocks.TRIPWIRE)));

    // Functional Blocks
    public static final Block SMALL_CAULDRON = registerBlockWithoutBlockItem("small_cauldron",
            new SmallCauldronBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block RUSTIC_OVEN = registerBlock("rustic_oven",
            new RusticOvenBlock(FabricBlockSettings.create().mapColor(MapColor.ORANGE).instrument(Instrument.BASEDRUM)
                    .strength(4f).requiresTool().luminance(state -> 12)));
    public static final Block RITUAL_TABLE = registerBlockWithoutBlockItem("ritual_table",
            new RitualTableBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));
    public static final Block BREW_SHELF = registerBlock("brew_shelf",
            new BrewShelfBlock(FabricBlockSettings.copyOf(Blocks.CHISELED_BOOKSHELF).nonOpaque()));

    // Tree-Related Blocks
    public static final Block COTTONWOOD_LEAVES = registerBlock("cottonwood_leaves",
            new LeavesBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES).strength(1f)));
    public static final Block COTTONWOOD_LOG = registerBlock("cottonwood_log",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG).strength(4f)));
    public static final Block COTTONWOOD_WOOD = registerBlock("cottonwood_wood",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG).strength(4f)));
    public static final Block STRIPPED_COTTONWOOD_LOG = registerBlock("stripped_cottonwood_log",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG).strength(4f)));
    public static final Block STRIPPED_COTTONWOOD_WOOD = registerBlock("stripped_cottonwood_wood",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG).strength(4f)));
    public static final Block COTTONWOOD_PLANKS = registerBlock("cottonwood_planks",
            new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(4f).requiresTool()));
    public static final Block COTTONWOOD_SAPLING = registerBlock("cottonwood_sapling",
            new SaplingBlock(new OakSaplingGenerator(), FabricBlockSettings.copyOf(Blocks.OAK_SAPLING)));
    public static final Block COTTONWOOD_STAIRS = registerBlock("cottonwood_stairs",
            new StairsBlock(ModBlocks.COTTONWOOD_PLANKS.getDefaultState(),
                    FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)));
    public static final Block COTTONWOOD_SLAB = registerBlock("cottonwood_slab",
            new SlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)));
    public static final Block COTTONWOOD_BUTTON = registerBlock("cottonwood_button",
            new ButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS), BlockSetType.OAK, 10, true));
    public static final Block COTTONWOOD_PRESSURE_PLATE = registerBlock("cottonwood_pressure_plate",
            new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING,
                    FabricBlockSettings.copyOf(Blocks.OAK_PLANKS), BlockSetType.OAK));
    public static final Block COTTONWOOD_FENCE = registerBlock("cottonwood_fence",
            new FenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)));
    public static final Block COTTONWOOD_FENCE_GATE = registerBlock("cottonwood_fence_gate",
            new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS), WoodType.OAK));
    public static final Block COTTONWOOD_TRAPDOOR = registerBlock("cottonwood_trapdoor",
            new TrapdoorBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque(), BlockSetType.OAK));
    public static final Block COTTONWOOD_DOOR = registerBlock("cottonwood_door",
            new DoorBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque(), BlockSetType.OAK));
    public static final Block COTTONWOOD_SIGN = registerBlockWithoutBlockItem("cottonwood_sign",
            new ModStandingSignBlock(FabricBlockSettings.copyOf(Blocks.OAK_SIGN).nonOpaque(), ModWoodTypes.COTTONWOOD));
    public static final Block COTTONWOOD_WALL_SIGN = registerBlockWithoutBlockItem("cottonwood_wall_sign",
            new ModWallSignBlock(FabricBlockSettings.copyOf(Blocks.OAK_WALL_SIGN), ModWoodTypes.COTTONWOOD));
    public static final Block COTTONWOOD_HANGING_SIGN = registerBlockWithoutBlockItem("cottonwood_hanging_sign",
            new ModHangingSignBlock(FabricBlockSettings.copyOf(Blocks.OAK_HANGING_SIGN), ModWoodTypes.COTTONWOOD));
    public static final Block COTTONWOOD_HANGING_WALL_SIGN = registerBlockWithoutBlockItem("cottonwood_hanging_wall_sign",
            new ModWallHangingSignBlock(FabricBlockSettings.copyOf(Blocks.OAK_WALL_HANGING_SIGN), ModWoodTypes.COTTONWOOD));

    // Decorative Blocks
    public static final Block PARCHMENT = registerBlockWithoutBlockItem("parchment",
            new ParchmentBlock(FabricBlockSettings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS)
                    .noCollision().strength(1.0f)));
    public static final Block DREAMCATCHER = registerBlockWithoutBlockItem("dreamcatcher",
            new DreamcatcherBlock(FabricBlockSettings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS)
                    .noCollision().strength(1.0f)));
    public static final Block CANDLE_SKULL = registerBlockWithoutBlockItem("candle_skull",
            new CandleSkullBlock(FabricBlockSettings.create().mapColor(MapColor.WHITE_GRAY)
                    .instrument(Instrument.GUITAR).strength(1.0f).luminance(state -> state.get(CandleSkullBlock.LIT) ? 8 : 0)));

    // Registries
    public static void registerBlockProperties() {
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.SPIRIT_BLOOM, 0.5F);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.DREAMSHROOM, 0.5F);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.HENBANE, 0.5F);

        StrippableBlockRegistry.register(ModBlocks.COTTONWOOD_LOG, ModBlocks.STRIPPED_COTTONWOOD_LOG);
        StrippableBlockRegistry.register(ModBlocks.COTTONWOOD_WOOD, ModBlocks.STRIPPED_COTTONWOOD_WOOD);

        FlammableBlockRegistry instance = FlammableBlockRegistry.getDefaultInstance();
        instance.add(ModBlocks.SPIRIT_BLOOM, 60, 100);
        instance.add(ModBlocks.CHILLBERRY_BUSH, 60, 100);
        instance.add(ModBlocks.WILD_MANDRAKE, 60, 100);
        instance.add(ModBlocks.HENBANE, 60, 100);
        instance.add(ModBlocks.PARCHMENT, 5, 20);
        instance.add(ModBlocks.DREAMCATCHER, 5, 20);
        instance.add(ModBlocks.SILKWORM_COCOON, 6, 100);
        instance.add(ModBlocks.COTTONWOOD_LOG, 5, 20);
        instance.add(ModBlocks.COTTONWOOD_WOOD, 5, 20);
        instance.add(ModBlocks.STRIPPED_COTTONWOOD_LOG, 5, 20);
        instance.add(ModBlocks.STRIPPED_COTTONWOOD_WOOD, 5, 20);
        instance.add(ModBlocks.COTTONWOOD_PLANKS, 5, 20);
    }

    // Methods
    private static Block registerBlockWithoutBlockItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(HexaliaMod.MOD_ID, name), block);
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(HexaliaMod.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, new Identifier(HexaliaMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        HexaliaMod.LOGGER.info("Registering Blocks for " + HexaliaMod.MOD_ID);
    }
}