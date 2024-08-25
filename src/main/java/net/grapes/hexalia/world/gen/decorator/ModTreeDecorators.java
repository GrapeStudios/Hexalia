package net.grapes.hexalia.world.gen.decorator;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.grapes.hexalia.HexaliaMod;

public class ModTreeDecorators {
    public static final TreeDecoratorType<CocoonTreeDecorator> COCOON_TREE =
            Registry.register(Registries.TREE_DECORATOR_TYPE,
                    new Identifier(HexaliaMod.MOD_ID, "cocoon_tree"),
                    new TreeDecoratorType<>(CocoonTreeDecorator.CODEC));

    public static void registerTreeDecorators() {
    }
}
