package net.grapes.hexalia.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.util.ModUtils;
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
                    .icon(() -> new ItemStack(ModItems.HEX_FOCUS)).entries((displayContext, entries) -> {
                        // Herbs
                        entries.add(ModBlocks.SPIRIT_BLOOM);
                        entries.add(ModBlocks.DREAMSHROOM);
                        entries.add(ModItems.SIREN_KELP);
                        entries.add(ModBlocks.GHOST_FERN);

                        // Crushed Herbs
                        entries.add(ModItems.SIREN_PASTE);
                        entries.add(ModItems.SPIRIT_POWDER);
                        entries.add(ModItems.DREAM_PASTE);
                        entries.add(ModItems.GHOST_POWDER);

                        // Elemental Nodes
                        entries.add(ModItems.FIRE_NODE);
                        entries.add(ModItems.WATER_NODE);
                        entries.add(ModItems.AIR_NODE);
                        entries.add(ModItems.EARTH_NODE);

                        // Enchanted Plants
                        entries.add(ModBlocks.MORPHORA);
                        entries.add(ModBlocks.GRIMSHADE);
                        entries.add(ModBlocks.NAUTILITE);
                        entries.add(ModBlocks.WINDSONG);
                        entries.add(ModBlocks.LUNAR_LILY);

                        // Other  Resources
                        entries.add(ModItems.TREE_RESIN);
                        entries.add(ModItems.MOON_CRYSTAL);
                        entries.add(ModBlocks.MOON_CRYSTAL_BLOCK);
                        entries.add(ModItems.SILK_FIBER);
                        entries.add(ModItems.SILKWORM);

                        // Seeds
                        entries.add(ModItems.MANDRAKE_SEEDS);
                        entries.add(ModItems.SUNFIRE_TOMATO_SEEDS);
                        entries.add(ModItems.RABBAGE_SEEDS);

                        // Crops
                        entries.add(ModItems.MANDRAKE);
                        entries.add(ModItems.SUNFIRE_TOMATO);
                        entries.add(ModItems.CHILLBERRIES);
                        entries.add(ModItems.RABBAGE);
                        entries.add(ModItems.SALTSPROUT);
                        entries.add(ModItems.MOON_BERRIES);

                        // Food
                        entries.add(ModItems.MANDRAKE_STEW);
                        entries.add(ModItems.SPICY_SANDWICH);
                        entries.add(ModItems.CHILLBERRY_PIE);
                        entries.add(ModItems.MOON_BERRY_COOKIE);

                        // Tools
                        entries.add(ModItems.MORTAR_AND_PESTLE);
                        entries.add(ModItems.STONE_DAGGER);
                        entries.add(ModItems.HEX_FOCUS);
                        entries.add(ModItems.SILK_IDOL);
                        entries.add(ModItems.RAIN_IDOL);
                        entries.add(ModItems.CLEAR_IDOL);
                        entries.add(ModItems.STORM_IDOL);

                        // Functional Blocks
                        entries.add(ModBlocks.RUSTIC_OVEN);
                        entries.add(ModItems.SMALL_CAULDRON);
                        entries.add(ModBlocks.SHELF);
                        entries.add(ModItems.RITUAL_TABLE);
                        entries.add(ModBlocks.INFUSED_DIRT);
                        entries.add(ModBlocks.INFUSED_FARMLAND);
                        entries.add(ModBlocks.RITUAL_BRAZIER);
                        entries.add(ModBlocks.CENSER);
                        entries.add(ModBlocks.DREAMCATCHER);

                        // Salt
                        entries.add(ModItems.SALT);
                        entries.add(ModItems.PURIFYING_SALTS);
                        entries.add(ModBlocks.SALT_BLOCK);

                        // Brews
                        entries.add(ModItems.RUSTIC_BOTTLE);
                        entries.add(ModItems.BREW_OF_SPIKESKIN);
                        entries.add(ModItems.BREW_OF_BLOODLUST);
                        entries.add(ModItems.BREW_OF_SLIMEWALKER);
                        entries.add(ModItems.BREW_OF_HOMESTEAD);
                        entries.add(ModItems.BREW_OF_SIPHON);
                        entries.add(ModItems.BREW_OF_DAYBLOOM);
                        entries.add(ModItems.BREW_OF_ARACHNID_GRACE);

                        // Decorative Plants
                        entries.add(ModBlocks.HENBANE);
                        entries.add(ModBlocks.BEGONIA);
                        entries.add(ModBlocks.LAVENDER);
                        entries.add(ModBlocks.DAHLIA);
                        entries.add(ModItems.LOTUS_FLOWER);
                        entries.add(ModBlocks.PALE_MUSHROOM);
                        entries.add(ModBlocks.WITCHWEED);
                        entries.add(ModBlocks.HEXED_BULRUSH);
                        entries.add(ModBlocks.NIGHTSHADE_BUSH);
                        entries.add(ModItems.DUCKWEED);

                        // Decorative Blocks
                        entries.add(ModBlocks.PARCHMENT);
                        entries.add(ModItems.CANDLE_SKULL);
                        entries.add(ModItems.WITHER_CANDLE_SKULL);
                        entries.add(ModItems.SALT_LAMP);

                        // Rare Items
                        entries.add(ModItems.ANCIENT_SEED);
                        entries.add(ModItems.KELPWEAVE_BLADE);
                        entries.add(ModItems.SAGE_PENDANT);

                        // Armor Items
                        entries.add(ModItems.EARPLUGS);
                        entries.add(ModItems.GHOSTVEIL);
                        entries.add(ModItems.BOGGED_BOOTS);

                        // Wood-related Items
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
                        entries.add(ModBlocks.WILLOW_MOSSY_WOOD);
                        entries.add(ModBlocks.STRIPPED_WILLOW_LOG);
                        entries.add(ModBlocks.STRIPPED_WILLOW_WOOD);
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

                        // Spawn Eggs
                        entries.add(ModItems.SILK_MOTH_SPAWN_EGG);

                        // Compat Items
                        if (ModUtils.isModLoaded("patchouli")) {
                            entries.add(ModItems.VERDANT_GRIMOIRE);
                        }

                        if (ModUtils.isModLoaded("farmersdelight")) {
                            entries.add(ModItems.WITCH_SALAD);
                        }


                    }).build());
    public static void registerItemGroups(){
    }
}