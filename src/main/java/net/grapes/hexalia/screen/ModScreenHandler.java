package net.grapes.hexalia.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.grapes.hexalia.HexaliaMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandler {

    public static final ScreenHandlerType<SmallCauldronScreenHandler> SMALL_CAULDRON_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(HexaliaMod.MOD_ID, "small_cauldron_screen_handler"),
                    new ExtendedScreenHandlerType<>(SmallCauldronScreenHandler::new));

    public static void registerScreenHandlers() {
        HexaliaMod.LOGGER.info("Registering Screen Handlers for " + HexaliaMod.MOD_ID);
    }
}
