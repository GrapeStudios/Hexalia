package net.grapes.hexalia.util;

import net.fabricmc.loader.api.FabricLoader;

public class ModUtils {
    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
