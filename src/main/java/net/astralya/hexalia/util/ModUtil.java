package net.astralya.hexalia.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ModUtil {

    public static void spawnItemEntity(World world, ItemStack stack, double x, double y, double z, double xMotion, double yMotion, double zMotion) {
        ItemEntity entity = new ItemEntity(world, x, y, z, stack);
        entity.setVelocity(xMotion, yMotion, zMotion);
        world.spawnEntity(entity);
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

}
