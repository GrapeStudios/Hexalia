package net.grapes.hexalia.util;

import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeRegistry;
import net.grapes.hexalia.HexaliaMod;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.WoodType;
import net.minecraft.util.Identifier;

public class ModWoodTypes {
    public static final WoodType COTTONWOOD = WoodTypeRegistry.register(new Identifier(HexaliaMod.MOD_ID, "cottonwood"), BlockSetType.OAK);
}
