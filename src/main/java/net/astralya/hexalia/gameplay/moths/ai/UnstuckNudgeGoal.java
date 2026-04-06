package net.astralya.hexalia.gameplay.moths.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class UnstuckNudgeGoal extends Goal {

    private static final int CHECK_INTERVAL = 5;
    private static final int STUCK_TICKS_THRESHOLD = 40;

    private final MobEntity mob;
    private Vec3d lastPos;
    private int stuckTicks;

    public UnstuckNudgeGoal(MobEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.noneOf(Control.class));
    }

    @Override
    public boolean canStart() {
        return !this.mob.getWorld().isClient();
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.getWorld().isClient();
    }

    @Override
    public void start() {
        this.lastPos = this.mob.getPos();
        this.stuckTicks = 0;
    }

    @Override
    public void tick() {
        if (this.mob.age % CHECK_INTERVAL != 0) {
            return;
        }
        if (this.mob.getNavigation().isIdle()) {
            this.lastPos = this.mob.getPos();
            this.stuckTicks = 0;
            return;
        }
        Vec3d now = this.mob.getPos();
        double movedSqr = now.squaredDistanceTo(this.lastPos);
        if (movedSqr < 0.0025) {
            this.stuckTicks += CHECK_INTERVAL;
        } else {
            this.stuckTicks = 0;
            this.lastPos = now;
        }
        if (this.stuckTicks < STUCK_TICKS_THRESHOLD) {
            return;
        }
        this.stuckTicks = 0;
        this.lastPos = now;
        this.mob.getNavigation().stop();
        double dx = (this.mob.getRandom().nextDouble() - 0.5) * 0.35;
        double dz = (this.mob.getRandom().nextDouble() - 0.5) * 0.35;
        Vec3d current = this.mob.getVelocity();
        this.mob.setVelocity(current.x + dx, Math.max(current.y, 0.18), current.z + dz);
        this.mob.velocityDirty = true;
    }
}