package net.grapes.hexalia.worldgen.biome.custom;

import com.mojang.datafixers.util.Pair;
import net.grapes.hexalia.worldgen.biome.ModBiomes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class EnchantedBayouRegion extends Region {

    public EnchantedBayouRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.addModifiedVanillaOverworldBiomes(mapper, (builder -> {
            builder.replaceBiome(Biomes.SWAMP, ModBiomes.ENCHANTED_BAYOU);
        }));
    }
}
