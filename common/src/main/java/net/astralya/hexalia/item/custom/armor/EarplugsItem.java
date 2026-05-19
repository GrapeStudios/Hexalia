package net.astralya.hexalia.item.custom.armor;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class EarplugsItem extends HexaliaGeoArmorItem {
  private static final String INVISIBILITY_KEY = "HexaliaInvisFromItem";

  public EarplugsItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
    super(material, type, properties, "earplugs", "earplugs");
  }

  @Override
  public void inventoryTick(
      ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
    if (!(entity instanceof Player player)) {
      super.inventoryTick(stack, level, entity, slot, selected);
      return;
    }

    boolean wearingThis = player.getItemBySlot(EquipmentSlot.HEAD).equals(stack);
    if (!level.isClientSide()) {
      if (wearingThis && player.isCrouching()) {
        MobEffectInstance current = player.getEffect(MobEffects.INVISIBILITY);
        if (current == null || current.getDuration() <= 10) {
          player.addEffect(
              new MobEffectInstance(MobEffects.INVISIBILITY, 20, 0, false, false, false));
        }
        CompoundTag tag = customData(stack);
        tag.putBoolean(INVISIBILITY_KEY, true);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        if (stack.isDamageableItem() && level.getGameTime() % 20L == 0L && !player.isCreative()) {
          stack.hurtAndBreak(1, player, EquipmentSlot.HEAD);
        }
      } else if (removeInvisibilityMarker(stack) && player.hasEffect(MobEffects.INVISIBILITY)) {
        player.removeEffect(MobEffects.INVISIBILITY);
      }
    }

    super.inventoryTick(stack, level, entity, slot, selected);
  }

  private static CompoundTag customData(ItemStack stack) {
    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
    return data == null ? new CompoundTag() : data.copyTag();
  }

  private static boolean removeInvisibilityMarker(ItemStack stack) {
    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
    if (data == null) {
      return false;
    }
    CompoundTag tag = data.copyTag();
    boolean fromItem = tag.getBoolean(INVISIBILITY_KEY);
    tag.remove(INVISIBILITY_KEY);
    if (tag.isEmpty()) {
      stack.remove(DataComponents.CUSTOM_DATA);
    } else {
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
    return fromItem;
  }
}
