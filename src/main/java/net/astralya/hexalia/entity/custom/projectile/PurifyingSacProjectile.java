package net.astralya.hexalia.entity.custom.projectile;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.gameplay.cloud.CleansingCloud;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

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

            int durationSeconds = Math.max(1, Configuration.PURIFYING_SAC_DURATION.get());
            CleansingCloud cloud = new CleansingCloud(this.getWorld(), this.getX(), this.getY(), this.getZ(), durationSeconds);

            if (this.getOwner() instanceof LivingEntity le) {
                cloud.setCloudOwner(le);
            }

            this.getWorld().spawnEntity(cloud);
            this.discard();
        }
    }
}