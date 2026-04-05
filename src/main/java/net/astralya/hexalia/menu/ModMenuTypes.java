package net.astralya.hexalia.menu;

import net.astralya.hexalia.HexaliaMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModMenuTypes {

    public static final ScreenHandlerType<NestingBlockMenu> NESTING_BLOCK_MENU =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    new Identifier(HexaliaMod.MODID, "nesting_block"),
                    new ExtendedScreenHandlerType<>(NestingBlockMenu::new)
            );

    public static void registerScreenHandlers() {
        HexaliaMod.LOGGER.info("Registering screen handlers for {}", HexaliaMod.MODID);
    }
}