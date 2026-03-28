package net.astralya.hexalia.datagen;

import java.util.concurrent.CompletableFuture;
import net.astralya.hexalia.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;

public class ModEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public ModEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries) {
        getOrCreateTagBuilder(ModTags.EntityTypes.SPIRITROOT_UNCAPTURABLE)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.GHAST)
                .add(EntityType.WITHER)
                .add(EntityType.PLAYER)
                .add(EntityType.WARDEN);

        getOrCreateTagBuilder(ModTags.EntityTypes.UNDEAD_VEIL_IMMUNE)
                .add(EntityType.WITHER);
    }
}