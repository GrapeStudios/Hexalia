package net.astralya.hexalia.entity.custom.projectile;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.astralya.hexalia.util.ModUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class PurifyingSacProjectile extends ThrowableItemProjectile {

    public PurifyingSacProjectile(EntityType<? extends PurifyingSacProjectile> type, Level level) {
        super(type, level);
    }

    public PurifyingSacProjectile(Level level) {
        super(ModEntities.PURIFYING_SAC.get(), level);
    }

    public PurifyingSacProjectile(Level level, LivingEntity owner) {
        super(ModEntities.PURIFYING_SAC.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.PURIFYING_SAC.get();
    }

    @Override
    protected float getGravity() {
        return 0.05F;
    }

    @Override
    protected void onHit(HitResult hit) {
        super.onHit(hit);
        if (!this.level().isClientSide) {
            this.level().playSound(
                    null, this.getX(), this.getY(), this.getZ(),
                    ModSoundEvents.SAC_IMPACT.get(), SoundSource.PLAYERS,
                    0.9F, 0.8F + this.level().getRandom().nextFloat() * 0.4F
            );
            spawnLingeringCloudAndCleanse();
            this.discard();
        }
    }

    private void spawnLingeringCloudAndCleanse() {
        if (this.level().isClientSide) return;

        int durationSeconds = Math.max(1, Configuration.PURIFYING_SAC_DURATION.get());

        AreaEffectCloud cloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
        if (this.getOwner() instanceof LivingEntity le) {
            cloud.setOwner(le);
        }
        cloud.setRadius(3.0F);
        cloud.setRadiusPerTick(-3.0F / (durationSeconds * 20.0F));
        cloud.setDuration(durationSeconds * 20);
        cloud.setWaitTime(0);

        cloud.setParticle(ParticleTypes.ENTITY_EFFECT);
        cloud.setFixedColor(0xCFE9FF);

        float r = cloud.getRadius();
        AABB aabb = new AABB(
                this.getX() - r, this.getY() - 1.0D, this.getZ() - r,
                this.getX() + r, this.getY() + 1.0D, this.getZ() + r
        );

        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
        for (LivingEntity target : targets) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            if ((dx * dx + dz * dz) <= (r * r)) {
                ModUtil.removeHarmfulEffects(target);
            }
        }

        this.level().addFreshEntity(cloud);
    }
}