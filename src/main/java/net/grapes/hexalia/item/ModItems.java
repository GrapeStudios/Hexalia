package net.grapes.hexalia.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.item.custom.MortarAndPestleItem;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.StewItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    // Resources
    public static final Item SALT = registerItem("salt",
            new Item(new FabricItemSettings()));
    public static final Item SIREN_KELP = registerItem("siren_kelp",
            new BlockItem(ModBlocks.SIREN_KELP, new FabricItemSettings()));

    public static final Item MANDRAKE = registerItem("mandrake",
            new Item(new FabricItemSettings()));
    public static final Item MANDRAKE_SEEDS = registerItem("mandrake_seeds",
            new AliasedBlockItem(ModBlocks.MANDRAKE_CROP, new FabricItemSettings()));
    public static final Item MANDRAKE_STEW = registerItem("mandrake_stew",
            new StewItem(new FabricItemSettings().food(ModFoodComponents.MANDRAKE_STEW)));

    public static final Item CHILLBERRY = registerItem("chillberry",
            new BlockItem(ModBlocks.CHILLBERRY_BUSH, new FabricItemSettings().food(ModFoodComponents.CHILLBERRY)));
    public static final Item CHILLBERRY_CUPCAKE = registerItem("chillberry_cupcake",
            new Item(new FabricItemSettings().food(ModFoodComponents.CHILLBERRY_CUPCAKE)));

    // Refined Resources
    public static final Item SIREN_KELP_PASTE = registerItem("siren_kelp_paste",
            new Item(new FabricItemSettings()));
    public static final Item SPIRIT_BLOOM_DUST = registerItem("spirit_bloom_dust",
            new Item(new FabricItemSettings()));
    public static final Item DREAMSHROOM_PASTE = registerItem("dreamshroom_paste",
            new Item(new FabricItemSettings()));

    // Tools
    public static final Item MORTAR_AND_PESTLE = registerItem("mortar_and_pestle",
            new MortarAndPestleItem(new FabricItemSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(HexaliaMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        HexaliaMod.LOGGER.info("Registering Mod Items for " + HexaliaMod.MOD_ID);
    }
}