package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class TidewardenEffect implements ICenserEffect {

    private static final int PULSE_INTERVAL       = 16;
    private static final int EFFECT_DURATION      = 140;

    private static final ColorParticleOption WARD_PARTICLE =
            ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.75f, 0.9f, 1.0f);

    private int tickCounter = 0;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.tidewarden";
    }

    @Override
    public void onTick(ServerLevel level, BlockPos pos) {
        spawnAmbientParticles(level, pos);

        tickCounter++;
        if (tickCounter < PULSE_INTERVAL) return;
        tickCounter = 0;

        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        AABB area = new AABB(pos).inflate(radius);

        for (Player player : level.getEntitiesOfClass(Player.class, area)) {
            applyWard(player);
        }
    }

    private static void applyWard(Player player) {
        player.forceAddEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE, EFFECT_DURATION, 0, false, false, true), null);
        player.forceAddEffect(new MobEffectInstance(
                MobEffects.SLOW_FALLING, EFFECT_DURATION, 0, false, false, true), null);
        spawnWardParticles((ServerLevel) player.level(), player);
    }

    private static void spawnWardParticles(ServerLevel level, Player player) {
        level.sendParticles(WARD_PARTICLE,
                player.getX(), player.getY() + player.getBbHeight() * 0.5, player.getZ(),
                6, 0.3, 0.4, 0.3, 0.03);
    }

    private static void spawnAmbientParticles(ServerLevel level, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + (level.random.nextDouble() * 2 - 1) * radius;
            double y = pos.getY() + level.random.nextDouble() * 2.5;
            double z = pos.getZ() + (level.random.nextDouble() * 2 - 1) * radius;
            level.sendParticles(WARD_PARTICLE, x, y, z, 1, 0, 0, 0, 0);
        }
    }
}