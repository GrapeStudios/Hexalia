package net.grapes.hexalia.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class BleedingEffect extends StatusEffect {

    private static final float BASE_DAMAGE = 0.5f;

    protected BleedingEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getEntityWorld().isClient && entity.getHealth() > 0f) {
            float damage = BASE_DAMAGE + amplifier * 0.2f;
            entity.damage(entity.getDamageSources().generic(), damage);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
