package net.astralya.hexalia.worldgen.tree;

import net.astralya.hexalia.worldgen.ModConfiguredFeatures;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class ModSaplingGenerators {

    public static final SaplingGenerator WILLOW = new SaplingGenerator() {
        @Override
        protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
            return ModConfiguredFeatures.WILLOW;
        }
    };

    public static final SaplingGenerator COTTONWOOD = new SaplingGenerator() {
        @Override
        protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
            return ModConfiguredFeatures.COTTONWOOD;
        }
    };
}