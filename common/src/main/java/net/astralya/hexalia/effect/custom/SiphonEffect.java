package net.astralya.hexalia.effect.custom;

import java.util.List;
import net.astralya.hexalia.HexaliaConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SiphonEffect extends MobEffect {
  protected final double modifier;

  public SiphonEffect(MobEffectCategory category, int color, double modifier) {
    super(category, color);
    this.modifier = modifier;
  }

  @Override
  public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
    if (!(livingEntity instanceof Player player) || player.isCrouching()) {
      return false;
    }

    Level level = player.level();
    double radius = HexaliaConfig.siphonRadius() + amplifier;
    AABB box = player.getBoundingBox().inflate(radius);
    List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, box, item -> true);

    for (ItemEntity itemEntity : itemEntities) {
      if (player.getInventory().getFreeSlot() == -1) {
        Vec3 direction = player.getEyePosition().subtract(itemEntity.position());
        int effectiveAmplifier = Math.min(amplifier + 1, 3);

        itemEntity.setPos(
            itemEntity.getX(),
            itemEntity.getY() + direction.y * 0.015D * effectiveAmplifier,
            itemEntity.getZ());

        if (level.isClientSide) {
          itemEntity.yOld = itemEntity.getY();
        }

        itemEntity.setDeltaMovement(
            itemEntity
                .getDeltaMovement()
                .scale(0.95D)
                .add(direction.normalize().scale(0.10D * effectiveAmplifier)));
        return true;
      }

      itemEntity.playerTouch(player);
    }
    return super.applyEffectTick(livingEntity, amplifier);
  }

  @Override
  public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
    return true;
  }
}
