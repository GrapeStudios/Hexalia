package net.grapes.hexalia.worldgen.biome;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.worldgen.biome.custom.EnchantedBayouRegion;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;

public class ModTerraBlenderAPI {
    public static void registerRegions(){
        Regions.register(new EnchantedBayouRegion(new ResourceLocation(HexaliaMod.MOD_ID, "overworld"), 5));
    }
}
