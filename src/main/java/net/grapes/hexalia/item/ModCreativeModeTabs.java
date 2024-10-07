package net.grapes.hexalia.item;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HexaliaMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> HEXALIA_TAB = CREATIVE_MODE_TABS.register("hexalia_tab",
            () -> CreativeModeTab.builder().icon(() ->new ItemStack(ModItems.HEX_FOCUS.get()))
                    .title(Component.translatable("creativetab.hexalia_tab")).displayItems((itemDisplayParameters, output) ->{
                        // Magical Plants
                        output.accept(ModBlocks.SPIRIT_BLOOM.get());
                        output.accept(ModBlocks.DREAMSHROOM.get());
                        output.accept(ModItems.SIREN_KELP.get());
                        output.accept(ModBlocks.GHOST_FERN.get());

                        // Crushed Plants (Powder and Paste)
                        output.accept(ModItems.SPIRIT_BLOOM_POWDER.get());
                        output.accept(ModItems.DREAMSHROOM_PASTE.get());
                        output.accept(ModItems.SIREN_KELP_PASTE.get());
                        output.accept(ModItems.GHOST_FERN_POWDER.get());

                        // Other Resources
                        output.accept(ModItems.RESIN.get());
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

                        // Food
                        output.accept(ModItems.MANDRAKE_STEW.get());
                        output.accept(ModItems.SPICY_SANDWICH.get());
                        output.accept(ModItems.CHILLBERRY_PIE.get());

                        // Tools
                        output.accept(ModItems.MORTAR_AND_PESTLE.get());
                        output.accept(ModItems.STONE_DAGGER.get());
                        output.accept(ModItems.HEX_FOCUS.get());

                        // Functional bLOCKS
                        output.accept(ModBlocks.RUSTIC_OVEN.get());
                        output.accept(ModItems.SMALL_CAULDRON.get());
                        output.accept(ModBlocks.BREW_SHELF.get());
                        output.accept(ModItems.RITUAL_TABLE.get());
                        output.accept(ModBlocks.INFUSED_DIRT.get());
                        output.accept(ModBlocks.INFUSED_FARMLAND.get());

                        // Salt
                        output.accept(ModItems.SALT.get());
                        output.accept(ModItems.PURIFYING_SALTS.get());
                        output.accept(ModBlocks.SALT_BLOCK.get());

                        // Brews
                        output.accept(ModItems.RUSTIC_BOTTLE.get());
                        output.accept(ModItems.BREW_OF_SPIKESKIN.get());
                        output.accept(ModItems.BREW_OF_BLOODLUST.get());
                        output.accept(ModItems.BREW_OF_SLIMEWALKER.get());
                        output.accept(ModItems.BREW_OF_HOMESTEAD.get());
                        output.accept(ModItems.BREW_OF_SIPHON.get());

                        // Decorative Plants
                        output.accept(ModBlocks.HENBANE.get());
                        output.accept(ModItems.LOTUS_FLOWER.get());
                        output.accept(ModBlocks.PALE_MUSHROOM.get());
                        output.accept(ModBlocks.WITCHWEED.get());
                        output.accept(ModBlocks.HEXED_BULRUSH.get());
                        output.accept(ModBlocks.NIGHTSHADE_BUSH.get());
                        output.accept(ModItems.DUCKWEED.get());

                        // Decorative Blocks
                        output.accept(ModBlocks.PARCHMENT.get());
                        output.accept(ModBlocks.DREAMCATCHER.get());
                        output.accept(ModItems.CANDLE_SKULL.get());
                        output.accept(ModItems.SALT_LAMP.get());

                        // Rare Items
                        output.accept(ModItems.ANCIENT_SEED.get());
                        output.accept(ModItems.KELPWEAVE_BLADE.get());
                        output.accept(ModItems.WISDOM_GEM.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
