package net.astralya.hexalia.entity.custom.projectile;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.effect.cloud.FrostCloud;
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

public class FrostSacProjectile extends ThrownItemEntity {

    public FrostSacProjectile(EntityType<? extends FrostSacProjectile> type, World world) {
        super(type, world);
    }

    public FrostSacProjectile(World world) {
        super(ModEntities.FROST_SAC, world);
    }

    public FrostSacProjectile(World world, LivingEntity owner) {
        super(ModEntities.FROST_SAC, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.FROST_SAC;
    }

    @Override
    protected double getGravity() {
        return 0.05D;
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

            int durationSeconds = Math.max(1, Configuration.FROST_SAC_DURATION.get());
            FrostCloud cloud = new FrostCloud(getWorld(), getX(), getY(), getZ(), durationSeconds);

            if (getOwner() instanceof LivingEntity le) {
                cloud.setCloudOwner(le);
            }

            getWorld().spawnEntity(cloud);
            discard();
        }
    }
}
