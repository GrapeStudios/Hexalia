package net.grapes.hexalia.effect.custom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DaybloomEffect extends MobEffect {

    private static final int COOLDOWN = 100;

    public DaybloomEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            Level level = player.level();

            if (isNight(level)) {
                player.hurt(player.damageSources().magic(), 1.0F);
            }
            if (isDay(level)) {
                player.heal(2.0F);
                increaseSpeed(player, 1);
            } else {
                defaultSpeed(player);
            }
        }
    }

    private boolean isDay(Level level) {
        long time = level.getDayTime();
        return time >= 0 && time < 13000;
    }

    private boolean isNight(Level level) {
        return level.getMoonBrightness() > 0.25;
    }

    private void increaseSpeed(Player player, int amplifier) {
        var movementSpeedAttribute = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttribute != null) {
            double baseSpeed = 0.1;
            double speedMultiplier = 1 + 0.2 * (amplifier + 1);
            movementSpeedAttribute.setBaseValue(baseSpeed * speedMultiplier);
        }
    }

    private void defaultSpeed(Player player) {
        var movementSpeedAttribute = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttribute != null) {
            movementSpeedAttribute.setBaseValue(0.1);
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration % COOLDOWN == 0;
    }
}