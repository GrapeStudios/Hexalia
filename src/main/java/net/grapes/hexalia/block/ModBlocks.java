package net.grapes.hexalia.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.custom.*;
import net.minecraft.block.*;
import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    // Herbology blocks
    public static final Block SPIRIT_BLOOM = registerBlock("spirit_bloom",
            new FlowerBlock(StatusEffects.LEVITATION, 4, FabricBlockSettings.copyOf(Blocks.ALLIUM).nonOpaque().noCollision()));
    public static final Block POTTED_SPIRIT_BLOOM = registerBlockWithoutBlockItem("potted_spirit_bloom",
            new FlowerPotBlock(SPIRIT_BLOOM, FabricBlockSettings.copyOf(Blocks.POTTED_ALLIUM)));
    public static final Block DREAMSHROOM = registerBlock("dreamshroom",
            new DreamshroomBlock(FabricBlockSettings.copyOf(Blocks.BROWN_MUSHROOM).luminance(4).nonOpaque().noCollision()));
    public static final Block POTTED_DREAMSHROOM = registerBlockWithoutBlockItem("potted_dreamshroom",
            new FlowerPotBlock(DREAMSHROOM, FabricBlockSettings.copyOf(Blocks.POTTED_ALLIUM).luminance(4)));
    public static final Block SIREN_KELP = registerBlockWithoutBlockItem("siren_kelp",
            new SirenKelpBlock(FabricBlockSettings.copyOf(Blocks.SEAGRASS)));

    // Crop blocks
    public static final Block MANDRAKE_CROP = registerBlockWithoutBlockItem("mandrake_crop",
            new MandrakeCropBlock(FabricBlockSettings.copyOf(Blocks.POTATOES)));
    public static final Block FERAL_MANDRAKE = registerBlock("feral_mandrake",
            new FlowerBlock(StatusEffects.LEVITATION, 4, FabricBlockSettings.copyOf(Blocks.ALLIUM).nonOpaque().noCollision()));
    public static final Block SUNFIRE_TOMATO_CROP = registerBlockWithoutBlockItem("sunfire_tomato_crop",
            new SunfireTomatoCropBlock(FabricBlockSettings.copyOf(Blocks.POTATOES)));
    public static final Block FERAL_SUNFIRE_TOMATO = registerBlock("feral_sunfire_tomato",
            new FeralSunfireTomato(FabricBlockSettings.copyOf(Blocks.CORNFLOWER).luminance(state -> 12).nonOpaque()));
    public static final Block CHILLBERRY_BUSH = registerBlockWithoutBlockItem("chillberry_bush",
            new ChillberryBushBlock(FabricBlockSettings.copyOf(Blocks.SWEET_BERRY_BUSH)));

    // Other blocks
    public static final Block SALT_ORE = registerBlock("salt_ore",
            new Block(FabricBlockSettings.copyOf(Blocks.COAL_ORE)));
    public static final Block SALT_BLOCK = registerBlock("salt_block",
            new Block(FabricBlockSettings.copyOf(Blocks.COAL_BLOCK)));
    public static final Block SMALL_CAULDRON = registerBlockWithoutBlockItem("small_cauldron",
            new SmallCauldronBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block SALT_LAMP = registerBlockWithoutBlockItem("salt_lamp",
            new SaltLampBlock(FabricBlockSettings.create().mapColor(MapColor.PALE_YELLOW).instrument(Instrument.BANJO)
                    .strength(4f).requiresTool().luminance(state -> 12)));
    public static final Block RUSTIC_OVEN = registerBlock("rustic_oven",
            new RusticOven(FabricBlockSettings.create().mapColor(MapColor.ORANGE).instrument(Instrument.BASEDRUM)
                    .strength(4f).requiresTool().luminance(state -> 12)));

    // Registries
    public static void registerBlockProperties() {
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.SPIRIT_BLOOM, 0.5F);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.DREAMSHROOM, 0.5F);

        FlammableBlockRegistry instance = FlammableBlockRegistry.getDefaultInstance();
        instance.add(ModBlocks.SPIRIT_BLOOM, 100, 60);
        instance.add(ModBlocks.DREAMSHROOM, 100, 60);
        instance.add(ModBlocks.CHILLBERRY_BUSH, 100, 60);
        instance.add(ModBlocks.FERAL_MANDRAKE, 100, 60);
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