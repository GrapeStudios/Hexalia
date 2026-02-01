package net.astralya.hexalia.screen;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.screen.custom.NestingBlockMenu;
import net.astralya.hexalia.screen.custom.SmallCauldronMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, HexaliaMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<SmallCauldronMenu>> SMALL_CAULDRON_MENU =
            registerMenuType("small_cauldron_menu", SmallCauldronMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<NestingBlockMenu>> NESTING_BLOCK_MENU =
            registerMenuType("nesting_block_menu", NestingBlockMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>,
            MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
