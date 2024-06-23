package net.grapes.hexalia.compat.farmersdelight;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class FarmersDelightCompat {

    public static void registerCompat() {
        if (FabricLoader.getInstance().isModLoaded("farmersdelight")) {
            RegistryKey<ItemGroup> farmersDelightGroupKey = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier("farmersdelight", "farmersdelight"));

            ItemGroupEvents.modifyEntriesEvent(farmersDelightGroupKey).register(content -> {
                content.add(new ItemStack(ModItems.WITCH_SALAD));
            });
        }
    }
}
