package net.grapes.hexalia.effect;

import net.grapes.hexalia.mixin.accesors.ExhaustionAccesorMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;

public class StuffedEffect extends StatusEffect {
    protected StuffedEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getEntityWorld().isClient() && entity instanceof PlayerEntity player) {
            HungerManager hungerManager = player.getHungerManager();
            boolean isPlayerHealing = player.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)
                    && player.canFoodHeal() && hungerManager.getFoodLevel() >= 18;
            if (!isPlayerHealing){
                float exhaustion = ((ExhaustionAccesorMixin) hungerManager).getExhaustion();
                float reduce = Math.min(exhaustion, 4f);
                if (exhaustion > .0f) {
                    player.addExhaustion(-reduce);
                }
            }
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
