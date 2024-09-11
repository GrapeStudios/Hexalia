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
                        entries.add(ModBlocks.HENBANE);
                        entries.add(ModItems.MANDRAKE_SEEDS);
                        entries.add(ModItems.SUNFIRE_TOMATO_SEEDS);
                        entries.add(ModItems.RABBAGE_SEEDS);

                        entries.add(ModItems.MANDRAKE);
                        entries.add(ModItems.SUNFIRE_TOMATO);
                        entries.add(ModItems.CHILLBERRIES);
                        entries.add(ModItems.RABBAGE);
                        entries.add(ModItems.MANDRAKE_STEW);
                        entries.add(ModItems.SPICY_SANDWICH);
                        entries.add(ModItems.CHILLBERRY_PIE);

                        entries.add(ModBlocks.INFUSED_DIRT);
                        entries.add(ModBlocks.INFUSED_FARMLAND);

                        entries.add(ModItems.PARCHMENT);
                        entries.add(ModItems.DREAMCATCHER);
                        entries.add(ModItems.CANDLE_SKULL);

                        entries.add(ModItems.SALT);
                        entries.add(ModItems.PURIFYING_SALTS);
                        entries.add(ModBlocks.SALT_ORE);
                        entries.add(ModBlocks.SALT_BLOCK);
                        entries.add(ModItems.SALT_LAMP);

                        entries.add(ModItems.MORTAR_AND_PESTLE);
                        entries.add(ModItems.SIREN_KELP_PASTE);
                        entries.add(ModItems.SPIRIT_BLOOM_POWDER);
                        entries.add(ModItems.DREAMSHROOM_PASTE);

                        entries.add(ModItems.STONE_DAGGER);
                        entries.add(ModItems.RESIN);

                        entries.add(ModBlocks.RUSTIC_OVEN);
                        entries.add(ModItems.SMALL_CAULDRON);

                        entries.add(ModBlocks.BREW_SHELF);
                        entries.add(ModItems.RUSTIC_BOTTLE);
                        entries.add(ModItems.BREW_OF_WARDING);
                        entries.add(ModItems.BREW_OF_VIGOR);
                        entries.add(ModItems.BREW_OF_SLIMEY_STEP);
                        entries.add(ModItems.BREW_OF_HOMESTEAD);
                        entries.add(ModItems.BREW_OF_SIPHONING);

                        entries.add(ModItems.HEX_FOCUS);
                        entries.add(ModItems.ANCIENT_SEED);
                        entries.add(ModItems.KELPWEAVE_BLADE);
                        entries.add(ModItems.RITUAL_TABLE);
                        entries.add(ModItems.WISDOM_GEM);

                        entries.add(ModItems.SILK_FIBER);
                        entries.add(ModItems.SILKWORM);

                        entries.add(ModItems.HAT_OF_THE_SWAMP);
                        entries.add(ModItems.CHEST_OF_THE_SWAMP);
                        entries.add(ModItems.PANTS_OF_THE_SWAMP);
                        entries.add(ModItems.BOOTS_OF_THE_SWAMP);

                        entries.add(ModItems.HEXBOOK);
                        entries.add(ModItems.SILK_MOTH_SPAWN_EGG);
                        entries.add(ModBlocks.COTTONWOOD_SAPLING);
                        entries.add(ModBlocks.COTTONWOOD_LEAVES);
                        entries.add(ModBlocks.COTTONWOOD_LOG);
                        entries.add(ModBlocks.COTTONWOOD_WOOD);
                        entries.add(ModBlocks.STRIPPED_COTTONWOOD_LOG);
                        entries.add(ModBlocks.STRIPPED_COTTONWOOD_WOOD);
                        entries.add(ModBlocks.COTTONWOOD_PLANKS);
                        entries.add(ModBlocks.COTTONWOOD_STAIRS);
                        entries.add(ModBlocks.COTTONWOOD_SLAB);
                        entries.add(ModBlocks.COTTONWOOD_FENCE);
                        entries.add(ModBlocks.COTTONWOOD_FENCE_GATE);
                        entries.add(ModBlocks.COTTONWOOD_DOOR);
                        entries.add(ModBlocks.COTTONWOOD_TRAPDOOR);
                        entries.add(ModBlocks.COTTONWOOD_PRESSURE_PLATE);
                        entries.add(ModBlocks.COTTONWOOD_BUTTON);
                        entries.add(ModItems.COTTONWOOD_BOAT);
                        entries.add(ModItems.COTTONWOOD_CHEST_BOAT);
                        entries.add(ModItems.COTTONWOOD_SIGN);
                        entries.add(ModItems.COTTONWOOD_HANGING_SIGN);
                        entries.add(ModBlocks.WILLOW_SAPLING);
                        entries.add(ModBlocks.WILLOW_LEAVES);
                        entries.add(ModBlocks.WILLOW_LOG);
                        entries.add(ModBlocks.WILLOW_WOOD);
                        entries.add(ModBlocks.WILLOW_PLANKS);
                        entries.add(ModBlocks.WILLOW_STAIRS);
                        entries.add(ModBlocks.WILLOW_SLAB);
                        entries.add(ModBlocks.WILLOW_FENCE);
                        entries.add(ModBlocks.WILLOW_FENCE_GATE);
                        entries.add(ModBlocks.WILLOW_DOOR);
                        entries.add(ModBlocks.WILLOW_TRAPDOOR);
                        entries.add(ModBlocks.WILLOW_PRESSURE_PLATE);
                        entries.add(ModBlocks.WILLOW_BUTTON);
                        entries.add(ModItems.WILLOW_BOAT);
                        entries.add(ModItems.WILLOW_CHEST_BOAT);
                        entries.add(ModItems.WILLOW_SIGN);
                        entries.add(ModItems.WILLOW_HANGING_SIGN);

                        entries.add(ModItems.LOTUS_FLOWER);
                        entries.add(ModBlocks.PALE_MUSHROOM);
                        entries.add(ModBlocks.WITCHWEED);
                    }).build());
    public static void registerItemGroups(){
    }
}