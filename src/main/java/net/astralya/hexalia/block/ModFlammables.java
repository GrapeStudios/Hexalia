package net.astralya.hexalia.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;

public final class ModFlammables {
    private ModFlammables() {}

    public static void register() {
        FireBlock fire = (FireBlock) Blocks.FIRE;

        // Leaves
        fire.setFlammable(ModBlocks.COTTONWOOD_CATKIN.get(), 30, 60);
        fire.setFlammable(ModBlocks.COTTONWOOD_LEAVES.get(), 30, 60);
        fire.setFlammable(ModBlocks.WILLOW_LEAVES.get(), 30, 60);

        // Saplings
        fire.setFlammable(ModBlocks.COTTONWOOD_SAPLING.get(), 60, 100);
        fire.setFlammable(ModBlocks.WILLOW_SAPLING.get(), 60, 100);

        // Wood
        fire.setFlammable(ModBlocks.COTTONWOOD_PLANKS.get(), 5, 20);
        fire.setFlammable(ModBlocks.COTTONWOOD_STAIRS.get(), 5, 20);
        fire.setFlammable(ModBlocks.COTTONWOOD_SLAB.get(), 5, 20);
        fire.setFlammable(ModBlocks.COTTONWOOD_FENCE.get(), 5, 20);
        fire.setFlammable(ModBlocks.COTTONWOOD_FENCE_GATE.get(), 5, 20);
        fire.setFlammable(ModBlocks.COTTONWOOD_TRAPDOOR.get(), 5, 20);
        fire.setFlammable(ModBlocks.COTTONWOOD_DOOR.get(), 5, 20);
        fire.setFlammable(ModBlocks.COTTONWOOD_BUTTON.get(), 5, 20);
        fire.setFlammable(ModBlocks.COTTONWOOD_PRESSURE_PLATE.get(), 5, 20);
        fire.setFlammable(ModBlocks.COTTONWOOD_SIGN.get(), 5, 20);
        fire.setFlammable(ModBlocks.COTTONWOOD_WALL_SIGN.get(), 5, 20);
        fire.setFlammable(ModBlocks.COTTONWOOD_HANGING_SIGN.get(), 5, 20);
        fire.setFlammable(ModBlocks.COTTONWOOD_HANGING_WALL_SIGN.get(), 5, 20);
        fire.setFlammable(ModBlocks.WILLOW_PLANKS.get(), 5, 20);
        fire.setFlammable(ModBlocks.WILLOW_STAIRS.get(), 5, 20);
        fire.setFlammable(ModBlocks.WILLOW_SLAB.get(), 5, 20);
        fire.setFlammable(ModBlocks.WILLOW_FENCE.get(), 5, 20);
        fire.setFlammable(ModBlocks.WILLOW_FENCE_GATE.get(), 5, 20);
        fire.setFlammable(ModBlocks.WILLOW_TRAPDOOR.get(), 5, 20);
        fire.setFlammable(ModBlocks.WILLOW_DOOR.get(), 5, 20);
        fire.setFlammable(ModBlocks.WILLOW_BUTTON.get(), 5, 20);
        fire.setFlammable(ModBlocks.WILLOW_PRESSURE_PLATE.get(), 5, 20);
        fire.setFlammable(ModBlocks.WILLOW_SIGN.get(), 5, 20);
        fire.setFlammable(ModBlocks.WILLOW_WALL_SIGN.get(), 5, 20);
        fire.setFlammable(ModBlocks.WILLOW_HANGING_SIGN.get(), 5, 20);
        fire.setFlammable(ModBlocks.WILLOW_HANGING_WALL_SIGN.get(), 5, 20);

        // Herbs
        fire.setFlammable(ModBlocks.SPIRIT_BLOOM.get(), 60, 100);
        fire.setFlammable(ModBlocks.GHOST_FERN.get(), 60, 100);
        fire.setFlammable(ModBlocks.CELESTIAL_BLOOM.get(), 60, 100);
        fire.setFlammable(ModBlocks.WILD_MANDRAKE.get(), 60, 100);
        fire.setFlammable(ModBlocks.CHILLBERRY_BUSH.get(), 60, 100);

        // Enchanted Plants
        fire.setFlammable(ModBlocks.MORPHORA.get(), 60, 100);
        fire.setFlammable(ModBlocks.GRIMSHADE.get(), 60, 100);
        fire.setFlammable(ModBlocks.WINDSONG.get(), 60, 100);
        fire.setFlammable(ModBlocks.ASTRYLIS.get(), 60, 100);
        fire.setFlammable(ModBlocks.LOURDES.get(), 60, 100);

        // Decorative Flowers
        fire.setFlammable(ModBlocks.BEGONIA.get(), 60, 100);
        fire.setFlammable(ModBlocks.LAVENDER.get(), 60, 100);
        fire.setFlammable(ModBlocks.DAHLIA.get(), 60, 100);
        fire.setFlammable(ModBlocks.WITCHWEED.get(), 60, 100);
        fire.setFlammable(ModBlocks.NIGHTSHADE_BUSH.get(), 60, 100);

        // Decorative Flowers
        fire.setFlammable(ModBlocks.GALEBERRIES_VINE.get(), 60, 100);
        fire.setFlammable(ModBlocks.GALEBERRIES_VINE_PLANT.get(), 60, 100);
        fire.setFlammable(ModBlocks.DREAMCATCHER.get(), 5, 20);
        fire.setFlammable(ModBlocks.SILKWORM_COCOON.get(), 60, 100);
        fire.setFlammable(ModBlocks.RITUAL_BRAZIER.get(), 5, 20);
    }
}