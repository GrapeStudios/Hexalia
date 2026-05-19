package net.astralya.hexalia.item.custom.armor;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class GhostveilItem extends HexaliaGeoArmorItem {
  public GhostveilItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
    super(material, type, properties, "ghostveil", "ghostveil");
  }

  public static boolean isWornBy(LivingEntity entity) {
    return entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof GhostveilItem;
  }

  @Override
  public void appendHoverText(
      ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    tooltip.add(Component.translatable("tooltip.hexalia.ghostveil").withStyle(ChatFormatting.GRAY));
  }
}
