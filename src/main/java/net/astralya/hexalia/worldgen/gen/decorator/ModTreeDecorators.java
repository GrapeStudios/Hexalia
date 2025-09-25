package net.astralya.hexalia.worldgen.gen.decorator;

import net.astralya.hexalia.HexaliaMod;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModTreeDecorators {

    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS =
            DeferredRegister.create(ForgeRegistries.TREE_DECORATOR_TYPES, HexaliaMod.MODID);

    public static final RegistryObject<TreeDecoratorType<CatkinTreeDecorator>> CATKIN =
            TREE_DECORATORS.register("catkin", () -> new TreeDecoratorType<>(CatkinTreeDecorator.CODEC));

    public static final RegistryObject<TreeDecoratorType<CocoonTreeDecorator>> COCOON =
            TREE_DECORATORS.register("cocoon", () -> new TreeDecoratorType<>(CocoonTreeDecorator.CODEC));

    public static void register (IEventBus eventBus) {
        TREE_DECORATORS.register(eventBus);
    }
}
