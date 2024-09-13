package net.grapes.hexalia.item.client;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.item.custom.GhostVeilItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class GhostVeilModel extends GeoModel<GhostVeilItem> {
    @Override
    public Identifier getModelResource(GhostVeilItem animatable) {
        return new Identifier(HexaliaMod.MOD_ID, "geo/ghostveil.geo.json");
    }

    @Override
    public Identifier getTextureResource(GhostVeilItem animatable) {
        return new Identifier(HexaliaMod.MOD_ID, "textures/armor/ghostveil.png");
    }

    @Override
    public Identifier getAnimationResource(GhostVeilItem animatable) {
        return new Identifier(HexaliaMod.MOD_ID, "animations/ghostveil.animation.json");
    }
}
