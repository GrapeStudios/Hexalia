package net.grapes.hexalia.item.client;

import net.grapes.hexalia.item.custom.GhostVeilItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GhostVeilRenderer extends GeoArmorRenderer<GhostVeilItem> {
    public GhostVeilRenderer() {
        super(new GhostVeilModel());
    }
}
