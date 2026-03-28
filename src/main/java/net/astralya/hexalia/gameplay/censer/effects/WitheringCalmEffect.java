package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class WitheringCalmEffect implements ICenserEffect {

    private static final int PULSE_INTERVAL   = 32;
    private static final int WITHER_DURATION  = 140;
    private static final int WITHER_AMPLIFIER = 1;
    private static final EntityEffectParticleEffect CALM_PARTICLE =
            EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0.08f, 0.0f, 0.12f);

    private int tickCounter = 0;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.withering_calm";
    }

    @Override
    public void onTick(ServerWorld world, BlockPos pos) {
        spawnAmbientParticles(world, pos);
        tickCounter++;
        if (tickCounter < PULSE_INTERVAL) return;
        tickCounter = 0;
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        Box area = new Box(pos).expand(radius);
        for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, area, e -> true)) {
            applyWither(entity);
            if (entity instanceof MobEntity mob) stripAggression(mob);
        }
    }

    private static void applyWither(LivingEntity entity) {
        entity.addStatusEffect(new StatusEffectInstance(
                StatusEffects.WITHER, WITHER_DURATION, WITHER_AMPLIFIER, false, false, true));
    }

    private static void stripAggression(MobEntity mob) {
        mob.setTarget(null);
        mob.setAttacker(null);
        mob.setAttacking(false);
        if (mob instanceof Angerable neutral) {
            neutral.stopAnger();
        }
    }

    private static void spawnAmbientParticles(ServerWorld world, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + (world.random.nextDouble() * 2 - 1) * radius;
            double y = pos.getY() + world.random.nextDouble() * 2.5;
            double z = pos.getZ() + (world.random.nextDouble() * 2 - 1) * radius;
            world.spawnParticles(CALM_PARTICLE, x, y, z, 1, 0, 0, 0, 0);
        }
    }
}