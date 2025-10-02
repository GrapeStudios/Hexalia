package net.astralya.hexalia.worldgen.gen;

import net.astralya.hexalia.entity.ModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;

public final class ModEntitySpawns {
    private ModEntitySpawns() {}

    public static void addSpawns() {

        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(BiomeKeys.DARK_FOREST),
                SpawnGroup.CREATURE,
                ModEntities.SILK_MOTH,
                20, 1, 1
        );

        SpawnRestriction.register(
                ModEntities.SILK_MOTH,
                SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                AnimalEntity::isValidNaturalSpawn
        );
    }
}
