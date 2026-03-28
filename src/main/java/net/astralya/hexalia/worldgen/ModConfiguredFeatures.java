package net.astralya.hexalia.worldgen;

import java.util.List;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.ChillberryBushBlock;
import net.astralya.hexalia.block.custom.SaltsproutBlock;
import net.astralya.hexalia.worldgen.feature.WildCropConfiguration;
import net.astralya.hexalia.worldgen.gen.decorator.CatkinTreeDecorator;
import net.astralya.hexalia.worldgen.gen.decorator.CocoonTreeDecorator;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.CherryFoliagePlacer;
import net.minecraft.world.gen.foliage.DarkOakFoliagePlacer;
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.DarkOakTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

public final class ModConfiguredFeatures {

    public static final RegistryKey<ConfiguredFeature<?, ?>> SPIRIT_BLOOM = registerKey("spirit_bloom");
    public static final RegistryKey<ConfiguredFeature<?, ?>> DREAMSHROOM = registerKey("dreamshroom");
    public static final RegistryKey<ConfiguredFeature<?, ?>> SIREN_KELP = registerKey("siren_kelp");
    public static final RegistryKey<ConfiguredFeature<?, ?>> CHILLBERRY = registerKey("chillberry");
    public static final RegistryKey<ConfiguredFeature<?, ?>> WILD_SUNFIRE_TOMATO = registerKey("wild_sunfire_tomato");
    public static final RegistryKey<ConfiguredFeature<?, ?>> WILD_MANDRAKE = registerKey("wild_mandrake");
    public static final RegistryKey<ConfiguredFeature<?, ?>> BEGONIA = registerKey("begonia");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LAVENDER = registerKey("lavender");
    public static final RegistryKey<ConfiguredFeature<?, ?>> DAHLIA = registerKey("dahlia");
    public static final RegistryKey<ConfiguredFeature<?, ?>> CELESTIAL_BLOOM = registerKey("celestial_bloom");
    public static final RegistryKey<ConfiguredFeature<?, ?>> GHOST_FERN = registerKey("ghost_fern");
    public static final RegistryKey<ConfiguredFeature<?, ?>> SALTSPROUT = registerKey("saltsprout");
    public static final RegistryKey<ConfiguredFeature<?, ?>> COTTONWOOD = registerKey("cottonwood");
    public static final RegistryKey<ConfiguredFeature<?, ?>> COTTONWOOD_COCOON = registerKey("cottonwood_cocoon");
    public static final RegistryKey<ConfiguredFeature<?, ?>> WILLOW = registerKey("willow");
    public static final RegistryKey<ConfiguredFeature<?, ?>> DARK_OAK_PLAIN = registerKey("dark_oak_plain");
    public static final RegistryKey<ConfiguredFeature<?, ?>> DARK_OAK_COCOON = registerKey("dark_oak_cocoon");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LOTUS_FLOWER = registerKey("lotus_flower");
    public static final RegistryKey<ConfiguredFeature<?, ?>> PALE_MUSHROOM = registerKey("pale_mushroom");
    public static final RegistryKey<ConfiguredFeature<?, ?>> WITCHWEED = registerKey("witchweed");
    public static final RegistryKey<ConfiguredFeature<?, ?>> NIGHTSHADE_BUSH = registerKey("nightshade_bush");

