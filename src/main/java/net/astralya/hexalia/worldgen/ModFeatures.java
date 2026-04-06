package net.astralya.hexalia.worldgen;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.worldgen.feature.WildCropConfiguration;
import net.astralya.hexalia.worldgen.feature.WildCropFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, HexaliaMod.MODID);

    public static final DeferredHolder<Feature<?>, WildCropFeature> WILD_CROP =
            FEATURES.register("wild_crop", () -> new WildCropFeature(WildCropConfiguration.CODEC));
}