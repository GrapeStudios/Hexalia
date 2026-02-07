package net.astralya.hexalia.worldgen;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.ChillberryBushBlock;
import net.astralya.hexalia.block.custom.SaltsproutBlock;
import net.astralya.hexalia.worldgen.gen.decorator.CatkinTreeDecorator;
import net.astralya.hexalia.worldgen.gen.decorator.CocoonTreeDecorator;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.function.Supplier;

public class ModConfiguredFeatures {

    // Keys
    public static final ResourceKey<ConfiguredFeature<?, ?>> SPIRIT_BLOOM = registerKey("spirit_bloom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DREAMSHROOM = registerKey("dreamshroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SIREN_KELP = registerKey("siren_kelp");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHILLBERRY = registerKey("chillberry");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILD_SUNFIRE_TOMATO = registerKey("wild_sunfire_tomato");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILD_MANDRAKE = registerKey("wild_mandrake");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BEGONIA = registerKey("begonia");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LAVENDER = registerKey("lavender");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DAHLIA = registerKey("dahlia");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CELESTIAL_BLOOM = registerKey("celestial_bloom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GHOST_FERN = registerKey("ghost_fern");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SALTSPROUT = registerKey("saltsprout");

    public static final ResourceKey<ConfiguredFeature<?, ?>> COTTONWOOD = registerKey("cottonwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILLOW = registerKey("willow");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DARK_OAK_COCOON = registerKey("dark_oak_cocoon");

    public static final ResourceKey<ConfiguredFeature<?, ?>> LOTUS_FLOWER = registerKey("lotus_flower");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PALE_MUSHROOM = registerKey("pale_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WITCHWEED = registerKey("witchweed");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NIGHTSHADE_BUSH = registerKey("nightshade_bush");


    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);

        registerFunctionalPlants(context);
        registerDecorativePlants(context);
        registerTrees(context);
    }

    private static void registerFunctionalPlants(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(context, SPIRIT_BLOOM, Feature.FLOWER, patchConfig(ModBlocks.SPIRIT_BLOOM.get(), 2, 3, 1));
        register(context, DREAMSHROOM, Feature.RANDOM_PATCH, patchConfig(ModBlocks.DREAMSHROOM.get(), 2, 3, 1));
        register(context, SIREN_KELP, Feature.RANDOM_PATCH, patchConfig(ModBlocks.SIREN_KELP.get(), 2, 3, 1));
        register(context, CHILLBERRY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(30, 8, 4,
                        simple(ModBlocks.CHILLBERRY_BUSH.get().defaultBlockState().setValue(ChillberryBushBlock.AGE, 3))));
        register(context, WILD_SUNFIRE_TOMATO, Feature.FLOWER, patchConfig(ModBlocks.WILD_SUNFIRE_TOMATO.get(), 3, 7, 3));
        register(context, WILD_MANDRAKE, Feature.FLOWER, patchConfig(ModBlocks.WILD_MANDRAKE.get(), 3, 7, 3));
        register(context, CELESTIAL_BLOOM, Feature.RANDOM_PATCH, patchConfig(ModBlocks.CELESTIAL_BLOOM.get(), 1, 7, 3));
        register(context, GHOST_FERN, Feature.RANDOM_PATCH, patchConfig(ModBlocks.GHOST_FERN.get(), 1, 7, 3));
        register(context, SALTSPROUT, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(8, 7, 3,
                        simple(ModBlocks.SALTSPROUT.get().defaultBlockState().setValue(SaltsproutBlock.AGE, 2))));
    }

    private static void registerDecorativePlants(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(context, BEGONIA, Feature.FLOWER, patchConfig(ModBlocks.BEGONIA.get(), 3, 7, 3));
        register(context, LAVENDER, Feature.FLOWER, patchConfig(ModBlocks.LAVENDER.get(), 15, 7, 5));
        register(context, DAHLIA, Feature.FLOWER, patchConfig(ModBlocks.DAHLIA.get(), 15, 7, 5));
        register(context, LOTUS_FLOWER, Feature.RANDOM_PATCH, patchConfig(ModBlocks.LOTUS_FLOWER.get(), 5, 3, 7));
        register(context, PALE_MUSHROOM, Feature.RANDOM_PATCH, patchConfig(ModBlocks.PALE_MUSHROOM.get(), 2, 2, 3));
        register(context, WITCHWEED, Feature.RANDOM_PATCH, patchConfig(ModBlocks.WITCHWEED.get(), 20, 10, 5));
        register(context, NIGHTSHADE_BUSH, Feature.RANDOM_PATCH, patchConfig(ModBlocks.NIGHTSHADE_BUSH.get(), 3, 7, 3));
    }

    private static void registerTrees(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(context, DARK_OAK_COCOON, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.DARK_OAK_LOG),
                new DarkOakTrunkPlacer(5, 2, 1),
                BlockStateProvider.simple(Blocks.DARK_OAK_LEAVES),
                new DarkOakFoliagePlacer(ConstantInt.of(1), ConstantInt.of(0)) {},
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(List.of(new CocoonTreeDecorator(0.2f))).build());

        register(context, COTTONWOOD, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.COTTONWOOD_LOG.get()),
                new StraightTrunkPlacer(6, 2, 1),
                BlockStateProvider.simple(ModBlocks.COTTONWOOD_LEAVES.get()),
                new FancyFoliagePlacer(ConstantInt.of(3), ConstantInt.of(1), 3) {},
                new TwoLayersFeatureSize(1, 0, 2))
                .decorators(List.of(new CatkinTreeDecorator()))
                .build());

        // Willow Tree with hanging-style foliage
        register(context, WILLOW, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.WILLOW_LOG.get()),
                new StraightTrunkPlacer(4, 2, 1),
                BlockStateProvider.simple(ModBlocks.WILLOW_LEAVES.get()),
                new CherryFoliagePlacer(
                        ConstantInt.of(4), ConstantInt.of(0), ConstantInt.of(5),
                        0.3F, 0.7F, 0.25F, 0.5F),
                new TwoLayersFeatureSize(1, 0, 2))
                .ignoreVines()
                .build());
    }

    // Utility
    private static RandomPatchConfiguration patchConfig(Block block, int tries, int xzSpread, int ySpread) {
        return new RandomPatchConfiguration(tries, xzSpread, ySpread,
                PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(block))));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstrapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key,
            F feature,
            FC config) {
        context.register(key, new ConfiguredFeature<>(feature, config));
    }

    private static <C extends FeatureConfiguration> Supplier<C> simple(C config) {
        return () -> config;
    }

    private static Supplier<SimpleBlockConfiguration> simple(BlockStateProvider provider) {
        return () -> new SimpleBlockConfiguration(provider);
    }


    private static Supplier<SimpleBlockConfiguration> simple(Block block) {
        return simple(BlockStateProvider.simple(block));
    }

    private static Holder<PlacedFeature> simple(BlockState state) {
        return PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(state)));
    }
}
