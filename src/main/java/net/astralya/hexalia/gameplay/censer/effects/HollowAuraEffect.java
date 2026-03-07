package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class HollowAuraEffect implements ICenserEffect {

    private static final int STRIP_INTERVAL = 16;
    private static final ColorParticleOption HOLLOW_PARTICLE =
            ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.15f, 0.0f, 0.2f);

    private int tickCounter = 0;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.hollow_aura";
    }

    @Override
    public void onTick(ServerLevel level, BlockPos pos) {
        spawnAmbientParticles(level, pos);

        tickCounter++;
        if (tickCounter < STRIP_INTERVAL) return;
        tickCounter = 0;

        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        AABB area = new AABB(pos).inflate(radius);

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
            stripBeneficialEffects(level, entity);
        }
    }

    private static void stripBeneficialEffects(ServerLevel level, LivingEntity entity) {
        List<Holder<MobEffect>> toRemove = new ArrayList<>();
        for (var entry : entity.getActiveEffects()) {
            MobEffectCategory category = entry.getEffect().value().getCategory();
            if (category == MobEffectCategory.BENEFICIAL || category == MobEffectCategory.NEUTRAL) {
                toRemove.add(entry.getEffect());
            }
        }
        if (toRemove.isEmpty()) return;
        for (Holder<MobEffect> effect : toRemove) {
            entity.removeEffect(effect);
        }
        spawnStripParticles(level, entity);
    }

    private static void spawnAmbientParticles(ServerLevel level, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + (level.random.nextDouble() * 2 - 1) * radius;
            double y = pos.getY() + level.random.nextDouble() * 2.5;
            double z = pos.getZ() + (level.random.nextDouble() * 2 - 1) * radius;
            level.sendParticles(HOLLOW_PARTICLE, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private static void spawnStripParticles(ServerLevel level, LivingEntity entity) {
        level.sendParticles(HOLLOW_PARTICLE,
                entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ(),
                12, 0.3, 0.4, 0.3, 0.05);
    }
}