package net.grapes.hexalia.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    public static final ItemGroup HEXALIA = Registry.register(Registries.ITEM_GROUP,
            new Identifier(HexaliaMod.MOD_ID, "hexalia"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.hexalia"))
                    .icon(() -> new ItemStack(ModItems.MANDRAKE)).entries((displayContext, entries) -> {
                        entries.add(ModBlocks.SPIRIT_BLOOM);
                        entries.add(ModItems.SPIRIT_BLOOM_DUST);

                        entries.add(ModBlocks.DREAMSHROOM);
                        entries.add(ModItems.DREAMSHROOM_PASTE);

                        entries.add(ModItems.SIREN_KELP);
                        entries.add(ModItems.SIREN_KELP_PASTE);

                        entries.add(ModItems.CHILLBERRIES);
                        entries.add(ModItems.CHILLBERRY_CUPCAKE);

                        entries.add(ModItems.MANDRAKE);
                        entries.add(ModItems.MANDRAKE_SEEDS);
                        entries.add(ModItems.MANDRAKE_STEW);

                        entries.add(ModItems.SUNFIRE_TOMATO);
                        entries.add(ModItems.SUNFIRE_TOMATO_SEEDS);
                        entries.add(ModItems.SPICY_SANDWICH);

                        entries.add(ModBlocks.SALT_ORE);
                        entries.add(ModItems.SALT);
                        entries.add(ModBlocks.SALT_BLOCK);
                        entries.add(ModItems.SALT_LAMP);

                        entries.add(ModItems.MORTAR_AND_PESTLE);
                        entries.add(ModItems.SMALL_CAULDRON);

                        entries.add(ModItems.RUSTIC_BOTTLE);
                        entries.add(ModItems.BREW_OF_WARDING);
                        entries.add(ModItems.BREW_OF_VIGOR);
                        entries.add(ModItems.BREW_OF_SLIMEY_STEP);

                    }).build());
    public static void registerItemGroups(){
    }
}