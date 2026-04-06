package net.astralya.hexalia.menu;

import net.astralya.hexalia.HexaliaMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModMenuTypes {

    public static final ScreenHandlerType<NestingBlockMenu> NESTING_BLOCK_MENU =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(HexaliaMod.MODID, "nesting_block"),
                    new ExtendedScreenHandlerType<>(NestingBlockMenu::new, BlockPos.PACKET_CODEC));

    private static <T extends ScreenHandler, D> ScreenHandlerType<T> register(String name, ExtendedScreenHandlerType.ExtendedFactory<T, D> factory,
                                                                              PacketCodec<? super RegistryByteBuf, D> codec) {
        return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(HexaliaMod.MODID, name), new ExtendedScreenHandlerType<>(factory, codec));
    }

    public static void register() {

    }
}