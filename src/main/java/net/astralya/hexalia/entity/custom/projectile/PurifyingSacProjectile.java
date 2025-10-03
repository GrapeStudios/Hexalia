package net.astralya.hexalia.entity.custom.projectile;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.astralya.hexalia.util.ModUtil;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class PurifyingSacProjectile extends ThrownItemEntity {

    public PurifyingSacProjectile(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public PurifyingSacProjectile(World world) {
        super(ModEntities.PURIFYING_SAC, world);
    }

    public PurifyingSacProjectile(World world, LivingEntity owner) {
        super(ModEntities.PURIFYING_SAC, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.PURIFYING_SAC;
    }

    @Override
    protected float getGravity() {
        return 0.05F;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (!getWorld().isClient()) {
            getWorld().playSound(
                    null,
                    getX(), getY(), getZ(),
                    ModSoundEvents.SAC_IMPACT,
                    SoundCategory.PLAYERS,
                    0.9F,
                    0.8F + getWorld().random.nextFloat() * 0.4F
            );

            spawnLingeringCloudAndCleanse();
            discard();
        }
    }

    private void spawnLingeringCloudAndCleanse() {
        if (getWorld().isClient()) return;

        int durationSeconds = Math.max(1, Configuration.common().tools.purifyingSacDuration);

        AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(getWorld(), getX(), getY(), getZ());
        if (getOwner() instanceof LivingEntity le) {
            cloud.setOwner(le);
        }

        cloud.setRadius(3.0F);
        cloud.setRadiusGrowth(-3.0F / (durationSeconds * 20.0F));
        cloud.setDuration(durationSeconds * 20);
        cloud.setWaitTime(0);

        cloud.setParticleType(ParticleTypes.ENTITY_EFFECT);
        cloud.setColor(0xCFE9FF); // from ARGB 0xFFCFE9FF

        float r = cloud.getRadius();
        Box box = new Box(
                getX() - r, getY() - 1.0D, getZ() - r,
                getX() + r, getY() + 1.0D, getZ() + r
        );

        List<LivingEntity> targets = getWorld().getEntitiesByClass(
                LivingEntity.class,
                box,
                LivingEntity::isAlive
        );

        for (LivingEntity target : targets) {
            double dx = target.getX() - getX();
            double dz = target.getZ() - getZ();
            if ((dx * dx + dz * dz) <= (r * r)) {
                ModUtil.removeHarmfulEffects(target);
            }
        }

        getWorld().spawnEntity(cloud);
    }
}
