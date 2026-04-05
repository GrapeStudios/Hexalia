package net.astralya.hexalia.worldgen;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.worldgen.feature.WildCropConfiguration;
import net.astralya.hexalia.worldgen.feature.WildCropFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;

public final class ModFeatures {

    public static final Feature<WildCropConfiguration> WILD_CROP = Registry.register(
            Registries.FEATURE,
            new Identifier(HexaliaMod.MODID, "wild_crop"),
            new WildCropFeature(WildCropConfiguration.CODEC)
    );

    private ModFeatures() {
    }

    public static void register() {
    }
}