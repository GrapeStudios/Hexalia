package net.astralya.hexalia.worldgen.gen.decorator;

import net.astralya.hexalia.HexaliaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTreeDecorators {
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS =
            DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, HexaliaMod.MODID);

    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<CocoonTreeDecorator>> COCOON_TREE =
            TREE_DECORATORS.register("cocoon_tree",
                    () -> new TreeDecoratorType<>(CocoonTreeDecorator.CODEC));

    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<CatkinTreeDecorator>> CATKIN =
            TREE_DECORATORS.register("catkin",
                    () -> new TreeDecoratorType<>(CatkinTreeDecorator.CODEC));

    public static void register(IEventBus eventBus) {
        TREE_DECORATORS.register(eventBus);
    }
}
