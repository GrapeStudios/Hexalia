package net.astralya.hexalia.effect.cloud;

import net.astralya.hexalia.effect.ModMobEffects;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.world.World;

public class SearingCloud extends AreaEffectCloudEntity {

    private static final float DAMAGE_PER_SECOND = 0.5F;
    private static final int FIRE_SECONDS_PER_PULSE = 3;
    private static final int HOLD_SECONDS = 2;

    private final SacCloudHelper.HoldShrinkPlan holdPlan;
    private final boolean[] shrinkStarted = new boolean[]{ false };
    private int ageTicks = 0;
    private int tickCounter = 0;

    public SearingCloud(World world, double x, double y, double z, int durationSeconds) {
        super(EntityType.AREA_EFFECT_CLOUD, world);
        this.setPosition(x, y, z);
        this.holdPlan = SacCloudHelper.configureWithHold(this, durationSeconds, HOLD_SECONDS, 3.0F, 0xE85A2A);
        this.addEffect(new StatusEffectInstance(ModMobEffects.BLEEDING, 200, 0, false, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) return;
        ageTicks++;
        SacCloudHelper.startShrinkIfReady(this, ageTicks, holdPlan, shrinkStarted);
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            pulse();
        }
    }

    private void pulse() {
        SacCloudHelper.forEachLivingInRadius(this, target -> {
            SacCloudHelper.damageMagic(this, target, DAMAGE_PER_SECOND);
            target.setOnFireFor(FIRE_SECONDS_PER_PULSE);
        });
    }

    public void setCloudOwner(LivingEntity owner) {
        this.setOwner(owner);
    }
}