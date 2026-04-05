package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

public class HollowAuraEffect implements ICenserEffect {

    private static final int STRIP_INTERVAL = 16;

    private int tickCounter = 0;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.hollow_aura";
    }

    @Override
    public void onTick(ServerWorld world, BlockPos pos) {
        spawnAmbientParticles(world, pos);

        tickCounter++;
        if (tickCounter < STRIP_INTERVAL) {
            return;
        }

        tickCounter = 0;

        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        Box area = new Box(pos).expand(radius);

        for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, area, living -> true)) {
            stripBeneficialEffects(world, entity);
        }
    }

    private static void stripBeneficialEffects(ServerWorld world, LivingEntity entity) {
        List<StatusEffect> toRemove = new ArrayList<>();

        for (StatusEffectInstance instance : entity.getStatusEffects()) {
            StatusEffect effect = instance.getEffectType();
            StatusEffectCategory category = effect.getCategory();

            if (category == StatusEffectCategory.BENEFICIAL || category == StatusEffectCategory.NEUTRAL) {
                toRemove.add(effect);
            }
        }

        if (toRemove.isEmpty()) {
            return;
        }

        for (StatusEffect effect : toRemove) {
            entity.removeStatusEffect(effect);
        }

        spawnStripParticles(world, entity);
    }

    private static void spawnAmbientParticles(ServerWorld world, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();

        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + (world.random.nextDouble() * 2 - 1) * radius;
            double y = pos.getY() + world.random.nextDouble() * 2.5;
            double z = pos.getZ() + (world.random.nextDouble() * 2 - 1) * radius;

            world.spawnParticles(
                    ParticleTypes.ENTITY_EFFECT,
                    x,
                    y,
                    z,
                    1,
                    0.15,
                    0.0,
                    0.2,
                    0.0
            );
        }
    }

    private static void spawnStripParticles(ServerWorld world, LivingEntity entity) {
        world.spawnParticles(
                ParticleTypes.ENTITY_EFFECT,
                entity.getX(),
                entity.getY() + entity.getHeight() * 0.5,
                entity.getZ(),
                12,
                0.15,
                0.0,
                0.2,
                0.05
        );
    }
}