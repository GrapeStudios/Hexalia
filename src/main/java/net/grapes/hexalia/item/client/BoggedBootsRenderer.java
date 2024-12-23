package net.grapes.hexalia.item.client;

import net.grapes.hexalia.item.custom.BoggedBootsItem;
import net.grapes.hexalia.item.custom.GhostVeilItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BoggedBootsRenderer extends GeoArmorRenderer<BoggedBootsItem> {
    public BoggedBootsRenderer() {
        super(new BoggedBootsModel());
    }
}
