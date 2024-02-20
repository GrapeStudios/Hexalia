package net.grapes.hexalia.world;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.block.custom.ChillberryBushBlock;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.List;

public class ModConfiguredFeatures {

    public static final RegistryKey<ConfiguredFeature<?, ?>> SALT_ORE_KEY = registerKey("salt_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> SPIRIT_BLOOM_KEY = registerKey("spirit_bloom");

    public static final RegistryKey<ConfiguredFeature<?, ?>> CHILLBERRY_KEY = registerKey("dreamshroom");

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
    }
    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(HexaliaMod.MOD_ID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
