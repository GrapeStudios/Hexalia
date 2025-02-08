package net.grapes.hexalia.block.entity;

import net.grapes.hexalia.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class WindsongBlockEntity extends BlockEntity {

    private int activeTicks = 0;
    private static final int DURATION = 600;
    private static final int AREA_RADIUS = 6;
    private int particleCooldown = 0;

    public WindsongBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WINDSONG_BE.get(), pPos, pBlockState);
    }

    public void activate() {
        this.activeTicks = DURATION;
    }

    public boolean isActive() {
        return this.activeTicks > 0;
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (this.isActive()) {
            this.activeTicks--;

            if (pLevel instanceof ServerLevel serverLevel) {
                AABB area = new AABB(pPos).inflate(AREA_RADIUS);
                List<Entity> projectiles = serverLevel.getEntitiesOfClass(Entity.class, area, entity -> entity instanceof Projectile);

                for (Entity projectile : projectiles) {
                    if (!projectile.isRemoved()) {
                        discardProjectile(serverLevel, projectile);
                    }
                }

                emitParticles(serverLevel, pPos);
            }

            if (this.activeTicks <= 0) {
                pLevel.playSound(null, pPos, ModSounds.WIND_BURST.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                pLevel.destroyBlock(pPos, false);
            }
        }
    }

    private void discardProjectile(ServerLevel pLevel, Entity projectile) {
        pLevel.playSound(null, projectile.getX(), projectile.getY(), projectile.getZ(),
                ModSounds.WIND_DEFLECT.get(), SoundSource.BLOCKS, 1.0f, 1.0f);

        Vec3 pos = projectile.position();
        for (int i = 0; i < 5; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double radius = Math.random() * 0.5;
            double x = pos.x + radius * Math.cos(angle);
            double z = pos.z + radius * Math.sin(angle);
            double y = pos.y + Math.random() * 0.5;

            pLevel.sendParticles(ParticleTypes.EFFECT, x, y, z, 1, 0, 0, 0, 0.1);
        }

        projectile.discard();
    }

    private void emitParticles(ServerLevel pLevel, BlockPos pPos) {
        if (particleCooldown <= 0) {
            Vec3 center = new Vec3(pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5);
            for (int i = 0; i < 3; i++) {
                double angle = Math.random() * 2 * Math.PI;
                double radius = Math.random() * AREA_RADIUS;
                double x = center.x + radius * Math.cos(angle);
                double z = center.z + radius * Math.sin(angle);
                double y = center.y + Math.random() * 2;

                pLevel.sendParticles(ParticleTypes.CLOUD, x, y, z, 1, 0, 0, 0, 0.1);
            }
            particleCooldown = 5;
        } else {
            particleCooldown--;
        }
    }
}