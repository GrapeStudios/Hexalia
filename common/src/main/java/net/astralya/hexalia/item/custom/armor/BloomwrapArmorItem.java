package net.astralya.hexalia.item.custom.armor;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;

public class BloomwrapArmorItem extends HexaliaGeoArmorItem {
  public BloomwrapArmorItem(
      Holder<ArmorMaterial> material, Type type, Properties properties, String name) {
    super(material, type, properties, name, "bloomwrap");
  }
}
