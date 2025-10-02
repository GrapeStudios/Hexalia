package net.astralya.hexalia.worldgen;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.ChillberryBushBlock;
import net.astralya.hexalia.worldgen.gen.decorator.CatkinTreeDecorator;
import net.astralya.hexalia.worldgen.gen.decorator.CocoonTreeDecorator;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.CherryFoliagePlacer;
import net.minecraft.world.gen.foliage.DarkOakFoliagePlacer;
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.DarkOakTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

import java.util.List;

public final class ModConfiguredFeatures {

    public static final RegistryKey<ConfiguredFeature<?, ?>> SPIRIT_BLOOM = registerKey("spirit_bloom");
    public static final RegistryKey<ConfiguredFeature<?, ?>> DREAMSHROOM = registerKey("dreamshroom");
    public static final RegistryKey<ConfiguredFeature<?, ?>> SIREN_KELP = registerKey("siren_kelp");
    public static final RegistryKey<ConfiguredFeature<?, ?>> CHILLBERRY = registerKey("chillberry");
    public static final RegistryKey<ConfiguredFeature<?, ?>> WILD_SUNFIRE_TOMATO = registerKey("wild_sunfire_tomato");
    public static final RegistryKey<ConfiguredFeature<?, ?>> WILD_MANDRAKE = registerKey("wild_mandrake");
    public static final RegistryKey<ConfiguredFeature<?, ?>> CELESTIAL_BLOOM = registerKey("celestial_bloom");
    public static final RegistryKey<ConfiguredFeature<?, ?>> GHOST_FERN = registerKey("ghost_fern");

    public static final RegistryKey<ConfiguredFeature<?, ?>> BEGONIA = registerKey("begonia");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LAVENDER = registerKey("lavender");
    public static final RegistryKey<ConfiguredFeature<?, ?>> DAHLIA = registerKey("dahlia");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LOTUS_FLOWER = registerKey("lotus_flower");
    public static final RegistryKey<ConfiguredFeature<?, ?>> PALE_MUSHROOM = registerKey("pale_mushroom");
    public static final RegistryKey<ConfiguredFeature<?, ?>> WITCHWEED = registerKey("witchweed");
    public static final RegistryKey<ConfiguredFeature<?, ?>> NIGHTSHADE_BUSH = registerKey("nightshade_bush");

    public static final RegistryKey<ConfiguredFeature<?, ?>> DARK_OAK_COCOON = registerKey("dark_oak_cocoon");
    public static final RegistryKey<ConfiguredFeature<?, ?>> COTTONWOOD = registerKey("cottonwood");
    public static final RegistryKey<ConfiguredFeature<?, ?>> COTTONWOOD_COCOON = registerKey("cottonwood_cocoon");
    public static final RegistryKey<ConfiguredFeature<?, ?>> WILLOW = registerKey("willow");

