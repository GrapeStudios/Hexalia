package net.astralya.hexalia.effect.custom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import java.util.UUID;

public class DaybloomEffect extends MobEffect {

    private static final int COOLDOWN = 100;
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("e1234567-89ab-cdef-0123-456789abcdef");

    public DaybloomEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            Level level = player.level();

            if (isNight(level)) {
                player.hurt(player.damageSources().magic(), 1.5F);
                removeSpeedModifier(player);
            } else if (isDay(level)) {
                player.heal(2.0F);
                applySpeedModifier(player, amplifier);
            } else {
                removeSpeedModifier(player);
            }
        }
    }

    private boolean isDay(Level level) {
        long time = level.getDayTime() % 24000;
        return time >= 0 && time < 13000;
    }

    private boolean isNight(Level level) {
        long time = level.getDayTime() % 24000;
        return time >= 13000 && time <= 23000;
    }

    private void applySpeedModifier(Player player, int amplifier) {
        var movementSpeedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttribute != null) {
            removeSpeedModifier(player);

            double speedBoost = 0.05 * (amplifier + 1);
            movementSpeedAttribute.addTransientModifier(
                    new AttributeModifier(SPEED_MODIFIER_UUID, "Daybloom Speed Boost", speedBoost, AttributeModifier.Operation.ADDITION)
            );
        }
    }

    private void removeSpeedModifier(Player player) {
        var movementSpeedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttribute != null) {
            movementSpeedAttribute.removeModifier(SPEED_MODIFIER_UUID);
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration % COOLDOWN == 0;
    }
}
