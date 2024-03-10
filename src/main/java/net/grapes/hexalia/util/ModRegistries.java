package net.grapes.hexalia.util;

import net.grapes.hexalia.item.ModItems;
import net.minecraft.block.ComposterBlock;

public class ModRegistries {
    public static void registerModStuff(){
        registerModCompostable();
    }
    private static void registerModCompostable() {
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(ModItems.SIREN_KELP, 0.6f);
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(ModItems.MANDRAKE, 0.6f);
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(ModItems.MANDRAKE_SEEDS, 0.3f);
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(ModItems.CHILLBERRIES, 0.3f);
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(ModItems.SUNFIRE_TOMATO, 0.6f);
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(ModItems.SUNFIRE_TOMATO_SEEDS, 0.3f);
    }
}