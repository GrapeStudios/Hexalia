package net.astralya.hexalia.menu;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.astralya.hexalia.Hexalia;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

public final class ModMenuTypes {
  public static final DeferredRegister<MenuType<?>> MENU_TYPES =
      DeferredRegister.create(Hexalia.MOD_ID, Registries.MENU);

  public static final RegistrySupplier<MenuType<NestingBlockMenu>> NESTING_BLOCK =
      MENU_TYPES.register("nesting_block", () -> MenuRegistry.ofExtended(NestingBlockMenu::new));

  private ModMenuTypes() {}

  public static void init() {
    MENU_TYPES.register();
  }
}
