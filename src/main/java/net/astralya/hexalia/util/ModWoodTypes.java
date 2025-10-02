package net.astralya.hexalia.util;

import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeRegistry;
import net.astralya.hexalia.HexaliaMod;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.WoodType;
import net.minecraft.util.Identifier;

public class ModWoodTypes {
    public static final WoodType COTTONWOOD = WoodTypeRegistry.register(new Identifier(HexaliaMod.MODID, "cottonwood"), BlockSetType.OAK);
    public static final WoodType WILLOW = WoodTypeRegistry.register(new Identifier(HexaliaMod.MODID, "willow"), BlockSetType.OAK);
}
