package net.astralya.hexalia.item.client;

import net.astralya.hexalia.item.custom.GhostVeilItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GhostVeilRenderer extends GeoArmorRenderer<GhostVeilItem> {
    public GhostVeilRenderer() {
        super(new GhostVeilModel());
    }
}
