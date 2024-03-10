package net.grapes.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.world.World;

import java.util.List;

public class ConcoctionFireAndStrength extends StatusEffect {
    public ConcoctionFireAndStrength(StatusEffectCategory category, int color) {
        super(category, color); // Passing the category directly
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // Do nothing here, effect is applied on attack event
    }

    // Method to apply the effect to nearby entities
    public void applyEffectToWorld(LivingEntity source, World world, double range) {
        List<Entity> entities = world.getOtherEntities(null, source.getBoundingBox().expand(range, range, range));

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity && entity != source) {
                applyEffect(source, (LivingEntity) entity);
            }
        }
    }

    // Method to apply the effect
    private void applyEffect(LivingEntity source, LivingEntity target) {
        // Apply fire aspect effect to the entity
        target.setOnFireFor(5 + getAmplifier(target) * 2); // Apply fire for 5 + amplifier * 2 ticks

        // Keep the effect for 2 minutes (1200 ticks)
        target.addStatusEffect(new StatusEffectInstance(this, 1200));
    }

    // Method to get the amplifier level of the effect for the target entity
    private int getAmplifier(LivingEntity entity) {
        StatusEffectInstance effectInstance = entity.getStatusEffect(this);
        return effectInstance != null ? effectInstance.getAmplifier() : 0;
    }
}
