package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class UndeadVeilEffect implements ICenserEffect {

    private final VeilCallback onStartCallback;
    private final VeilCallback onStopCallback;

    public UndeadVeilEffect(VeilCallback onStartCallback, VeilCallback onStopCallback) {
        this.onStartCallback = onStartCallback;
        this.onStopCallback = onStopCallback;
    }

    @Override
    public boolean usesSpatialCache() {
        return true;
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

        for (MobEntity mob : world.getEntitiesByClass(MobEntity.class, area, entity -> entity.getType().isIn(ModTags.EntityTypes.AFFECTED_BY_UNDEAD_VEIL))) {
            mob.setTarget(null);
            mob.setAttacker(null);
            mob.setAttacking(false);

            if (mob instanceof Angerable angerable) {
                angerable.stopAnger();
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

            world.spawnParticles(
                    ParticleTypes.ENTITY_EFFECT,
                    x,
                    y,
                    z,
                    1,
                    0.9,
                    0.95,
                    1.0,
                    0.0
            );
        }
    }

    private static void spawnMobVeilParticles(ServerWorld world, MobEntity mob) {
        world.spawnParticles(
                ParticleTypes.ENTITY_EFFECT,
                mob.getX(),
                mob.getY() + mob.getHeight() * 0.5,
                mob.getZ(),
                6,
                0.9,
                0.95,
                1.0,
                0.02
        );

        world.spawnParticles(
                ParticleTypes.CLOUD,
                mob.getX(),
                mob.getY() + mob.getHeight() * 0.8,
                mob.getZ(),
                2,
                0.2,
                0.1,
                0.2,
                0.005
        );
    }

    public interface VeilCallback {
        void run(ServerWorld world, BlockPos pos);
    }
}