package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.worldgen.tree.CocoonTreeHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class CocoonSaplingBlock extends SaplingBlock {
    private final RegistryKey<ConfiguredFeature<?, ?>> plainKey;
    private final RegistryKey<ConfiguredFeature<?, ?>> cocoonKey;

    public CocoonSaplingBlock(
            SaplingGenerator generator,
            RegistryKey<ConfiguredFeature<?, ?>> plainKey,
            RegistryKey<ConfiguredFeature<?, ?>> cocoonKey,
            Settings settings
    ) {
        super(generator, settings);
        this.plainKey = plainKey;
        this.cocoonKey = cocoonKey;
    }

    @Override
    public void generate(ServerWorld world, BlockPos pos, BlockState state, Random random) {
        CocoonTreeHelper.growTree(
                world,
                world.getChunkManager().getChunkGenerator(),
                pos,
                state,
                random,
                plainKey,
                cocoonKey
        );
    }
}