package net.astralya.hexalia.entity.custom.projectile;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.gameplay.cloud.SearingCloud;
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

public class SearingSacProjectile extends ThrownItemEntity {

    public SearingSacProjectile(EntityType<? extends SearingSacProjectile> type, World world) {
        super(type, world);
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
    protected double getGravity() {
        return 0.05D;
    }

    @Override
    protected void onCollision(HitResult hit) {
        super.onCollision(hit);
        if (!this.getWorld().isClient) {
            this.getWorld().playSound(
                    null, this.getX(), this.getY(), this.getZ(),
                    ModSoundEvents.SAC_IMPACT, SoundCategory.PLAYERS,
                    0.9F, 0.8F + this.getWorld().getRandom().nextFloat() * 0.4F
            );
            int durationSeconds = Math.max(1, Configuration.SEARING_SAC_DURATION.get());
            SearingCloud cloud = new SearingCloud(this.getWorld(), this.getX(), this.getY(), this.getZ(), durationSeconds);
            if (this.getOwner() instanceof LivingEntity le) {
                cloud.setCloudOwner(le);
            }
            this.getWorld().spawnEntity(cloud);
            this.discard();
        }
    }
}