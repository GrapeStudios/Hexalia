package net.grapes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }
    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(ModTags.Items.COOKED_MEAT)
                .add(Items.COOKED_BEEF, Items.COOKED_CHICKEN,
                        Items.COOKED_MUTTON, Items.COOKED_PORKCHOP,
                        Items.COOKED_RABBIT, Items.COOKED_COD,
                        Items.COOKED_SALMON);

        getOrCreateTagBuilder(ModTags.Items.CRUSHED_PLANTS)
                .add(ModItems.DREAMSHROOM_PASTE, ModItems.SIREN_KELP_PASTE,
                        ModItems.SPIRIT_BLOOM_POWDER);

        getOrCreateTagBuilder(ModTags.Items.BREWS)
                .add(ModItems.BREW_OF_HOMESTEAD, ModItems.BREW_OF_VIGOR,
                        ModItems.BREW_OF_SLIMEY_STEP, ModItems.BREW_OF_WARDING);
    }
}
