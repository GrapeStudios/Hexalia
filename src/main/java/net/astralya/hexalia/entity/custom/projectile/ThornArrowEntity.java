package net.astralya.hexalia.entity.custom.projectile;

import net.astralya.hexalia.effect.ModMobEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ThornArrowEntity extends ArrowEntity {

    public ThornArrowEntity(EntityType<? extends ThornArrowEntity> type, World world) {
        super(type, world);
        this.pickupType = PickupPermission.DISALLOWED;
        this.setDamage(1.5D);
    }

    public ThornArrowEntity(EntityType<? extends ThornArrowEntity> type, World world, LivingEntity shooter) {
        super(type, world);
        this.setOwner(shooter);
        this.pickupType = PickupPermission.DISALLOWED;
        this.setPosition(shooter.getX(), shooter.getEyeY() - 0.1D, shooter.getZ());
        this.setDamage(1.5D);
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        super.setOwner(entity);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        super.onEntityHit(hit);
        if (!this.getWorld().isClient && hit.getEntity() instanceof LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(ModMobEffects.BLEEDING, 60, 0));
        }
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.ARROW);
    }
}