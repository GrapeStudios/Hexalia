package net.astralya.hexalia.entity.custom.projectile;

import net.astralya.hexalia.effect.ModMobEffects;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class RabbageProjectile extends ThrownItemEntity {

    public RabbageProjectile(EntityType<? extends ThrownItemEntity> type, World world) {
        super(type, world);
    }

    public RabbageProjectile(World world) {
        super(ModEntities.RABBAGE, world);
    }

    public RabbageProjectile(World world, LivingEntity owner) {
        super(ModEntities.RABBAGE, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.RABBAGE;
    }

    private ParticleEffect getParticle() {
        ItemStack stack = new ItemStack(ModItems.RABBAGE);
        return new ItemStackParticleEffect(ParticleTypes.ITEM, stack);
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleEffect effect = this.getParticle();
            for (int i = 0; i < 8; i++) {
                this.getWorld().addParticle(effect, this.getX(), this.getY(), this.getZ(),
                        0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        super.onEntityHit(hit);
        Entity target = hit.getEntity();
        if (target instanceof LivingEntity living) {
            int damage = 1;
            target.damage(this.getDamageSources().thrown(this, this.getOwner()), damage);
            living.addStatusEffect(new StatusEffectInstance(ModMobEffects.BLEEDING, 100, 0));
        }
    }

    @Override
    protected void onCollision(HitResult hit) {
        super.onCollision(hit);
        if (!this.getWorld().isClient()) {
            this.getWorld().sendEntityStatus(this, (byte) 3);
            this.discard();
        }
    }
}