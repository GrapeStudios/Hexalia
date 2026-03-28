package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

public class HollowAuraEffect implements ICenserEffect {

    private static final int STRIP_INTERVAL = 16;
    private static final EntityEffectParticleEffect HOLLOW_PARTICLE =
            EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0.15f, 0.0f, 0.2f);

    private int tickCounter = 0;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.hollow_aura";
    }

    @Override
    public void onTick(ServerWorld world, BlockPos pos) {
        spawnAmbientParticles(world, pos);
        tickCounter++;
        if (tickCounter < STRIP_INTERVAL) return;
        tickCounter = 0;
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        Box area = new Box(pos).expand(radius);
        for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, area, e -> true)) {
            stripBeneficialEffects(world, entity);
        }
    }

    private static void stripBeneficialEffects(ServerWorld world, LivingEntity entity) {
        List<RegistryEntry<StatusEffect>> toRemove = new ArrayList<>();
        for (var entry : entity.getStatusEffects()) {
            StatusEffectCategory category = entry.getEffectType().value().getCategory();
            if (category == StatusEffectCategory.BENEFICIAL || category == StatusEffectCategory.NEUTRAL) {
                toRemove.add(entry.getEffectType());
            }
        }
        if (toRemove.isEmpty()) return;
        for (RegistryEntry<StatusEffect> effect : toRemove) {
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
            world.spawnParticles(HOLLOW_PARTICLE, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private static void spawnStripParticles(ServerWorld world, LivingEntity entity) {
        world.spawnParticles(HOLLOW_PARTICLE,
                entity.getX(), entity.getY() + entity.getHeight() * 0.5, entity.getZ(),
                12, 0.3, 0.4, 0.3, 0.05);
    }
}