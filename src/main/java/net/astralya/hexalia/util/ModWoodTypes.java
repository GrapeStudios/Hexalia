package net.astralya.hexalia.util;

import net.astralya.hexalia.HexaliaMod;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.WoodType;
import net.minecraft.util.Identifier;

public class ModWoodTypes {
    public static final BlockSetType WILLOW_SET = BlockSetTypeBuilder.copyOf(BlockSetType.MANGROVE).register(Identifier.of(HexaliaMod.MODID, "willow"));
    public static final WoodType WILLOW_WOOD_TYPE = WoodTypeBuilder.copyOf(WoodType.MANGROVE).register(Identifier.of(HexaliaMod.MODID,"willow"), WILLOW_SET);
    
    public static final BlockSetType COTTONWOOD_SET = BlockSetTypeBuilder.copyOf(BlockSetType.MANGROVE).register(Identifier.of(HexaliaMod.MODID, "cottonwood"));
    public static final WoodType COTTONWOOD_WOOD_TYPE = WoodTypeBuilder.copyOf(WoodType.MANGROVE).register(Identifier.of(HexaliaMod.MODID,"cottonwood"), COTTONWOOD_SET);
}
