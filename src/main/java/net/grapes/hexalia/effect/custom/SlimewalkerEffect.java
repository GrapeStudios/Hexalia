package net.grapes.hexalia.effect.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class SlimewalkerEffect extends MobEffect {
    // Constants for easier tuning
    private static final String WAS_ON_GROUND_KEY = "HexaliaWasOnGround";
    private static final double BASE_BOUNCE_HEIGHT = 1.0;
    private static final int PARTICLE_COUNT = 8;

    public SlimewalkerEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        boolean wasOnGround = entity.getPersistentData().getBoolean(WAS_ON_GROUND_KEY);
        boolean isOnGround = entity.onGround();

        // Detect landing and apply bounce
        if (!wasOnGround && isOnGround && !entity.isShiftKeyDown()) {
            Vec3 movement = entity.getDeltaMovement();
            // Optional: Scale bounce with amplifier (if you want stronger bounces at higher levels)
            double bounceHeight = BASE_BOUNCE_HEIGHT * (1 + amplifier * 0.2);
            entity.setDeltaMovement(movement.x, bounceHeight, movement.z);
            entity.hasImpulse = true;

            // Play sound with random pitch variation
            entity.playSound(SoundEvents.SLIME_JUMP,
                    1.0F,
                    1.0F + (entity.level().random.nextFloat() - 0.5F) * 0.2F);

            // Spawn particles in a circle
            for (int i = 0; i < PARTICLE_COUNT; ++i) {
                float angle = entity.level().random.nextFloat() * Mth.TWO_PI;
                float radius = entity.level().random.nextFloat() * 0.5F + 0.5F;
                float xOffset = Mth.sin(angle) * 0.5F * radius;
                float zOffset = Mth.cos(angle) * 0.5F * radius;
                entity.level().addParticle(ParticleTypes.ITEM_SLIME,
                        entity.getX() + xOffset,
                        entity.getY(),
                        entity.getZ() + zOffset,
                        0.0D, 0.0D, 0.0D);
            }
        }

        // Always reset fall distance while active
        entity.fallDistance = 0.0F;

        // Save current onGround state for next tick
        entity.getPersistentData().putBoolean(WAS_ON_GROUND_KEY, isOnGround);

        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}