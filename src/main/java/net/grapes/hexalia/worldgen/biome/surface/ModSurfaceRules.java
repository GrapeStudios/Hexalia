package net.grapes.hexalia.worldgen.biome.surface;

import net.grapes.hexalia.worldgen.biome.ModBiomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

public class ModSurfaceRules {
    private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource MUD = makeStateRule(Blocks.MUD);

    public static SurfaceRules.RuleSource makeRules() {
        SurfaceRules.ConditionSource isAtOrAboveWaterLevel = SurfaceRules.waterBlockCheck(-1, 0);

        SurfaceRules.RuleSource grassSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, GRASS_BLOCK), DIRT);
        SurfaceRules.RuleSource grassWithMudPatches = getSurfaceRule(isAtOrAboveWaterLevel);

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(ModBiomes.ENCHANTED_BAYOU),
                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, 0,
                                CaveSurface.FLOOR), grassWithMudPatches)
                ),
                // Default to a grass and dirt surface
                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, 0,
                        CaveSurface.FLOOR), grassSurface)
        );
    }

    private static SurfaceRules.RuleSource getSurfaceRule(SurfaceRules.ConditionSource isAtOrAboveWaterLevel) {
        // Condition for random mud patches using noise threshold
        SurfaceRules.ConditionSource randomMudPatches = SurfaceRules.noiseCondition(Noises.PATCH, -0.1, 0.1);

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(randomMudPatches, MUD),
                SurfaceRules.ifTrue(isAtOrAboveWaterLevel, GRASS_BLOCK), DIRT);
    }


    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}
