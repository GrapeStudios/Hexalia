package net.astralya.hexalia.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class ModUtil {
  private ModUtil() {}

  public static void removeHarmfulEffects(LivingEntity entity) {
    List<MobEffectInstance> harmfulEffects =
        new ArrayList<>(
            entity.getActiveEffects().stream()
                .filter(
                    effect -> effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL)
                .toList());

    for (MobEffectInstance effect : harmfulEffects) {
      entity.removeEffect(effect.getEffect());
    }
  }
}
