package net.astralya.hexalia.item;

import net.astralya.hexalia.util.ModUtil;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    public static final ItemGroup HEXALIA = Registry.register(Registries.ITEM_GROUP,
            new Identifier(HexaliaMod.MODID, "hexalia"),
            FabricItemGroup.builder().displayName(Text.translatable("itemGroup.hexalia"))
                    .icon(() -> new ItemStack(ModItems.HEX_FOCUS)).entries((displayContext, entries) -> {
                        // Herbs
                        entries.add(ModBlocks.SPIRIT_BLOOM);
                        entries.add(ModBlocks.DREAMSHROOM);
                        entries.add(ModItems.SIREN_KELP);
                        entries.add(ModBlocks.GHOST_FERN);
                        entries.add(ModBlocks.CELESTIAL_BLOOM);
                        entries.add(ModItems.LOTUS_FLOWER);
                        entries.add(ModItems.LOTUS_BLOSSOM);
                        entries.add(ModBlocks.WITCHWEED);

                        // Crushed Herbs
                        entries.add(ModItems.SPIRIT_POWDER);
                        entries.add(ModItems.DREAM_PASTE);
                        entries.add(ModItems.SIREN_PASTE);
                        entries.add(ModItems.GHOST_POWDER);

                        // Nodes
                        entries.add(ModItems.FIRE_NODE);
                        entries.add(ModItems.WATER_NODE);
                        entries.add(ModItems.AIR_NODE);
                        entries.add(ModItems.EARTH_NODE);

                        // Enchanted Plants
                        entries.add(ModBlocks.MORPHORA);
                        entries.add(ModBlocks.GRIMSHADE);
                        entries.add(ModItems.NAUTILITE);
                        entries.add(ModBlocks.WINDSONG);
                        entries.add(ModBlocks.ASTRYLIS);

                        // Other Resources
                        entries.add(ModItems.TREE_RESIN);
                        entries.add(ModItems.CELESTIAL_CRYSTAL);
                        entries.add(ModBlocks.CELESTIAL_CRYSTAL_BLOCK);
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
                        entries.add(ModItems.GALEBERRIES);

                        // Food
                        entries.add(ModItems.MANDRAKE_STEW);
                        entries.add(ModItems.SPICY_SANDWICH);
                        entries.add(ModItems.CHILLBERRY_PIE);
                        entries.add(ModItems.GALEBERRIES_COOKIE);

                        // Tools
                        entries.add(ModItems.MORTAR_AND_PESTLE);
                        entries.add(ModItems.ATHAME);
                        entries.add(ModItems.HEX_FOCUS);
                        entries.add(ModItems.SILK_IDOL);
                        entries.add(ModItems.RAINFALL_IDOL);
                        entries.add(ModItems.CLARITY_IDOL);
                        entries.add(ModItems.TEMPEST_IDOL);
                        entries.add(ModItems.PURITY_IDOL);
                        entries.add(ModItems.MUTAVIS);

                        // Functional Blocks
                        entries.add(ModBlocks.RUSTIC_OVEN);
                        entries.add(ModItems.SMALL_CAULDRON);
                        entries.add(ModBlocks.SHELF);
                        entries.add(ModBlocks.RITUAL_TABLE);
                        entries.add(ModBlocks.INFUSED_DIRT);
                        entries.add(ModBlocks.INFUSED_FARMLAND);
                        entries.add(ModBlocks.RITUAL_BRAZIER);
                        entries.add(ModBlocks.CENSER);
                        entries.add(ModBlocks.DREAMCATCHER);

                        // Salt
                        entries.add(ModItems.SALT);
                        entries.add(ModBlocks.SALT_BLOCK);
                        entries.add(ModItems.PURIFYING_SAC);
                        entries.add(ModItems.FOUL_SAC);
                        entries.add(ModItems.FROST_SAC);

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
                        entries.add(ModBlocks.BEGONIA);
                        entries.add(ModBlocks.LAVENDER);
                        entries.add(ModBlocks.DAHLIA);
                        entries.add(ModBlocks.PALE_MUSHROOM);
                        entries.add(ModBlocks.NIGHTSHADE_BUSH);

                        // Decorative Blocks
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

                        // Wood Related Items
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
                        entries.add(ModItems.COTTONWOOD_SIGN);
                        entries.add(ModItems.COTTONWOOD_HANGING_SIGN);
                        entries.add(ModItems.COTTONWOOD_BOAT);
                        entries.add(ModItems.COTTONWOOD_CHEST_BOAT);

                        entries.add(ModBlocks.WILLOW_SAPLING);
                        entries.add(ModBlocks.WILLOW_LEAVES);
                        entries.add(ModBlocks.WILLOW_LOG);
                        entries.add(ModBlocks.WILLOW_WOOD);
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
                        entries.add(ModItems.WILLOW_SIGN);
                        entries.add(ModItems.WILLOW_HANGING_SIGN);
                        entries.add(ModItems.WILLOW_BOAT);
                        entries.add(ModItems.WILLOW_CHEST_BOAT);

                        entries.add(ModItems.SILK_MOTH_SPAWN_EGG);

                        // Compat Items
                        if (ModUtil.isModLoaded("patchouli")) {
                            entries.add(ModItems.VERDANT_GRIMOIRE);
                        }


                    }).build());
    public static void registerItemGroups(){
    }
}