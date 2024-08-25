package net.grapes.hexalia.world.gen;

import net.grapes.hexalia.world.gen.decorator.ModTreeDecorators;

public class ModWorldGeneration {
    public static void generateModWorldGeneration() {
        ModOreGeneration.generateOres();
        ModVegetationGeneration.generateVegetation();
        ModEntitySpawns.addSpawns();
        ModTreeDecorators.registerTreeDecorators();
    }
}
