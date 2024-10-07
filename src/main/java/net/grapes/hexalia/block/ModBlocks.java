package net.grapes.hexalia.block;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, HexaliaMod.MOD_ID);

    // Natural Blocks
    public static final RegistryObject<Block> INFUSED_DIRT = registerBlock("infused_dirt",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT)));
    public static final RegistryObject<Block> INFUSED_FARMLAND = registerBlock("infused_farmland",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT)));
    public static final RegistryObject<Block> SILKWORM_COCOON = BLOCKS.register("silkworm_cocoon",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .instrument(NoteBlockInstrument.BANJO).strength(0.5f).noCollission()));

    // Functional Plants
    public static final RegistryObject<Block> SPIRIT_BLOOM = registerBlock("spirit_bloom",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> POTTED_SPIRIT_BLOOM = BLOCKS.register("potted_spirit_bloom",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM)));
    public static final RegistryObject<Block> DREAMSHROOM = registerBlock("dreamshroom",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> POTTED_DREAMSHROOM = BLOCKS.register("potted_dreamshroom",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM)));
    public static final RegistryObject<Block> SIREN_KELP = BLOCKS.register("siren_kelp",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> GHOST_FERN = registerBlock("ghost_fern",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));

    // Decorative Plants
    public static final RegistryObject<Block> HENBANE = registerBlock("henbane",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> POTTED_HENBANE = BLOCKS.register("potted_henbane",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM)));
    public static final RegistryObject<Block> LOTUS_FLOWER = BLOCKS.register("lotus_flower",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.LILY_PAD)));
    public static final RegistryObject<Block> PALE_MUSHROOM = registerBlock("pale_mushroom",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> POTTED_PALE_MUSHROOM = BLOCKS.register("potted_pale_mushroom",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM)));
    public static final RegistryObject<Block> WITCHWEED = registerBlock("witchweed",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> HEXED_BULRUSH = registerBlock("hexed_bulrush",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> NIGHTSHADE_BUSH = registerBlock("nightshade_bush",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> POTTED_NIGHTSHADE_BUSH = BLOCKS.register("potted_nightshade_bush",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM)));
    public static final RegistryObject<Block> DUCKWEED = BLOCKS.register("duckweed",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));

    // Crop & Wild Crop Blocks
    public static final RegistryObject<Block> MANDRAKE_CROP = BLOCKS.register("mandrake_crop",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> WILD_MANDRAKE = BLOCKS.register("wild_mandrake",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> SUNFIRE_TOMATO_CROP = BLOCKS.register("sunfire_tomato_crop",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> WILD_SUNFIRE_TOMATO = BLOCKS.register("wild_sunfire_tomato",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> CHILLBERRY_BUSH = BLOCKS.register("chillberry_bush",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> RABBAGE_CROP = BLOCKS.register("rabbage_crop",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));
    public static final RegistryObject<Block> SALTSPROUT = BLOCKS.register("saltsprout",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ALLIUM)));

    //  Mineral-Related Blocks
    public static final RegistryObject<Block> SALT_ORE = registerBlock("salt_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> SALT_BLOCK = registerBlock("salt_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> SALT_LAMP = BLOCKS.register("salt_lamp",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> SALT = BLOCKS.register("salt",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));

    // Functional Blocks
    public static final RegistryObject<Block> SMALL_CAULDRON = BLOCKS.register("small_cauldron",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> RUSTIC_OVEN = registerBlock("rustic_oven",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> RITUAL_TABLE = BLOCKS.register("ritual_table",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> BREW_SHELF = registerBlock("brew_shelf",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));

    // Decorative Blocks
    public static final RegistryObject<Block> PARCHMENT = registerBlock("parchment",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> DREAMCATCHER = registerBlock("dreamcatcher",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> CANDLE_SKULL = BLOCKS.register("candle_skull",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));

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
