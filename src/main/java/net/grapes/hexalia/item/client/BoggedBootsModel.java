package net.grapes.hexalia.item.client;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.item.custom.BoggedBootsItem;
import net.grapes.hexalia.item.custom.GhostVeilItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BoggedBootsModel extends GeoModel<BoggedBootsItem> {
    @Override
    public ResourceLocation getModelResource(BoggedBootsItem boggedBootsItem) {
        return new ResourceLocation(HexaliaMod.MOD_ID, "geo/bogged_boots.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BoggedBootsItem boggedBootsItem) {
        return new ResourceLocation(HexaliaMod.MOD_ID, "textures/armor/bogged_boots.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BoggedBootsItem boggedBootsItem) {
        return new ResourceLocation(HexaliaMod.MOD_ID, "animations/bogged_boots.animation.json");
    }
}
