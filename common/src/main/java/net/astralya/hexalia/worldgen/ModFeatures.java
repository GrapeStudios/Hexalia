package net.astralya.hexalia.worldgen;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.hexalia.Hexalia;
import net.astralya.hexalia.worldgen.feature.WildCropConfiguration;
import net.astralya.hexalia.worldgen.feature.WildCropFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;

public final class ModFeatures {
  public static final DeferredRegister<Feature<?>> FEATURES =
      DeferredRegister.create(Hexalia.MOD_ID, Registries.FEATURE);

  public static final RegistrySupplier<WildCropFeature> WILD_CROP =
      FEATURES.register("wild_crop", () -> new WildCropFeature(WildCropConfiguration.CODEC));

  private ModFeatures() {}

  public static void init() {
    FEATURES.register();
  }
}
