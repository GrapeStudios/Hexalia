package net.grapes.hexalia.world.gen.feature;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.ProbabilityConfig;

public class HexaliaFeatures {
    public static final SirenKelpFeature SIREN_KELP = new SirenKelpFeature(ProbabilityConfig.CODEC);

    public static void registerFeature() {
        Registry.register(Registries.FEATURE, ("siren_kelp"), SIREN_KELP);
    }
}