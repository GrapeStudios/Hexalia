package net.astralya.hexalia.effect.custom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;

public class OverfedEffect extends MobEffect {
  public OverfedEffect(MobEffectCategory category, int color) {
    super(category, color);
  }

  @Override
  public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
    if (!livingEntity.getCommandSenderWorld().isClientSide()
        && livingEntity instanceof Player player) {
      FoodData foodData = player.getFoodData();
      boolean isPlayerHealing =
          player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)
              && player.isHurt()
              && foodData.getFoodLevel() >= 18;
      if (!isPlayerHealing) {
        float exhaustion = foodData.getExhaustionLevel();
        float reduction = Math.min(exhaustion, 4.0F);
        if (exhaustion > 0.0F) {
          player.causeFoodExhaustion(-reduction);
        }
      }
      return true;
    }
    return super.applyEffectTick(livingEntity, amplifier);
  }

  @Override
  public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
    return true;
  }
}
