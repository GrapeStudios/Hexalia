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
                    .icon(() -> new ItemStack(ModItems.SMALL_CAULDRON)).entries((displayContext, entries) -> {
                        entries.add(ModBlocks.SPIRIT_BLOOM);
                        entries.add(ModBlocks.DREAMSHROOM);
                        entries.add(ModItems.SIREN_KELP);

                        entries.add(ModBlocks.INFUSED_DIRT);
                        entries.add(ModBlocks.INFUSED_FARMLAND);

                        entries.add(ModItems.CHILLBERRIES);
                        entries.add(ModItems.CHILLBERRY_PIE);

                        entries.add(ModItems.MANDRAKE_SEEDS);
                        entries.add(ModItems.MANDRAKE);
                        entries.add(ModItems.MANDRAKE_STEW);

                        entries.add(ModItems.SUNFIRE_TOMATO_SEEDS);
                        entries.add(ModItems.SUNFIRE_TOMATO);
                        entries.add(ModItems.SPICY_SANDWICH);

                        entries.add(ModBlocks.SALT_ORE);
                        entries.add(ModBlocks.SALT_BLOCK);
                        entries.add(ModItems.SALT);
                        entries.add(ModItems.SALT_LAMP);
                        entries.add(ModItems.PURIFYING_SALTS);

                        entries.add(ModItems.MORTAR_AND_PESTLE);
                        entries.add(ModItems.SIREN_KELP_PASTE);
                        entries.add(ModItems.SPIRIT_BLOOM_POWDER);
                        entries.add(ModItems.DREAMSHROOM_PASTE);

                        entries.add(ModItems.STONE_DAGGER);
                        entries.add(ModItems.RESIN);

                        entries.add(ModBlocks.RUSTIC_OVEN);
                        entries.add(ModItems.SMALL_CAULDRON);

                        entries.add(ModItems.RUSTIC_BOTTLE);
                        entries.add(ModItems.BREW_OF_WARDING);
                        entries.add(ModItems.BREW_OF_VIGOR);
                        entries.add(ModItems.BREW_OF_SLIMEY_STEP);
                        entries.add(ModItems.BREW_OF_HOMESTEAD);

                        entries.add(ModItems.ANCIENT_SEED);
                        entries.add(ModItems.KELPWEAVE_BLADE);
                    }).build());
    public static void registerItemGroups(){
    }
}