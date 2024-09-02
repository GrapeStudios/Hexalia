package net.grapes.hexalia.world;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.block.custom.ChillberryBushBlock;
import net.grapes.hexalia.world.gen.decorator.CatkinTreeDecorator;
import net.grapes.hexalia.world.gen.decorator.CocoonTreeDecorator;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.DarkOakTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

import java.util.List;

public class ModConfiguredFeatures {

    public static final RegistryKey<ConfiguredFeature<?, ?>> SALT_ORE_KEY = registerKey("salt_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> SPIRIT_BLOOM_KEY = registerKey("spirit_bloom");
    public static final RegistryKey<ConfiguredFeature<?, ?>> CHILLBERRY_KEY = registerKey("chillberry");
    public static final RegistryKey<ConfiguredFeature<?, ?>> DREAMSHROOM_KEY = registerKey("dreamshroom");
    public static final RegistryKey<ConfiguredFeature<?, ?>> SIREN_KELP_KEY = registerKey("siren_kelp");
    public static final RegistryKey<ConfiguredFeature<?, ?>> WILD_SUNFIRE_TOMATO_KEY = registerKey("wild_sunfire_tomato");
    public static final RegistryKey<ConfiguredFeature<?, ?>> WILD_MANDRAKE_KEY = registerKey("wild_mandrake");
    public static final RegistryKey<ConfiguredFeature<?, ?>> HENBANE = registerKey("henbane");
    public static final RegistryKey<ConfiguredFeature<?, ?>> DARK_OAK_COCOON_KEY = registerKey("dark_oak_cocoon_tree");
    public static final RegistryKey<ConfiguredFeature<?, ?>> COTTONWOOD_KEY = registerKey("cottonwood");
    public static final RegistryKey<ConfiguredFeature<?, ?>> COTTONWOOD_COCOON_KEY = registerKey("cottonwood_cocoon");


    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);

        List<OreFeatureConfig.Target> overworldSaltOres =
                List.of(OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.SALT_ORE.getDefaultState()));

        register(context, SALT_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldSaltOres, 12));

        register(context, SPIRIT_BLOOM_KEY, Feature.FLOWER, ConfiguredFeatures.createRandomPatchFeatureConfig(3,
                PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig(BlockStateProvider.of(ModBlocks.SPIRIT_BLOOM)))));

        register(context, CHILLBERRY_KEY, Feature.RANDOM_PATCH, ConfiguredFeatures.createRandomPatchFeatureConfig(10,
                PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig(BlockStateProvider.of(ModBlocks.CHILLBERRY_BUSH.getDefaultState().with(ChillberryBushBlock.AGE, 3))))));

        register(context, DREAMSHROOM_KEY, Feature.RANDOM_PATCH, ConfiguredFeatures.createRandomPatchFeatureConfig(10,
                PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig(BlockStateProvider.of(ModBlocks.DREAMSHROOM)))));

        register(context, WILD_SUNFIRE_TOMATO_KEY, Feature.FLOWER, ConfiguredFeatures.createRandomPatchFeatureConfig(3,
                PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig(BlockStateProvider.of(ModBlocks.WILD_SUNFIRE_TOMATO)))));

        register(context, SIREN_KELP_KEY, Feature.RANDOM_PATCH, ConfiguredFeatures.createRandomPatchFeatureConfig(12,
                PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig(BlockStateProvider.of(ModBlocks.SIREN_KELP)))));

        register(context, WILD_MANDRAKE_KEY, Feature.FLOWER, ConfiguredFeatures.createRandomPatchFeatureConfig(3,
                PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig(BlockStateProvider.of(ModBlocks.WILD_MANDRAKE)))));

        register(context, HENBANE, Feature.FLOWER, ConfiguredFeatures.createRandomPatchFeatureConfig(3,
                PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig(BlockStateProvider.of(ModBlocks.HENBANE)))));

        register(context, DARK_OAK_COCOON_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(Blocks.DARK_OAK_LOG),
                new DarkOakTrunkPlacer(5, 2, 1),
                BlockStateProvider.of(Blocks.DARK_OAK_LEAVES),
                new DarkOakFoliagePlacer(ConstantIntProvider.create(1), ConstantIntProvider.create(0)),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(List.of(new CocoonTreeDecorator()))
                .build());

        register(context, COTTONWOOD_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.COTTONWOOD_LOG),
                new StraightTrunkPlacer(6, 2, 1),
                BlockStateProvider.of(ModBlocks.COTTONWOOD_LEAVES),
                new LargeOakFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(1), 3) {},
                new TwoLayersFeatureSize(1, 0, 2))
                .decorators(List.of(new CatkinTreeDecorator()))
                .build());

        register(context, COTTONWOOD_COCOON_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.COTTONWOOD_LOG),
                new StraightTrunkPlacer(6, 2, 1),
                BlockStateProvider.of(ModBlocks.COTTONWOOD_LEAVES),
                new LargeOakFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(1), 3) {},
                new TwoLayersFeatureSize(1, 0, 2))
                .decorators(List.of(new CatkinTreeDecorator(), new CocoonTreeDecorator()))
                .build());
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(HexaliaMod.MOD_ID, name));

    }
    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
