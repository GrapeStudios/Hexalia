package net.astralya.hexalia.event;

import net.astralya.hexalia.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class SagePendantEvents {
  private SagePendantEvents() {}

  public static boolean hasSagePendant(Player player) {
    ItemStack offhand = player.getOffhandItem();
    return !offhand.isEmpty() && offhand.getItem() == ModItems.SAGE_PENDANT.get();
  }

  public static int boostedExperience(int value) {
    return value + (int) Math.floor(value * 2.0);
  }

  public static void damagePendant(Player player) {
    ItemStack offhand = player.getOffhandItem();
    if (player.level().isClientSide || player.isCreative() || !offhand.isDamageableItem()) {
      return;
    }

    if (player instanceof ServerPlayer serverPlayer
        && player.level() instanceof ServerLevel serverLevel) {
      offhand.hurtAndBreak(
          1,
          serverLevel,
          serverPlayer,
          brokenStack -> serverPlayer.onEquippedItemBroken(brokenStack, EquipmentSlot.OFFHAND));
    }
  }
}
