package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class UndeadVeilEffect implements ICenserEffect {

    @Override
    public boolean usesSpatialCache() { return true; }

    public interface VeilCallback {
        void run(ServerWorld world, BlockPos pos);
    }

    private final VeilCallback onStartCallback;
    private final VeilCallback onStopCallback;

    private static final EntityEffectParticleEffect VEIL_PARTICLE =
            EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0.9f, 0.95f, 1.0f);

    public UndeadVeilEffect(VeilCallback onStartCallback, VeilCallback onStopCallback) {
        this.onStartCallback = onStartCallback;
        this.onStopCallback = onStopCallback;
    }

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.undead_veil";
    }

    @Override
    public void onStart(ServerWorld world, BlockPos pos) {
        onStartCallback.run(world, pos);
    }

    @Override
    public void onTick(ServerWorld world, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        Box area = new Box(pos).expand(radius);
        spawnAmbientParticles(world, pos, radius);
        for (MobEntity mob : world.getEntitiesByClass(MobEntity.class, area,
                e -> e.getType().isIn(EntityTypeTags.UNDEAD))) {
            mob.setTarget(null);
            mob.setAttacker(null);
            mob.setAttacking(false);
            if (mob instanceof Angerable neutral) {
                neutral.stopAnger();
            }
            mob.setInvulnerable(false);
            spawnMobVeilParticles(world, mob);
        }
    }

    @Override
    public void onStop(ServerWorld world, BlockPos pos) {
        onStopCallback.run(world, pos);
    }

    private static void spawnAmbientParticles(ServerWorld world, BlockPos pos, int radius) {
        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + (world.random.nextDouble() * 2 - 1) * radius;
            double y = pos.getY() + world.random.nextDouble() * 2.5;
            double z = pos.getZ() + (world.random.nextDouble() * 2 - 1) * radius;
            world.spawnParticles(VEIL_PARTICLE, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private static void spawnMobVeilParticles(ServerWorld world, MobEntity mob) {
        world.spawnParticles(VEIL_PARTICLE,
                mob.getX(), mob.getY() + mob.getHeight() * 0.5, mob.getZ(),
                6, 0.3, 0.4, 0.3, 0.02);
        world.spawnParticles(ParticleTypes.CLOUD,
                mob.getX(), mob.getY() + mob.getHeight() * 0.8, mob.getZ(),
                2, 0.2, 0.1, 0.2, 0.005);
    }
}