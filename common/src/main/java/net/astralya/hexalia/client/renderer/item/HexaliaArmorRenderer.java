package net.astralya.hexalia.client.renderer.item;

import net.astralya.hexalia.item.custom.armor.HexaliaGeoArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class HexaliaArmorRenderer extends GeoArmorRenderer<HexaliaGeoArmorItem> {
  public HexaliaArmorRenderer(HexaliaGeoArmorItem item) {
    super(new HexaliaArmorModel(item));
  }
}
