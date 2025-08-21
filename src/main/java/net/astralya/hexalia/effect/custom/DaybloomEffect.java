package net.astralya.hexalia.effect.custom;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.UUID;

public class DaybloomEffect extends StatusEffect {

    private static final int COOLDOWN = 100;
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("e1234567-89ab-cdef-0123-456789abcdef");

    public DaybloomEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            World world = player.getWorld();

            if (isNight(world)) {
                player.damage(player.getDamageSources().magic(), 1.5F);
                removeSpeedModifier(player);
            } else if (isDay(world)) {
                player.heal(2.0F);
                applySpeedModifier(player, amplifier);
            } else {
                removeSpeedModifier(player);
            }
        }
    }

    private boolean isDay(World world) {
        long time = world.getTimeOfDay() % 24000;
        return time >= 0 && time < 13000;
    }

    private boolean isNight(World world) {
        long time = world.getTimeOfDay() % 24000;
        return time >= 13000 && time <= 23000;
    }

    private void applySpeedModifier(PlayerEntity player, int amplifier) {
        EntityAttributeInstance speedAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttribute != null) {
            removeSpeedModifier(player);

            double speedBoost = 0.05 * (amplifier + 1);
            speedAttribute.addTemporaryModifier(
                    new EntityAttributeModifier(SPEED_MODIFIER_UUID, "Daybloom Speed Boost", speedBoost, EntityAttributeModifier.Operation.ADDITION)
            );
        }
    }

    private void removeSpeedModifier(PlayerEntity player) {
        EntityAttributeInstance speedAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(SPEED_MODIFIER_UUID);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % COOLDOWN == 0;
    }
}
