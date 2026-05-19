package net.astralya.hexalia.event;

import dev.architectury.event.events.common.TickEvent;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class BloomwrapEvents {
  private static final int EFFECT_DURATION = 60;
  private static final double FLOWER_SCAN_RADIUS = 4.0;

  private BloomwrapEvents() {}

  public static void register() {
    TickEvent.PLAYER_POST.register(BloomwrapEvents::onPlayerTick);
  }

  public static boolean isWearing(LivingEntity entity, EquipmentSlot slot, Item item) {
    ItemStack stack = entity.getItemBySlot(slot);
    return !stack.isEmpty() && stack.getItem() == item;
  }

  private static void onPlayerTick(Player player) {
    if (player.level().isClientSide() || player.tickCount % 20 != 0) {
      return;
    }
    tickBoots(player);
    tickLeggings(player);
  }

  private static void tickBoots(Player player) {
    if (!isWearing(player, EquipmentSlot.FEET, ModItems.BLOOMWRAP_BOOTS.get())) {
      return;
    }
    BlockPos below = player.blockPosition().below();
    Level level = player.level();
    boolean onNature =
        level.getBlockState(below).is(BlockTags.DIRT)
            || level.getBlockState(below).is(BlockTags.LEAVES)
            || level.getBlockState(below).is(BlockTags.SMALL_FLOWERS)
            || level.getBlockState(below).is(BlockTags.TALL_FLOWERS);
    if (onNature) {
      player.addEffect(
          new MobEffectInstance(MobEffects.MOVEMENT_SPEED, EFFECT_DURATION, 0, false, false, true));
    }
  }

  private static void tickLeggings(Player player) {
    if (!isWearing(player, EquipmentSlot.LEGS, ModItems.BLOOMWRAP_LEGGINGS.get())) {
      return;
    }
    Level level = player.level();
    AABB scanArea = new AABB(player.blockPosition()).inflate(FLOWER_SCAN_RADIUS);
    boolean nearFlower =
        BlockPos.betweenClosedStream(
                BlockPos.containing(scanArea.minX, scanArea.minY, scanArea.minZ),
                BlockPos.containing(scanArea.maxX, scanArea.maxY, scanArea.maxZ))
            .anyMatch(
                pos ->
                    level.getBlockState(pos).is(BlockTags.SMALL_FLOWERS)
                        || level.getBlockState(pos).is(BlockTags.TALL_FLOWERS));
    if (nearFlower) {
      player.addEffect(
          new MobEffectInstance(MobEffects.REGENERATION, EFFECT_DURATION, 0, false, false, true));
    }
  }
}
