package net.grapes.hexalia.world.biome.surface;

import net.grapes.hexalia.world.biome.ModBiomes;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import org.jetbrains.annotations.NotNull;

public class ModMaterialRules {
    private static final MaterialRules.MaterialRule DIRT = makeStateRule(Blocks.DIRT);
    private static final MaterialRules.MaterialRule GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final MaterialRules.MaterialRule MUD = makeStateRule(Blocks.MUD);
    private static final MaterialRules.MaterialRule MOSS_BLOCK = makeStateRule(Blocks.MOSS_BLOCK);

    public static MaterialRules.MaterialRule makeRules() {
        MaterialRules.MaterialCondition isAtOrAboveWaterLevel = MaterialRules.water(-1, 0);
        MaterialRules.MaterialRule grassWithMudAndMossPatches = getMaterialRule(isAtOrAboveWaterLevel);

        return MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.biome(ModBiomes.ENCHANTED_BAYOU), grassWithMudAndMossPatches),
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, grassWithMudAndMossPatches)
        );
    }

    private static MaterialRules.@NotNull MaterialRule getMaterialRule(MaterialRules.MaterialCondition isAtOrAboveWaterLevel) {
        MaterialRules.MaterialCondition randomMudPatches = MaterialRules.noiseThreshold(NoiseParametersKeys.PATCH, -0.1, 0.1);
        MaterialRules.MaterialCondition randomMossPatches = MaterialRules.noiseThreshold(NoiseParametersKeys.PATCH, -0.1, 0.1);

        return MaterialRules.sequence(
                MaterialRules.condition(randomMudPatches, MUD),
                MaterialRules.condition(randomMossPatches, MOSS_BLOCK),
                MaterialRules.condition(isAtOrAboveWaterLevel, GRASS_BLOCK),
                DIRT
        );
    }

    private static MaterialRules.MaterialRule makeStateRule(Block block) {
        return MaterialRules.block(block.getDefaultState());
    }
}
