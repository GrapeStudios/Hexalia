package net.grapes.hexalia.worldgen;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.worldgen.gen.decorator.CatkinTreeDecorator;
import net.grapes.hexalia.worldgen.gen.decorator.CocoonTreeDecorator;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.AboveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.UpwardsBranchingTrunkPlacer;

import java.util.List;
import java.util.Optional;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> SPIRIT_BLOOM_KEY = registerKey("spirit_bloom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DREAMSHROOM_KEY = registerKey("dreamshroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SIREN_KELP_KEY = registerKey("siren_kelp");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHILLBERRY_KEY = registerKey("chillberry");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILD_SUNFIRE_TOMATO_KEY = registerKey("wild_sunfire_tomato");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILD_MANDRAKE_KEY = registerKey("wild_mandrake");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HENBANE_KEY = registerKey("henbane");

    public static final ResourceKey<ConfiguredFeature<?, ?>> COTTONWOOD_KEY = registerKey("cottonwood_key");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILLOW_KEY = registerKey("willow_key");
    public static final ResourceKey<ConfiguredFeature<?, ?>> COTTONWOOD_COCOON_KEY = registerKey("cottonwood_cocoon_key");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DARK_OAK_COCOON_KEY = registerKey("dark_oak_cocoon_key");

    public static final ResourceKey<ConfiguredFeature<?, ?>> LOTUS_FLOWER_KEY = registerKey("lotus_flower_key");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PALE_MUSHROOM_KEY = registerKey("pale_mushroom_key");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WITCHWEED_KEY = registerKey("witchweed_key");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GHOST_FERN_KEY = registerKey("ghost_fern_key");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HEXED_BULRUSH_KEY = registerKey("hexed_bulrush_key");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NIGHTSHADE_BUSH_KEY = registerKey("nightshade_bush_key");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DUCKWEED_KEY = registerKey("duckweed_key");


    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<Block> holdergetter = context.lookup(Registries.BLOCK);

        // Functional Plants
        register(context, SPIRIT_BLOOM_KEY, Feature.FLOWER,
                new RandomPatchConfiguration(2, 3, 1, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.SPIRIT_BLOOM.get())))));

        register(context, DREAMSHROOM_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(2, 3, 1, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.DREAMSHROOM.get())))));

        register(context, SIREN_KELP_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(5, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.SIREN_KELP.get())))));

        register(context, CHILLBERRY_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(15, 8, 4, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.CHILLBERRY_BUSH.get().defaultBlockState()
                                .setValue(SweetBerryBushBlock.AGE, 3))))));

        register(context, WILD_SUNFIRE_TOMATO_KEY, Feature.FLOWER,
                new RandomPatchConfiguration(3, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.WILD_SUNFIRE_TOMATO.get())))));

        register(context, WILD_MANDRAKE_KEY, Feature.FLOWER,
                new RandomPatchConfiguration(3, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.WILD_MANDRAKE.get())))));

        register(context, GHOST_FERN_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(1, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.GHOST_FERN.get())))));

        // Decorative Plants
        register(context, HENBANE_KEY, Feature.FLOWER,
                new RandomPatchConfiguration(3, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.HENBANE.get())))));

        register(context, LOTUS_FLOWER_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(5, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.LOTUS_FLOWER.get())))));

        register(context, PALE_MUSHROOM_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(2, 2, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.PALE_MUSHROOM.get())))));

        register(context, WITCHWEED_KEY, Feature.FLOWER,
                new RandomPatchConfiguration(20, 10, 5, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.WITCHWEED.get())))));

        register(context, HEXED_BULRUSH_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(5, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.HEXED_BULRUSH.get())))));

        register(context, NIGHTSHADE_BUSH_KEY, Feature.FLOWER,
                new RandomPatchConfiguration(3, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.NIGHTSHADE_BUSH.get())))));

        register(context, DUCKWEED_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(5, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.DUCKWEED.get())))));

        // Trees
        register(context, DARK_OAK_COCOON_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.DARK_OAK_LOG),
                new DarkOakTrunkPlacer(5, 2, 1),
                BlockStateProvider.simple(Blocks.DARK_OAK_LEAVES),
                new DarkOakFoliagePlacer(ConstantInt.of(1), ConstantInt.of(0)) {},
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(List.of(new CocoonTreeDecorator()))
                .build());

        register(context, COTTONWOOD_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.COTTONWOOD_LOG.get()),
                new StraightTrunkPlacer(6, 2, 1),
                BlockStateProvider.simple(ModBlocks.COTTONWOOD_LEAVES.get()),
                new FancyFoliagePlacer(ConstantInt.of(3), ConstantInt.of(1), 3) {},
                new TwoLayersFeatureSize(1, 0, 2))
                .decorators(List.of(new CatkinTreeDecorator()))
                .build());

        register(context, COTTONWOOD_COCOON_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.COTTONWOOD_LOG.get()),
                new StraightTrunkPlacer(6, 2, 1),
                BlockStateProvider.simple(ModBlocks.COTTONWOOD_LEAVES.get()),
                new FancyFoliagePlacer(ConstantInt.of(3), ConstantInt.of(1), 3) {},
                new TwoLayersFeatureSize(1, 0, 2))
                .decorators(List.of(new CatkinTreeDecorator(), new CocoonTreeDecorator()))
                .build());

        register(context, WILLOW_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.WILLOW_LOG.get()),
                new UpwardsBranchingTrunkPlacer(2, 1, 5, UniformInt.of(1, 4), 0.5f,
                        UniformInt.of(0, 1), holdergetter.getOrThrow(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)),
                BlockStateProvider.simple(ModBlocks.COTTONWOOD_LEAVES.get()),
                new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 70),
                Optional.of(new MangroveRootPlacer(UniformInt.of(1, 3),
                        BlockStateProvider.simple(ModBlocks.WILLOW_WOOD.get()),
                        Optional.of(new AboveRootPlacement(BlockStateProvider.simple(Blocks.MOSS_CARPET), 0.5F)),
                        new MangroveRootPlacement(holdergetter.getOrThrow(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH),
                                HolderSet.direct(Block::builtInRegistryHolder, Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS),
                                BlockStateProvider.simple(Blocks.MUDDY_MANGROVE_ROOTS), 8, 15, 0.2F))),
                new TwoLayersFeatureSize(2, 0, 2))
                .decorators(List.of(new LeaveVineDecorator(0.125f)))
                .build());
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(HexaliaMod.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
