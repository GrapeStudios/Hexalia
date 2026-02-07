package net.astralya.hexalia.item;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HexaliaMod.MODID);

    public static final Supplier<CreativeModeTab> HEXALIA_TAB = CREATIVE_MODE_TABS.register("hexalia_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.hexalia"))
            .icon(() -> new ItemStack(ModItems.HEX_FOCUS.get()))
            .displayItems((itemDisplayParameters, output) -> {
                // Herbs
                output.accept(ModBlocks.SPIRIT_BLOOM.get());
                output.accept(ModBlocks.DREAMSHROOM.get());
                output.accept(ModItems.SIREN_KELP.get());
                output.accept(ModBlocks.GHOST_FERN.get());
                output.accept(ModBlocks.CELESTIAL_BLOOM.get());
                output.accept(ModItems.LOTUS_FLOWER.get());
                output.accept(ModItems.LOTUS_BLOSSOM.get());
                output.accept(ModBlocks.WITCHWEED.get());

                // Crushed Items
                output.accept(ModItems.SPIRIT_POWDER.get());
                output.accept(ModItems.DREAM_PASTE.get());
                output.accept(ModItems.SIREN_PASTE.get());
                output.accept(ModItems.GHOST_POWDER.get());
                output.accept(ModItems.FRAGRANT_NECTAR.get());
                output.accept(ModItems.FIREPROOF_WAX.get());

                // Nodes
                output.accept(ModItems.FIRE_NODE.get());
                output.accept(ModItems.WATER_NODE.get());
                output.accept(ModItems.AIR_NODE.get());
                output.accept(ModItems.EARTH_NODE.get());

                // Enchanted Plants
                output.accept(ModBlocks.MORPHORA.get());
                output.accept(ModBlocks.GRIMSHADE.get());
                output.accept(ModItems.NAUTILITE.get());
                output.accept(ModBlocks.WINDSONG.get());
                output.accept(ModBlocks.ASTRYLIS.get());

                // Other Resources
                output.accept(ModItems.TREE_RESIN.get());
                output.accept(ModItems.CELESTIAL_CRYSTAL.get());
                output.accept(ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get());
                output.accept(ModItems.SILK_FIBER.get());
                output.accept(ModItems.SILKWORM.get());

                // Seeds
                output.accept(ModItems.MANDRAKE_SEEDS.get());
                output.accept(ModItems.SUNFIRE_TOMATO_SEEDS.get());
                output.accept(ModItems.RABBAGE_SEEDS.get());

                // Crops
                output.accept(ModItems.MANDRAKE.get());
                output.accept(ModItems.SUNFIRE_TOMATO.get());
                output.accept(ModItems.CHILLBERRIES.get());
                output.accept(ModItems.RABBAGE.get());
                output.accept(ModItems.SALTSPROUT.get());
                output.accept(ModItems.GALEBERRIES.get());

                // Food
                output.accept(ModItems.MANDRAKE_STEW.get());
                output.accept(ModItems.SPICY_SANDWICH.get());
                output.accept(ModItems.CHILLBERRY_PIE.get());
                output.accept(ModItems.GALEBERRIES_COOKIE.get());

                // Tools
                output.accept(ModItems.MORTAR_AND_PESTLE.get());
                output.accept(ModItems.ATHAME.get());
                output.accept(ModItems.HEX_FOCUS.get());
                output.accept(ModItems.SILK_IDOL.get());
                output.accept(ModItems.RAINFALL_IDOL.get());
                output.accept(ModItems.CLARITY_IDOL.get());
                output.accept(ModItems.TEMPEST_IDOL.get());
                output.accept(ModItems.PURITY_IDOL.get());
                output.accept(ModItems.MUTAVIS.get());

                // Functional Blocks
                output.accept(ModBlocks.RUSTIC_OVEN.get());
                output.accept(ModItems.SMALL_CAULDRON.get());
                output.accept(ModBlocks.SHELF.get());
                output.accept(ModBlocks.RITUAL_TABLE.get());
                output.accept(ModBlocks.INFUSED_DIRT.get());
                output.accept(ModBlocks.INFUSED_FARMLAND.get());
                output.accept(ModBlocks.RITUAL_BRAZIER.get());
                output.accept(ModBlocks.CENSER.get());
                output.accept(ModBlocks.DREAMCATCHER.get());
                output.accept(ModBlocks.NESTING_BLOCK.get());

                // Salt
                output.accept(ModItems.SALT.get());
                output.accept(ModBlocks.SALT_BLOCK.get());
                output.accept(ModItems.PURIFYING_SAC.get());
                output.accept(ModItems.FOUL_SAC.get());
                output.accept(ModItems.FROST_SAC.get());

                // Brews
                output.accept(ModItems.RUSTIC_BOTTLE.get());
                output.accept(ModItems.BREW_OF_SPIKESKIN.get());
                output.accept(ModItems.BREW_OF_BLOODLUST.get());
                output.accept(ModItems.BREW_OF_SLIMEWALKER.get());
                output.accept(ModItems.BREW_OF_HOMESTEAD.get());
                output.accept(ModItems.BREW_OF_SIPHON.get());
                output.accept(ModItems.BREW_OF_DAYBLOOM.get());
                output.accept(ModItems.BREW_OF_ARACHNID_GRACE.get());

                // Decorative Plants
                output.accept(ModBlocks.BEGONIA.get());
                output.accept(ModBlocks.LAVENDER.get());
                output.accept(ModBlocks.DAHLIA.get());
                output.accept(ModBlocks.PALE_MUSHROOM.get());
                output.accept(ModBlocks.NIGHTSHADE_BUSH.get());

                // Decorative Blocks
                output.accept(ModItems.CANDLE_SKULL.get());
                output.accept(ModItems.WITHER_CANDLE_SKULL.get());
                output.accept(ModItems.SALT_LAMP.get());

                // Rare Items
                output.accept(ModItems.ANCIENT_SEED.get());
                output.accept(ModItems.KELPWEAVE_BLADE.get());
                output.accept(ModItems.ROOTSHAPER.get());
                output.accept(ModItems.SAGE_PENDANT.get());
                output.accept(ModItems.THORNBOW.get());
                output.accept(ModItems.BRIAR_SICKLE.get());

                // Armor Items
                output.accept(ModItems.EARPLUGS.get());
                output.accept(ModItems.GHOSTVEIL.get());
                output.accept(ModItems.BOGGED_BOOTS.get());

                // Wood Related Items
                output.accept(ModBlocks.COTTONWOOD_SAPLING.get());
                output.accept(ModBlocks.COTTONWOOD_LEAVES.get());
                output.accept(ModBlocks.COTTONWOOD_LOG.get());
                output.accept(ModBlocks.COTTONWOOD_WOOD.get());
                output.accept(ModBlocks.STRIPPED_COTTONWOOD_LOG.get());
                output.accept(ModBlocks.STRIPPED_COTTONWOOD_WOOD.get());
                output.accept(ModBlocks.COTTONWOOD_PLANKS.get());
                output.accept(ModBlocks.COTTONWOOD_STAIRS.get());
                output.accept(ModBlocks.COTTONWOOD_SLAB.get());
                output.accept(ModBlocks.COTTONWOOD_FENCE.get());
                output.accept(ModBlocks.COTTONWOOD_FENCE_GATE.get());
                output.accept(ModBlocks.COTTONWOOD_DOOR.get());
                output.accept(ModBlocks.COTTONWOOD_TRAPDOOR.get());
                output.accept(ModBlocks.COTTONWOOD_PRESSURE_PLATE.get());
                output.accept(ModBlocks.COTTONWOOD_BUTTON.get());
                output.accept(ModItems.COTTONWOOD_SIGN.get());
                output.accept(ModItems.COTTONWOOD_HANGING_SIGN.get());
                output.accept(ModItems.COTTONWOOD_BOAT.get());
                output.accept(ModItems.COTTONWOOD_CHEST_BOAT.get());

                output.accept(ModBlocks.WILLOW_SAPLING.get());
                output.accept(ModBlocks.WILLOW_LEAVES.get());
                output.accept(ModBlocks.WILLOW_LOG.get());
                output.accept(ModBlocks.WILLOW_WOOD.get());
                output.accept(ModBlocks.STRIPPED_WILLOW_LOG.get());
                output.accept(ModBlocks.STRIPPED_WILLOW_WOOD.get());
                output.accept(ModBlocks.WILLOW_PLANKS.get());
                output.accept(ModBlocks.WILLOW_STAIRS.get());
                output.accept(ModBlocks.WILLOW_SLAB.get());
                output.accept(ModBlocks.WILLOW_FENCE.get());
                output.accept(ModBlocks.WILLOW_FENCE_GATE.get());
                output.accept(ModBlocks.WILLOW_DOOR.get());
                output.accept(ModBlocks.WILLOW_TRAPDOOR.get());
                output.accept(ModBlocks.WILLOW_PRESSURE_PLATE.get());
                output.accept(ModBlocks.WILLOW_BUTTON.get());
                output.accept(ModItems.WILLOW_SIGN.get());
                output.accept(ModItems.WILLOW_HANGING_SIGN.get());
                output.accept(ModItems.WILLOW_BOAT.get());
                output.accept(ModItems.WILLOW_CHEST_BOAT.get());

                output.accept(ModItems.SILK_MOTH_SPAWN_EGG.get());
            })
            .build());

    public static void register (IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
