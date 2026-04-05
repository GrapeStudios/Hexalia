package net.astralya.hexalia.effect.cloud;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;

public class FrostCloud extends AreaEffectCloudEntity {

    private static final int FREEZE_INCREMENT_PER_TICK = 5;
    private static final int HOLD_SECONDS = 2;

    private final SacCloudHelper.HoldShrinkPlan holdPlan;
    private final boolean[] shrinkStarted = new boolean[]{false};
    private int ageTicks;

    public FrostCloud(World world, double x, double y, double z, int durationSeconds) {
        super(EntityType.AREA_EFFECT_CLOUD, world);
        this.setPosition(x, y, z);
        this.holdPlan = SacCloudHelper.configureWithHold(this, durationSeconds, HOLD_SECONDS, 3.0F, 0x9FD9FF);
        this.addEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 1, false, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            return;
        }

        this.ageTicks++;
        SacCloudHelper.startShrinkIfReady(this, this.ageTicks, this.holdPlan, this.shrinkStarted);
        SacCloudHelper.forEachLivingInRadius(this, this::applyFreeze);
    }

    private void applyFreeze(LivingEntity target) {
        if (!target.canFreeze()) {
            return;
        }

        int required = target.getMinFreezeDamageTicks();
        int current = target.getFrozenTicks();
        if (current < required) {
            target.setFrozenTicks(Math.min(required, current + FREEZE_INCREMENT_PER_TICK));
        }
    }

    public void setCloudOwner(LivingEntity owner) {
        this.setOwner(owner);
    }
}