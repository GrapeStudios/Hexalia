package net.grapes.hexalia.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SlimewalkerEffect extends StatusEffect {
    protected SlimewalkerEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // Bounce effect on the player
        if (entity.isOnGround() && entity.bypassesLandingEffects()) {
            Vec3d movement = entity.getVelocity();
            entity.setVelocity(movement.x, 1D, movement.z);
            entity.velocityDirty = true;
            entity.playSound(SoundEvents.ENTITY_SLIME_JUMP, 1.0F, 1.0F);
            for (int i = 0; i < 8; ++i) {
                float a = entity.getWorld().random.nextFloat() * ((float) Math.PI * 2F);
                float a1 = entity.getWorld().random.nextFloat() * 0.5F + 0.5F;
                float a2 = MathHelper.sin(a) * 0.5F * a1;
                float a3 = MathHelper.cos(a) * 0.5F * a1;
                entity.getWorld().addParticle(ParticleTypes.ITEM_SLIME, entity.getX() + (double) a2, entity.getY(), entity.getZ() +
                        (double) a3, 0.0D, 0.0D, 0.0D);
            }

        }
        entity.fallDistance = 0.0F;
        // Climbing effect on the player
        if(entity.horizontalCollision) {
            Vec3d intialVec = entity.getVelocity();
            Vec3d climbVec = new Vec3d(intialVec.x, 0.2D, intialVec.z);
            entity.setVelocity(climbVec.x * 0.92D, climbVec.y * 0.98D, climbVec.z * 0.92D);
        }

        super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}

