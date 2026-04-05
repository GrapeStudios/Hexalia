package net.astralya.hexalia.entity.custom.projectile;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.effect.cloud.SearingCloud;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraft.util.hit.HitResult;

public class SearingSacProjectile extends ThrownItemEntity {

    public SearingSacProjectile(EntityType<? extends SearingSacProjectile> entityType, World world) {
        super(entityType, world);
    }

    public SearingSacProjectile(World world) {
        super(ModEntities.SEARING_SAC, world);
    }

    public SearingSacProjectile(World world, LivingEntity owner) {
        super(ModEntities.SEARING_SAC, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SEARING_SAC;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (getWorld().isClient) {
            return;
        }

        getWorld().playSound(
                null,
                getX(),
                getY(),
                getZ(),
                ModSoundEvents.SAC_IMPACT,
                getSoundCategory(),
                0.9F,
                0.8F + getWorld().random.nextFloat() * 0.4F
        );

        int durationSeconds = Math.max(1, Configuration.SEARING_SAC_DURATION.get());
        SearingCloud cloud = new SearingCloud(getWorld(), getX(), getY(), getZ(), durationSeconds);
        if (getOwner() instanceof LivingEntity livingEntity) {
            cloud.setCloudOwner(livingEntity);
        }

        getWorld().spawnEntity(cloud);
        discard();
    }
}