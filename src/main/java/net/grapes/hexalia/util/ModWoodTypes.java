package net.grapes.hexalia.util;

import net.grapes.hexalia.HexaliaMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ModWoodTypes {
    public static final WoodType COTTONWOOD = WoodType.register(new WoodType(HexaliaMod.MOD_ID+ ":cottonwood", BlockSetType.OAK));
    public static final WoodType WILLOW = WoodType.register(new WoodType(HexaliaMod.MOD_ID+ ":willow", BlockSetType.OAK));
}
