package net.grapes.hexalia.block.entity;

import net.grapes.hexalia.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class WindsongBlockEntity extends BlockEntity {

    private int activeTicks = 0;
    private static final int DURATION = 600;
    private static final int AREA_RADIUS = 6;
    private int particleCooldown = 0;

    public WindsongBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WINDSONG_BE, pos, state);
    }

    public void activate() {
        this.activeTicks = DURATION;
    }

    public boolean isActive() {
        return this.activeTicks > 0;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (this.isActive()) {
            this.activeTicks--;

            if (world instanceof ServerWorld serverWorld) {
                Box area = new Box(pos).expand(AREA_RADIUS);
                List<Entity> projectiles = serverWorld.getEntitiesByClass(Entity.class, area,
                        entity -> entity instanceof ProjectileEntity);

                for (Entity projectile : projectiles) {
                    if (!projectile.isRemoved()) {
                        discardProjectile(serverWorld, projectile);
                    }
                }

                emitParticles(serverWorld, pos);
            }

            if (this.activeTicks <= 0) {
                world.playSound(null, pos, ModSounds.WIND_BURST, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.breakBlock(pos, false);
            }
        }
    }


    private static void discardProjectile(ServerWorld world, Entity projectile) {
        world.playSound(null, projectile.getX(), projectile.getY(), projectile.getZ(),
                ModSounds.WIND_DEFLECT, SoundCategory.BLOCKS, 1.0f, 1.0f);

        Vec3d pos = projectile.getPos();
        for (int i = 0; i < 5; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double radius = Math.random() * 0.5;
            double x = pos.x + radius * Math.cos(angle);
            double z = pos.z + radius * Math.sin(angle);
            double y = pos.y + Math.random() * 0.5;

            world.spawnParticles(ParticleTypes.EFFECT, x, y, z, 1, 0, 0, 0, 0.1);
        }

        projectile.discard();
    }

    private void emitParticles(ServerWorld world, BlockPos pos) {
        if (particleCooldown <= 0) {
            Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            for (int i = 0; i < 3; i++) {
                double angle = Math.random() * 2 * Math.PI;
                double radius = Math.random() * AREA_RADIUS;
                double x = center.x + radius * Math.cos(angle);
                double z = center.z + radius * Math.sin(angle);
                double y = center.y + Math.random() * 2;

                world.spawnParticles(ParticleTypes.CLOUD, x, y, z, 1, 0, 0, 0, 0.1);
            }
            particleCooldown = 5;
        } else {
            particleCooldown--;
        }
    }
}
