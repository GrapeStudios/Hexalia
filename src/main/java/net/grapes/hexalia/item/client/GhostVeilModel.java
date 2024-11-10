package net.grapes.hexalia.item.client;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.item.custom.GhostVeilItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GhostVeilModel extends GeoModel<GhostVeilItem> {
    @Override
    public ResourceLocation getModelResource(GhostVeilItem ghostVeilItem) {
        return new ResourceLocation(HexaliaMod.MOD_ID, "geo/ghostveil.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GhostVeilItem ghostVeilItem) {
        return new ResourceLocation(HexaliaMod.MOD_ID, "textures/armor/ghostveil.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GhostVeilItem ghostVeilItem) {
        return new ResourceLocation(HexaliaMod.MOD_ID, "animations/ghostveil.animation.json");
    }
}
