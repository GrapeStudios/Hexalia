package net.astralya.hexalia.entity.custom.projectile;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.gameplay.cloud.FoulCloud;
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

public class FoulSacProjectile extends ThrownItemEntity {

    public FoulSacProjectile(EntityType<? extends FoulSacProjectile> type, World world) {
        super(type, world);
    }

    public FoulSacProjectile(World world) {
        super(ModEntities.FOUL_SAC, world);
    }

    public FoulSacProjectile(World world, LivingEntity owner) {
        super(ModEntities.FOUL_SAC, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.FOUL_SAC;
    }

    @Override
    protected double getGravity() {
        return 0.05D;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (!this.getWorld().isClient()) {
            this.getWorld().playSound(
                    null,
                    this.getX(), this.getY(), this.getZ(),
                    ModSoundEvents.SAC_IMPACT,
                    SoundCategory.PLAYERS,
                    0.9F,
                    0.8F + this.getWorld().random.nextFloat() * 0.4F
            );

            int durationSeconds = Math.max(1, Configuration.FOUL_SAC_DURATION.get());
            FoulCloud cloud = new FoulCloud(this.getWorld(), this.getX(), this.getY(), this.getZ(), durationSeconds);

            if (this.getOwner() instanceof LivingEntity le) {
                cloud.setCloudOwner(le);
            }

            this.getWorld().spawnEntity(cloud);
            this.discard();
        }
    }
}