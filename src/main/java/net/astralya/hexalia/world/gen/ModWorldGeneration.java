package net.astralya.hexalia.world.gen;

import net.astralya.hexalia.world.gen.decorator.ModTreeDecorators;

public class ModWorldGeneration {
    public static void generateModWorldGeneration() {
        ModVegetationGeneration.generateVegetation();
        ModEntitySpawns.addSpawns();
        ModTreeDecorators.registerTreeDecorators();
        ModTreeGeneration.generateTrees();
    }
}
