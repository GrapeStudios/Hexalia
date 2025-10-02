package net.astralya.hexalia.item.custom.client;

import net.astralya.hexalia.item.custom.BoggedBootsItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BoggedBootsRenderer extends GeoArmorRenderer<BoggedBootsItem> {
    public BoggedBootsRenderer() {
        super(new BoggedBootsModel());
    }
}
