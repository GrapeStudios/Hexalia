package net.astralya.hexalia.fabric.worldgen;

import net.astralya.hexalia.util.ModTags;
import net.astralya.hexalia.worldgen.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.level.levelgen.GenerationStep;

public final class FabricNaturalGeneration {
  private FabricNaturalGeneration() {}

  public static void register() {
    BiomeModifications.addFeature(
        BiomeSelectors.tag(ModTags.Biomes.HAS_SHADED_VEGETATION),
        GenerationStep.Decoration.VEGETAL_DECORATION,
        ModPlacedFeatures.DARK_OAK_COCOON_PLACED);
  }
}
