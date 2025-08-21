package net.astralya.hexalia.effect.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class SlimewalkerEffect extends MobEffect {

    public SlimewalkerEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.onGround() && entity.isSuppressingBounce()) {
            Vec3 movement = entity.getDeltaMovement();
            entity.setDeltaMovement(movement.x, 1.0D, movement.z);
            entity.hasImpulse = true;
            entity.playSound(SoundEvents.SLIME_JUMP, 1.0F, 1.0F);

            for (int i = 0; i < 8; ++i) {
                float a = entity.level().random.nextFloat() * ((float) Math.PI * 2F);
                float a1 = entity.level().random.nextFloat() * 0.5F + 0.5F;
                float a2 = Mth.sin(a) * 0.5F * a1;
                float a3 = Mth.cos(a) * 0.5F * a1;
                entity.level().addParticle(ParticleTypes.ITEM_SLIME, entity.getX() + (double) a2, entity.getY(), entity.getZ() + (double) a3, 0.0D, 0.0D, 0.0D);
            }
        }
        entity.fallDistance = 0.0F;
        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}