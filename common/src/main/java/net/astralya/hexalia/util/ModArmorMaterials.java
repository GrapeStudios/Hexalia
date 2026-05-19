package net.astralya.hexalia.util;

import java.util.EnumMap;
import java.util.List;
import net.astralya.hexalia.Hexalia;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public final class ModArmorMaterials {
  public static final Holder<ArmorMaterial> SILKWEAVE =
      Holder.direct(
          new ArmorMaterial(
              Util.make(
                  new EnumMap<>(ArmorItem.Type.class),
                  map -> {
                    map.put(ArmorItem.Type.BOOTS, 2);
                    map.put(ArmorItem.Type.LEGGINGS, 5);
                    map.put(ArmorItem.Type.CHESTPLATE, 6);
                    map.put(ArmorItem.Type.HELMET, 2);
                    map.put(ArmorItem.Type.BODY, 0);
                  }),
              22,
              SoundEvents.ARMOR_EQUIP_LEATHER,
              () -> Ingredient.of(ModItems.SILK_FIBER.get()),
              List.of(
                  new ArmorMaterial.Layer(
                      ResourceLocation.fromNamespaceAndPath(Hexalia.MOD_ID, "silkweave"))),
              0.0F,
              0.0F));

  public static final Holder<ArmorMaterial> MOONWEAVE =
      Holder.direct(
          new ArmorMaterial(
              Util.make(
                  new EnumMap<>(ArmorItem.Type.class),
                  map -> {
                    map.put(ArmorItem.Type.BOOTS, 3);
                    map.put(ArmorItem.Type.LEGGINGS, 6);
                    map.put(ArmorItem.Type.CHESTPLATE, 8);
                    map.put(ArmorItem.Type.HELMET, 3);
                    map.put(ArmorItem.Type.BODY, 0);
                  }),
              22,
              SoundEvents.ARMOR_EQUIP_LEATHER,
              () -> Ingredient.of(ModItems.CELESTIAL_CRYSTAL.get()),
              List.of(
                  new ArmorMaterial.Layer(
                      ResourceLocation.fromNamespaceAndPath(Hexalia.MOD_ID, "moonweave"))),
              0.0F,
              0.0F));

  private ModArmorMaterials() {}
}
