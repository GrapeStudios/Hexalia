package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.phys.AABB;

public class WitheringCalmEffect implements ICenserEffect {

    private static final int PULSE_INTERVAL   = 32;
    private static final int WITHER_DURATION  = 140;
    private static final int WITHER_AMPLIFIER = 1;

    private static final ColorParticleOption CALM_PARTICLE =
            ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.08f, 0.0f, 0.12f);

    private int tickCounter = 0;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.withering_calm";
    }

    @Override
    public void onTick(ServerLevel level, BlockPos pos) {
        spawnAmbientParticles(level, pos);

        tickCounter++;
        if (tickCounter < PULSE_INTERVAL) return;
        tickCounter = 0;

        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        AABB area = new AABB(pos).inflate(radius);

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
            applyWither(entity);
            if (entity instanceof Mob mob) stripAggression(mob);
        }
    }

    private static void applyWither(LivingEntity entity) {
        entity.forceAddEffect(new MobEffectInstance(
                MobEffects.WITHER, WITHER_DURATION, WITHER_AMPLIFIER, false, false, true), null);
    }

    private static void stripAggression(Mob mob) {
        mob.setTarget(null);
        mob.setLastHurtByMob(null);
        mob.setLastHurtByPlayer(null);
        mob.setNoActionTime(60);
        if (mob instanceof NeutralMob neutral) {
            neutral.setPersistentAngerTarget(null);
            neutral.stopBeingAngry();
        }
    }

    private static void spawnAmbientParticles(ServerLevel level, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + (level.random.nextDouble() * 2 - 1) * radius;
            double y = pos.getY() + level.random.nextDouble() * 2.5;
            double z = pos.getZ() + (level.random.nextDouble() * 2 - 1) * radius;
            level.sendParticles(CALM_PARTICLE, x, y, z, 1, 0, 0, 0, 0);
        }
    }
}