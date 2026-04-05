package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class TidalPullEffect implements ICenserEffect {

    private static final double PULL_FORCE = 0.1;
    private static final double PULL_TARGET_RADIUS = 1.5;
    private static final int PARTICLE_INTERVAL = 4;

    private int tickCounter = 0;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.tidal_pull";
    }

    @Override
    public void onTick(ServerWorld world, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        Box area = new Box(pos).expand(radius);
        Vec3d target = Vec3d.ofCenter(pos);

        List<Entity> entities = new ArrayList<>();
        entities.addAll(world.getEntitiesByClass(AnimalEntity.class, area, entity -> true));
        entities.addAll(world.getEntitiesByClass(HostileEntity.class, area, entity -> true));
        entities.addAll(world.getEntitiesByClass(ItemEntity.class, area, entity -> true));

        for (Entity entity : entities) {
            Vec3d entityPos = entity.getPos();
            double distSq = entityPos.squaredDistanceTo(target);

            if (distSq <= PULL_TARGET_RADIUS * PULL_TARGET_RADIUS) {
                continue;
            }

            Vec3d direction = target.subtract(entityPos).normalize();
            double dist = Math.sqrt(distSq);
            double scaledForce = PULL_FORCE * (1.0 + dist / radius);

            entity.setVelocity(entity.getVelocity().add(direction.multiply(scaledForce)));
            entity.velocityModified = true;
        }

        tickCounter++;
        if (tickCounter >= PARTICLE_INTERVAL) {
            tickCounter = 0;
            spawnAmbientParticles(world, pos, radius);
            spawnCurrentParticles(world, pos, radius);
        }
    }

    private static void spawnAmbientParticles(ServerWorld world, BlockPos pos, int radius) {
        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + (world.random.nextDouble() * 2 - 1) * radius;
            double y = pos.getY() + world.random.nextDouble() * 2.0;
            double z = pos.getZ() + (world.random.nextDouble() * 2 - 1) * radius;

            world.spawnParticles(
                    ParticleTypes.ENTITY_EFFECT,
                    x,
                    y,
                    z,
                    1,
                    0.0,
                    0.55,
                    1.0,
                    0.0
            );
        }
    }

    private static void spawnCurrentParticles(ServerWorld world, BlockPos pos, int radius) {
        Vec3d center = Vec3d.ofCenter(pos);
        double angle = world.random.nextDouble() * Math.PI * 2.0;
        double startX = pos.getX() + Math.cos(angle) * radius;
        double startZ = pos.getZ() + Math.sin(angle) * radius;
        double startY = pos.getY() + world.random.nextDouble() * 1.5;
        Vec3d start = new Vec3d(startX, startY, startZ);
        Vec3d direction = center.subtract(start).normalize();
        int steps = 5;
        double stepSize = radius / (double) steps;

        for (int i = 0; i < steps; i++) {
            Vec3d point = start.add(direction.multiply(i * stepSize));
            world.spawnParticles(
                    ParticleTypes.FALLING_WATER,
                    point.x,
                    point.y,
                    point.z,
                    1,
                    0.05,
                    0.05,
                    0.05,
                    0.0
            );
        }
    }
}