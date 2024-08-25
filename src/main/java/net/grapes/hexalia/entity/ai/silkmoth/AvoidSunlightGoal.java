package net.grapes.hexalia.entity.ai.silkmoth;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.EnumSet;

public class AvoidSunlightGoal extends Goal {
    private final MobEntity entity;
    private final World world;

    public AvoidSunlightGoal(MobEntity entity) {
        this.entity = entity;
        this.world = entity.getWorld();
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return this.world.isDay() && this.isInSunlight();
    }

    @Override
    public void start() {
        BlockPos targetPos = this.getShadePosition();
        if (targetPos != null) {
            double targetX = targetPos.getX();
            double targetY = targetPos.getY();
            double targetZ = targetPos.getZ();
            this.entity.getNavigation().startMovingTo(targetX, targetY, targetZ, 1.0);
        }
    }

    @Override
    public boolean shouldContinue() {
        // Stay put if the moth is in a shaded area
        if (this.entity.isOnGround() && this.isInShade()) {
            this.entity.getNavigation().stop();
            return true;
        }
        return !this.entity.getNavigation().isIdle();
    }

    private boolean isInShade() {
        BlockPos blockPos = this.entity.getBlockPos();
        return !this.world.isSkyVisible(blockPos) && this.world.getBlockState(blockPos.down()).isOpaque();
    }

    @Override
    public void tick() {
        if (this.world.isDay() && this.entity.getNavigation().isIdle()) {
            BlockPos targetPos = this.getShadePosition();
            if (targetPos != null) {
                this.entity.getNavigation().startMovingTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1.0);
            }
        }
    }

    private boolean isInSunlight() {
        BlockPos blockPos = this.entity.getBlockPos();
        return this.world.isSkyVisible(blockPos);
    }

    private BlockPos getShadePosition() {
        BlockPos entityPos = this.entity.getBlockPos();
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int i = 0; i < 10; i++) {
            int randomX = entityPos.getX() + MathHelper.nextInt(this.entity.getRandom(), -10, 10);
            int randomY = entityPos.getY() + MathHelper.nextInt(this.entity.getRandom(), -5, 5);
            int randomZ = entityPos.getZ() + MathHelper.nextInt(this.entity.getRandom(), -10, 10);

            mutablePos.set(randomX, randomY, randomZ);
            if (!this.world.isSkyVisible(mutablePos) && this.world.getBlockState(mutablePos.down()).isOpaque()) {
                return mutablePos.toImmutable();
            }
        }
        return null; // No suitable shade found
    }

    @Override
    public boolean canStop() {
        return !this.world.isDay();
    }
}
