package net.astralya.hexalia.block;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;

public final class ModBlockProperties {

    private ModBlockProperties() {
    }

    public static void register() {
        registerStrippables();
        registerFlammables();
    }

    private static void registerStrippables() {
        StrippableBlockRegistry.register(ModBlocks.COTTONWOOD_LOG, ModBlocks.STRIPPED_COTTONWOOD_LOG);
        StrippableBlockRegistry.register(ModBlocks.COTTONWOOD_WOOD, ModBlocks.STRIPPED_COTTONWOOD_WOOD);
        StrippableBlockRegistry.register(ModBlocks.WILLOW_LOG, ModBlocks.STRIPPED_WILLOW_LOG);
        StrippableBlockRegistry.register(ModBlocks.WILLOW_WOOD, ModBlocks.STRIPPED_WILLOW_WOOD);
    }

    public static void registerFlammables() {
        FlammableBlockRegistry instance = FlammableBlockRegistry.getDefaultInstance();

        // Blocks
        instance.add(ModBlocks.NESTING_BLOCK, 5, 20);
        instance.add(ModBlocks.RITUAL_BRAZIER, 5, 20);
        instance.add(ModBlocks.DREAMCATCHER, 5, 20);
        instance.add(ModBlocks.SILKWORM_COCOON, 60, 100);

        // Leaves
        instance.add(ModBlocks.COTTONWOOD_CATKIN, 30, 60);
        instance.add(ModBlocks.COTTONWOOD_LEAVES, 30, 60);
        instance.add(ModBlocks.WILLOW_LEAVES, 30, 60);

        // Saplings
        instance.add(ModBlocks.COTTONWOOD_SAPLING, 60, 100);
        instance.add(ModBlocks.WILLOW_SAPLING, 60, 100);

        // Cottonwood Logs and Wood
        instance.add(ModBlocks.COTTONWOOD_LOG, 5, 5);
        instance.add(ModBlocks.COTTONWOOD_WOOD, 5, 5);
        instance.add(ModBlocks.STRIPPED_COTTONWOOD_LOG, 5, 5);
        instance.add(ModBlocks.STRIPPED_COTTONWOOD_WOOD, 5, 5);

        // Cottonwood Planks and Variants
        instance.add(ModBlocks.COTTONWOOD_PLANKS, 5, 20);
        instance.add(ModBlocks.COTTONWOOD_STAIRS, 5, 20);
        instance.add(ModBlocks.COTTONWOOD_SLAB, 5, 20);
        instance.add(ModBlocks.COTTONWOOD_FENCE, 5, 20);
        instance.add(ModBlocks.COTTONWOOD_FENCE_GATE, 5, 20);
        instance.add(ModBlocks.COTTONWOOD_TRAPDOOR, 5, 20);
        instance.add(ModBlocks.COTTONWOOD_DOOR, 5, 20);
        instance.add(ModBlocks.COTTONWOOD_BUTTON, 5, 20);
        instance.add(ModBlocks.COTTONWOOD_PRESSURE_PLATE, 5, 20);

        // Cottonwood Signs
        instance.add(ModBlocks.COTTONWOOD_SIGN, 5, 20);
        instance.add(ModBlocks.COTTONWOOD_WALL_SIGN, 5, 20);
        instance.add(ModBlocks.COTTONWOOD_HANGING_SIGN, 5, 20);
        instance.add(ModBlocks.COTTONWOOD_HANGING_WALL_SIGN, 5, 20);

        // Willow Logs and Wood
        instance.add(ModBlocks.WILLOW_LOG, 5, 5);
        instance.add(ModBlocks.WILLOW_WOOD, 5, 5);
        instance.add(ModBlocks.STRIPPED_WILLOW_LOG, 5, 5);
        instance.add(ModBlocks.STRIPPED_WILLOW_WOOD, 5, 5);

        // Willow Planks and Variants
        instance.add(ModBlocks.WILLOW_PLANKS, 5, 20);
        instance.add(ModBlocks.WILLOW_STAIRS, 5, 20);
        instance.add(ModBlocks.WILLOW_SLAB, 5, 20);
        instance.add(ModBlocks.WILLOW_FENCE, 5, 20);
        instance.add(ModBlocks.WILLOW_FENCE_GATE, 5, 20);
        instance.add(ModBlocks.WILLOW_TRAPDOOR, 5, 20);
        instance.add(ModBlocks.WILLOW_DOOR, 5, 20);
        instance.add(ModBlocks.WILLOW_BUTTON, 5, 20);
        instance.add(ModBlocks.WILLOW_PRESSURE_PLATE, 5, 20);

        // Willow Signs
        instance.add(ModBlocks.WILLOW_SIGN, 5, 20);
        instance.add(ModBlocks.WILLOW_WALL_SIGN, 5, 20);
        instance.add(ModBlocks.WILLOW_HANGING_SIGN, 5, 20);
        instance.add(ModBlocks.WILLOW_HANGING_WALL_SIGN, 5, 20);

        // Herbs and Plants
        instance.add(ModBlocks.SPIRIT_BLOOM, 60, 100);
        instance.add(ModBlocks.GHOST_FERN, 60, 100);
        instance.add(ModBlocks.CELESTIAL_BLOOM, 60, 100);
        instance.add(ModBlocks.WITHERED_CELESTIAL_BLOOM, 60, 100);
        instance.add(ModBlocks.WILD_MANDRAKE, 60, 100);
        instance.add(ModBlocks.CHILLBERRY_BUSH, 60, 100);

        // Enchanted Plants
        instance.add(ModBlocks.MORPHORA, 60, 100);
        instance.add(ModBlocks.GRIMSHADE, 60, 100);
        instance.add(ModBlocks.WINDSONG, 60, 100);
        instance.add(ModBlocks.ASTRYLIS, 60, 100);
        instance.add(ModBlocks.LOURDES, 60, 100);
        instance.add(ModBlocks.AEGIFLORA, 60, 100);
        instance.add(ModBlocks.WITHERED_AEGIFLORA, 60, 100);

        // Decorative Flowers
        instance.add(ModBlocks.BEGONIA, 60, 100);
        instance.add(ModBlocks.LAVENDER, 60, 100);
        instance.add(ModBlocks.DAHLIA, 60, 100);
        instance.add(ModBlocks.WITCHWEED, 60, 100);
        instance.add(ModBlocks.NIGHTSHADE_BUSH, 60, 100);

        // Vines and Decorative Blocks
        instance.add(ModBlocks.GALEBERRIES_VINE, 60, 100);
        instance.add(ModBlocks.GALEBERRIES_VINE_PLANT, 60, 100);
    }
}