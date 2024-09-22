package net.grapes.hexalia.world.biome;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.entity.ModEntities;
import net.grapes.hexalia.particle.ModParticles;
import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.OceanPlacedFeatures;

public class ModBiomes {
    public static final RegistryKey<Biome> ENCHANTED_BAYOU = register("enchanted_bayou");

    public static RegistryKey<Biome> register(String name) {
        return RegistryKey.of(RegistryKeys.BIOME, new Identifier(HexaliaMod.MOD_ID, name));
    }

    public static void bootstrap(Registerable<Biome> context) {
        context.register(ENCHANTED_BAYOU, enchantedBayou(context));
    }

    public static Biome enchantedBayou(Registerable<Biome> context) {
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addBatsAndMonsters(spawnBuilder);
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(ModEntities.SILK_MOTH, 2, 1, 1));
        spawnBuilder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FROG, 10, 2, 5));
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SLIME, 100, 4, 4));
        GenerationSettings.LookupBackedBuilder biomeBuilder =
                new GenerationSettings.LookupBackedBuilder(context.getRegistryLookup(RegistryKeys.PLACED_FEATURE),
                        context.getRegistryLookup(RegistryKeys.CONFIGURED_CARVER));
        addBaseFeatures(biomeBuilder);
        DefaultBiomeFeatures.addDefaultOres(biomeBuilder);
        DefaultBiomeFeatures.addGrassAndClayDisks(biomeBuilder);
        DefaultBiomeFeatures.addSwampFeatures(biomeBuilder);
        DefaultBiomeFeatures.addSwampVegetation(biomeBuilder);

        biomeBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_SWAMP);
        MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_SWAMP);

        return new Biome.Builder()
                .precipitation(true)
                .downfall(0.9f)
                .temperature(0.8f)
                .generationSettings(biomeBuilder.build())
                .spawnSettings(spawnBuilder.build())
                .effects((new BiomeEffects.Builder())
                        .waterColor(2051120)
                        .waterFogColor(1124377)
                        .skyColor(13886395)
                        .grassColor(5730350)
                        .foliageColor(4808231)
                        .fogColor(11652229)
                        .moodSound(BiomeMoodSound.CAVE)
                        .particleConfig(new BiomeParticleConfig(ModParticles.MOTE_PARTICLE, 0.05f))
                        .music(musicSound).build())
                .build();
    }

    private static void addBaseFeatures(GenerationSettings.LookupBackedBuilder generationSettings) {
        DefaultBiomeFeatures.addLandCarvers(generationSettings);
        DefaultBiomeFeatures.addAmethystGeodes(generationSettings);
        DefaultBiomeFeatures.addDungeons(generationSettings);
        DefaultBiomeFeatures.addMineables(generationSettings);
        DefaultBiomeFeatures.addSprings(generationSettings);
        DefaultBiomeFeatures.addFrozenTopLayer(generationSettings);
    }
}
