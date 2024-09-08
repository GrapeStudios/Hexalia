package net.grapes.hexalia.entity;

import com.terraformersmc.terraform.boat.api.TerraformBoatType;
import com.terraformersmc.terraform.boat.api.TerraformBoatTypeRegistry;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModBoats {
    public static final Identifier COTTONWOOD_BOAT_ID = new Identifier(HexaliaMod.MOD_ID, "cottonwood_boat");
    public static final Identifier COTTONWOOD_CHEST_BOAT_ID = new Identifier(HexaliaMod.MOD_ID, "cottonwood_chest_boat");

    public static final Identifier WILLOW_BOAT_ID = new Identifier(HexaliaMod.MOD_ID, "willow_boat");
    public static final Identifier WILLOW_CHEST_BOAT_ID = new Identifier(HexaliaMod.MOD_ID, "willow_chest_boat");

    public static final RegistryKey<TerraformBoatType> COTTONWOOD_BOAT_KEY = TerraformBoatTypeRegistry.createKey(COTTONWOOD_BOAT_ID);
    public static final RegistryKey<TerraformBoatType> WILLOW_BOAT_KEY = TerraformBoatTypeRegistry.createKey(WILLOW_BOAT_ID);

    public static void registerBoats() {
        TerraformBoatType cottonwoodBoat = new TerraformBoatType.Builder()
                .item(ModItems.COTTONWOOD_BOAT)
                .chestItem(ModItems.COTTONWOOD_CHEST_BOAT)
                .planks(ModBlocks.COTTONWOOD_PLANKS.asItem())
                .build();

        TerraformBoatType willowBoat = new TerraformBoatType.Builder()
                .item(ModItems.WILLOW_BOAT)
                .chestItem(ModItems.WILLOW_CHEST_BOAT)
                .planks(ModBlocks.WILLOW_PLANKS.asItem())
                .build();

        Registry.register(TerraformBoatTypeRegistry.INSTANCE, COTTONWOOD_BOAT_KEY, cottonwoodBoat);
        Registry.register(TerraformBoatTypeRegistry.INSTANCE, WILLOW_BOAT_KEY, willowBoat);
    }
}
