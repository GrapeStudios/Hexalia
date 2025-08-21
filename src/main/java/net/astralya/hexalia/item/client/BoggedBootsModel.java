package net.astralya.hexalia.item.client;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.BoggedBootsItem;
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
