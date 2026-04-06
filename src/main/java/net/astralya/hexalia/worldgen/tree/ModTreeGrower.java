package net.astralya.hexalia.worldgen.tree;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.worldgen.ModConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class ModTreeGrower {

    public static final TreeGrower COTTONWOOD = new TreeGrower(
            HexaliaMod.MODID + ":cottonwood",
            Optional.empty(),
            Optional.empty(),
            Optional.of(ModConfiguredFeatures.COTTONWOOD)
    );

    public static final TreeGrower WILLOW = new TreeGrower(
            HexaliaMod.MODID + ":willow",
            Optional.empty(),
            Optional.empty(),
            Optional.of(ModConfiguredFeatures.WILLOW)
    );
}