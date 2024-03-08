package net.grapes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }
    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
    getOrCreateTagBuilder(ModTags.Items.COOKED_MEAT)
            .add(Items.COOKED_BEEF)
            .add(Items.COOKED_CHICKEN)
            .add(Items.COOKED_MUTTON)
            .add(Items.COOKED_PORKCHOP)
            .add(Items.COOKED_RABBIT)
            .add(Items.COOKED_COD)
            .add(Items.COOKED_SALMON);
    }
}
