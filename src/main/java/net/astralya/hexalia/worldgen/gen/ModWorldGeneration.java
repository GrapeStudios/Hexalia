package net.astralya.hexalia.worldgen.gen;


import net.astralya.hexalia.worldgen.gen.decorator.ModTreeDecorators;

public class ModWorldGeneration {
    public static void generateModWorldGeneration() {
        ModTreeDecorators.registerTreeDecorators();
        ModEntitySpawns.addSpawns();
        ModNaturalGeneration.registerNaturalGeneration();
    }
}
