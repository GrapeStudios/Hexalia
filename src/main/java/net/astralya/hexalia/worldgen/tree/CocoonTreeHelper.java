package net.astralya.hexalia.worldgen.tree;

import java.util.Optional;
import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public final class CocoonTreeHelper {
    private static final int SCAN_RADIUS = 8;
    private static final float COCOON_CHANCE = 0.25f;

    private CocoonTreeHelper() {
    }

    public static boolean growTree(
            ServerWorld world,
            ChunkGenerator generator,
            BlockPos pos,
            BlockState saplingState,
            Random random,
            RegistryKey<ConfiguredFeature<?, ?>> plainKey,
            RegistryKey<ConfiguredFeature<?, ?>> cocoonKey
    ) {
        RegistryKey<ConfiguredFeature<?, ?>> key = hasNearbyGhostFern(world, pos) && random.nextFloat() < COCOON_CHANCE
                ? cocoonKey
                : plainKey;

        Optional<? extends RegistryEntry<ConfiguredFeature<?, ?>>> optional =
                world.getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).getEntry(key);

        if (optional.isEmpty()) {
            return false;
        }

        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);

        if (optional.get().value().generate(world, generator, random, pos)) {
            return true;
        }

        world.setBlockState(pos, saplingState, 4);
        return false;
    }

    public static boolean hasNearbyGhostFern(ServerWorld world, BlockPos pos) {
        for (BlockPos candidate : BlockPos.iterate(
                pos.add(-SCAN_RADIUS, -2, -SCAN_RADIUS),
                pos.add(SCAN_RADIUS, 2, SCAN_RADIUS)
        )) {
            if (world.getBlockState(candidate).isOf(ModBlocks.GHOST_FERN)) {
                return true;
            }
        }

        return false;
    }
}