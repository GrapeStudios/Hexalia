package net.grapes.hexalia.entity.ai.silkmoth;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;

import java.util.EnumSet;

public class FlyRandomlyGoal extends Goal {
    private final MobEntity entity;

    public FlyRandomlyGoal(MobEntity entity) {
        this.entity = entity;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public void start() {
        double targetX = entity.getX() + (entity.getRandom().nextDouble() * 16 - 8);
        double targetY = entity.getY() + (entity.getRandom().nextDouble() * 16 - 8);
        double targetZ = entity.getZ() + (entity.getRandom().nextDouble() * 16 - 8);
        entity.getNavigation().startMovingTo(targetX, targetY, targetZ, 1.0);
    }

    @Override
    public boolean shouldContinue() {
        return !entity.getNavigation().isIdle();
    }
}

