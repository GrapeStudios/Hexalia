package net.astralya.hexalia.worldgen.gen.decorator;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.astralya.hexalia.HexaliaMod;

public class ModTreeDecorators {
    public static final TreeDecoratorType<CocoonTreeDecorator> COCOON_TREE =
            Registry.register(Registries.TREE_DECORATOR_TYPE,
                    Identifier.of(HexaliaMod.MODID, "cocoon_tree"),
                    new TreeDecoratorType<>(CocoonTreeDecorator.CODEC));

    public static final TreeDecoratorType<CatkinTreeDecorator> CATKIN =
            Registry.register(Registries.TREE_DECORATOR_TYPE,
                    Identifier.of(HexaliaMod.MODID, "catkin"),
                    new TreeDecoratorType<>(CatkinTreeDecorator.CODEC));

    public static void registerTreeDecorators() {}
}