    private ModConfiguredFeatures() {
    }

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        registerFunctionalPlants(context);
        registerDecorativePlants(context);
        registerTrees(context);
    }

    private static void registerFunctionalPlants(Registerable<ConfiguredFeature<?, ?>> context) {
        register(context, SPIRIT_BLOOM, ModFeatures.WILD_CROP, wildCropConfig(ModBlocks.SPIRIT_BLOOM, 2, 3, 1, onDirt()));
        register(context, DREAMSHROOM, ModFeatures.WILD_CROP, wildCropConfig(ModBlocks.DREAMSHROOM, 2, 3, 1, onDirtOrMycelium()));
        register(context, SIREN_KELP, ModFeatures.WILD_CROP, WildCropConfiguration.forWater(
                BlockStateProvider.of(ModBlocks.SIREN_KELP),
                2,
                3,
                1,
                onDirtOrSand()
        ));
        register(context, CHILLBERRY, ModFeatures.WILD_CROP, wildCropConfig(
                BlockStateProvider.of(ModBlocks.CHILLBERRY_BUSH.getDefaultState().with(ChillberryBushBlock.AGE, 3)),
                30,
                8,
                4,
                onDirt()
        ));
        register(context, WILD_SUNFIRE_TOMATO, ModFeatures.WILD_CROP, wildCropConfig(ModBlocks.WILD_SUNFIRE_TOMATO, 3, 7, 3, onDirt()));
        register(context, WILD_MANDRAKE, ModFeatures.WILD_CROP, wildCropConfig(ModBlocks.WILD_MANDRAKE, 3, 7, 3, onDirt()));
        register(context, CELESTIAL_BLOOM, ModFeatures.WILD_CROP, wildCropConfig(ModBlocks.CELESTIAL_BLOOM, 1, 7, 3, onDirt()));
        register(context, GHOST_FERN, ModFeatures.WILD_CROP, wildCropConfig(ModBlocks.GHOST_FERN, 1, 7, 3, onDirt()));
        register(context, SALTSPROUT, ModFeatures.WILD_CROP, wildCropConfig(
                BlockStateProvider.of(ModBlocks.SALTSPROUT.getDefaultState().with(SaltsproutBlock.AGE, 2)),
                8,
                7,
                3,
                onSand()
        ));
    }

    private static void registerDecorativePlants(Registerable<ConfiguredFeature<?, ?>> context) {
        register(context, BEGONIA, ModFeatures.WILD_CROP, wildCropConfig(ModBlocks.BEGONIA, 3, 7, 3, onDirt()));
        register(context, LAVENDER, ModFeatures.WILD_CROP, wildCropConfig(ModBlocks.LAVENDER, 15, 7, 5, onDirt()));
        register(context, DAHLIA, ModFeatures.WILD_CROP, wildCropConfig(ModBlocks.DAHLIA, 15, 7, 5, onDirt()));
        register(context, LOTUS_FLOWER, ModFeatures.WILD_CROP, wildCropConfig(
                ModBlocks.LOTUS_FLOWER,
                64,
                6,
                2,
                BlockPredicate.matchingBlocks(List.of(Blocks.WATER))
        ));
        register(context, PALE_MUSHROOM, ModFeatures.WILD_CROP, wildCropConfig(
                ModBlocks.PALE_MUSHROOM,
                2,
                2,
                3,
                BlockPredicate.matchingBlocks(List.of(Blocks.MYCELIUM))
        ));
        register(context, WITCHWEED, ModFeatures.WILD_CROP, wildCropConfig(ModBlocks.WITCHWEED, 20, 10, 5, onDirt()));
        register(context, NIGHTSHADE_BUSH, ModFeatures.WILD_CROP, wildCropConfig(ModBlocks.NIGHTSHADE_BUSH, 3, 7, 3, onDirt()));
    }

    private static void registerTrees(Registerable<ConfiguredFeature<?, ?>> context) {
        register(context, DARK_OAK_PLAIN, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(Blocks.DARK_OAK_LOG),
                new DarkOakTrunkPlacer(5, 2, 1),
                BlockStateProvider.of(Blocks.DARK_OAK_LEAVES),
                new DarkOakFoliagePlacer(ConstantIntProvider.create(1), ConstantIntProvider.create(0)),
                new TwoLayersFeatureSize(1, 0, 1)
        ).build());

        register(context, DARK_OAK_COCOON, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(Blocks.DARK_OAK_LOG),
                new DarkOakTrunkPlacer(5, 2, 1),
                BlockStateProvider.of(Blocks.DARK_OAK_LEAVES),
                new DarkOakFoliagePlacer(ConstantIntProvider.create(1), ConstantIntProvider.create(0)),
                new TwoLayersFeatureSize(1, 0, 1)
        ).decorators(List.of(new CocoonTreeDecorator(0.2f))).build());

        register(context, COTTONWOOD, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.COTTONWOOD_LOG),
                new StraightTrunkPlacer(6, 2, 1),
                BlockStateProvider.of(ModBlocks.COTTONWOOD_LEAVES),
                new LargeOakFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(1), 3),
                new TwoLayersFeatureSize(1, 0, 2)
        ).decorators(List.of(new CatkinTreeDecorator())).build());

        register(context, COTTONWOOD_COCOON, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.COTTONWOOD_LOG),
                new StraightTrunkPlacer(6, 2, 1),
                BlockStateProvider.of(ModBlocks.COTTONWOOD_LEAVES),
                new LargeOakFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(1), 3),
                new TwoLayersFeatureSize(1, 0, 2)
        ).decorators(List.of(new CatkinTreeDecorator(), new CocoonTreeDecorator(0.2f))).build());

        register(context, WILLOW, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.WILLOW_LOG),
                new StraightTrunkPlacer(4, 2, 1),
                BlockStateProvider.of(ModBlocks.WILLOW_LEAVES),
                new CherryFoliagePlacer(
                        ConstantIntProvider.create(4),
                        ConstantIntProvider.create(0),
                        ConstantIntProvider.create(5),
                        0.3F,
                        0.7F,
                        0.25F,
                        0.5F
                ),
                new TwoLayersFeatureSize(1, 0, 2)
        ).ignoreVines().build());
    }

    private static BlockPredicate onDirt() {
        return BlockPredicate.matchingBlockTag(BlockTags.DIRT);
    }

    private static BlockPredicate onSand() {
        return BlockPredicate.matchingBlockTag(BlockTags.SAND);
    }

    private static BlockPredicate onDirtOrMycelium() {
        return BlockPredicate.anyOf(
                BlockPredicate.matchingBlockTag(BlockTags.DIRT),
                BlockPredicate.matchingBlocks(List.of(Blocks.MYCELIUM))
        );
    }

    private static BlockPredicate onDirtOrSand() {
        return BlockPredicate.anyOf(
                BlockPredicate.matchingBlockTag(BlockTags.DIRT),
                BlockPredicate.matchingBlockTag(BlockTags.SAND)
        );
    }

    private static WildCropConfiguration wildCropConfig(Block block, int tries, int xzSpread, int ySpread, BlockPredicate groundPredicate) {
        return WildCropConfiguration.forLand(BlockStateProvider.of(block), tries, xzSpread, ySpread, groundPredicate);
    }

    private static WildCropConfiguration wildCropConfig(BlockStateProvider provider, int tries, int xzSpread, int ySpread, BlockPredicate groundPredicate) {
        return WildCropConfiguration.forLand(provider, tries, xzSpread, ySpread, groundPredicate);
    }

    private static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(HexaliaMod.MODID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(
            Registerable<ConfiguredFeature<?, ?>> context,
            RegistryKey<ConfiguredFeature<?, ?>> key,
            F feature,
            FC config
    ) {
        context.register(key, new ConfiguredFeature<>(feature, config));
    }
}