    private ModConfiguredFeatures() {}

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> ctx) {
        registerFunctionalPlants(ctx);
        registerDecorativePlants(ctx);
        registerTrees(ctx);
    }

    private static void registerFunctionalPlants(Registerable<ConfiguredFeature<?, ?>> ctx) {
        register(ctx, SPIRIT_BLOOM, Feature.FLOWER,
                randomPatch(ModBlocks.SPIRIT_BLOOM, 2, 3, 1));
        register(ctx, DREAMSHROOM, Feature.RANDOM_PATCH,
                randomPatch(ModBlocks.DREAMSHROOM, 2, 3, 1));
        register(ctx, SIREN_KELP, Feature.RANDOM_PATCH,
                randomPatch(ModBlocks.SIREN_KELP, 2, 3, 1));
        register(ctx, CHILLBERRY, Feature.RANDOM_PATCH,
                new RandomPatchFeatureConfig(
                        30, 8, 4,
                        PlacedFeatures.createEntry(
                                Feature.SIMPLE_BLOCK,
                                new SimpleBlockFeatureConfig(
                                        BlockStateProvider.of(ModBlocks.CHILLBERRY_BUSH.getDefaultState()
                                                .with(ChillberryBushBlock.AGE, 3)))
                        )
                )
        );
        register(ctx, WILD_SUNFIRE_TOMATO, Feature.FLOWER,
                randomPatch(ModBlocks.WILD_SUNFIRE_TOMATO, 3, 7, 3));
        register(ctx, WILD_MANDRAKE, Feature.FLOWER,
                randomPatch(ModBlocks.WILD_MANDRAKE, 3, 7, 3));
        register(ctx, CELESTIAL_BLOOM, Feature.RANDOM_PATCH,
                randomPatch(ModBlocks.CELESTIAL_BLOOM, 1, 7, 3));
        register(ctx, GHOST_FERN, Feature.RANDOM_PATCH,
                randomPatch(ModBlocks.GHOST_FERN, 1, 7, 3));
    }

    private static void registerDecorativePlants(Registerable<ConfiguredFeature<?, ?>> ctx) {
        register(ctx, BEGONIA, Feature.FLOWER,
                randomPatch(ModBlocks.BEGONIA, 3, 7, 3));
        register(ctx, LAVENDER, Feature.FLOWER,
                randomPatch(ModBlocks.LAVENDER, 15, 7, 5));
        register(ctx, DAHLIA, Feature.FLOWER,
                randomPatch(ModBlocks.DAHLIA, 15, 7, 5));
        register(ctx, LOTUS_FLOWER, Feature.RANDOM_PATCH,
                randomPatch(ModBlocks.LOTUS_FLOWER, 5, 3, 7));
        register(ctx, PALE_MUSHROOM, Feature.RANDOM_PATCH,
                randomPatch(ModBlocks.PALE_MUSHROOM, 2, 2, 3));
        register(ctx, WITCHWEED, Feature.RANDOM_PATCH,
                randomPatch(ModBlocks.WITCHWEED, 20, 10, 5));
        register(ctx, NIGHTSHADE_BUSH, Feature.RANDOM_PATCH,
                randomPatch(ModBlocks.NIGHTSHADE_BUSH, 3, 7, 3));
    }

    private static void registerTrees(Registerable<ConfiguredFeature<?, ?>> ctx) {
        register(ctx, DARK_OAK_COCOON, Feature.TREE,
                new net.minecraft.world.gen.feature.TreeFeatureConfig.Builder(
                        BlockStateProvider.of(Blocks.DARK_OAK_LOG),
                        new DarkOakTrunkPlacer(5, 2, 1),
                        BlockStateProvider.of(Blocks.DARK_OAK_LEAVES),
                        new DarkOakFoliagePlacer(
                                ConstantIntProvider.create(1),
                                ConstantIntProvider.create(0)
                        ),
                        new TwoLayersFeatureSize(1, 0, 1)
                ).decorators(List.of(new CocoonTreeDecorator(0.2f))).build()
        );

        register(ctx, COTTONWOOD, Feature.TREE,
                new net.minecraft.world.gen.feature.TreeFeatureConfig.Builder(
                        BlockStateProvider.of(ModBlocks.COTTONWOOD_LOG),
                        new StraightTrunkPlacer(6, 2, 1),
                        BlockStateProvider.of(ModBlocks.COTTONWOOD_LEAVES),
                        new LargeOakFoliagePlacer(
                                ConstantIntProvider.create(3),
                                ConstantIntProvider.create(1),
                                3
                        ),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).decorators(List.of(new CatkinTreeDecorator())).build()
        );

        register(ctx, COTTONWOOD_COCOON, Feature.TREE,
                new net.minecraft.world.gen.feature.TreeFeatureConfig.Builder(
                        BlockStateProvider.of(ModBlocks.COTTONWOOD_LOG),
                        new StraightTrunkPlacer(6, 2, 1),
                        BlockStateProvider.of(ModBlocks.COTTONWOOD_LEAVES),
                        new LargeOakFoliagePlacer(
                                ConstantIntProvider.create(3),
                                ConstantIntProvider.create(1),
                                3
                        ),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).decorators(List.of(new CatkinTreeDecorator(), new CocoonTreeDecorator(0.2f))).build()
        );

        register(ctx, WILLOW, Feature.TREE,
                new net.minecraft.world.gen.feature.TreeFeatureConfig.Builder(
                        BlockStateProvider.of(ModBlocks.WILLOW_LOG),
                        new StraightTrunkPlacer(4, 2, 1),
                        BlockStateProvider.of(ModBlocks.WILLOW_LEAVES),
                        new CherryFoliagePlacer(
                                ConstantIntProvider.create(4),
                                ConstantIntProvider.create(0),
                                ConstantIntProvider.create(5),
                                0.3F, 0.7F, 0.25F, 0.5F
                        ),
                        new TwoLayersFeatureSize(1, 0, 2)
                ).ignoreVines().build()
        );
    }

    private static RandomPatchFeatureConfig randomPatch(Block block, int tries, int xzSpread, int ySpread) {
        return new RandomPatchFeatureConfig(
                tries, xzSpread, ySpread,
                PlacedFeatures.createEntry(
                        Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig(BlockStateProvider.of(block))
                )
        );
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(
            Registerable<ConfiguredFeature<?, ?>> ctx,
            RegistryKey<ConfiguredFeature<?, ?>> key,
            F feature,
            FC config
    ) {
        ctx.register(key, new ConfiguredFeature<>(feature, config));
    }

    private static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(HexaliaMod.MODID, name));
    }
}