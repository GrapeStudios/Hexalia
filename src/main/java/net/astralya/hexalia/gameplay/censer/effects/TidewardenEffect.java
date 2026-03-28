package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class TidewardenEffect implements ICenserEffect {

    private static final int PULSE_INTERVAL  = 16;
    private static final int EFFECT_DURATION = 140;
    private static final EntityEffectParticleEffect WARD_PARTICLE =
            EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0.75f, 0.9f, 1.0f);

    private int tickCounter = 0;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.tidewarden";
    }

    @Override
    public void onTick(ServerWorld world, BlockPos pos) {
        spawnAmbientParticles(world, pos);
        tickCounter++;
        if (tickCounter < PULSE_INTERVAL) return;
        tickCounter = 0;
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        Box area = new Box(pos).expand(radius);
        for (PlayerEntity player : world.getEntitiesByClass(PlayerEntity.class, area, e -> true)) {
            applyWard(world, player);
        }
    }

    private static void applyWard(ServerWorld world, PlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.RESISTANCE, EFFECT_DURATION, 0, false, false, true));
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOW_FALLING, EFFECT_DURATION, 0, false, false, true));
        spawnWardParticles(world, player);
    }

    private static void spawnWardParticles(ServerWorld world, PlayerEntity player) {
        world.spawnParticles(WARD_PARTICLE,
                player.getX(), player.getY() + player.getHeight() * 0.5, player.getZ(),
                6, 0.3, 0.4, 0.3, 0.03);
    }

    private static void spawnAmbientParticles(ServerWorld world, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + (world.random.nextDouble() * 2 - 1) * radius;
            double y = pos.getY() + world.random.nextDouble() * 2.5;
            double z = pos.getZ() + (world.random.nextDouble() * 2 - 1) * radius;
            world.spawnParticles(WARD_PARTICLE, x, y, z, 1, 0, 0, 0, 0);
        }
    }
}