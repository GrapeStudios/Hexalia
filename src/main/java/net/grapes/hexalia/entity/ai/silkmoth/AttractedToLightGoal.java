package net.grapes.hexalia.entity.ai.silkmoth;

import net.grapes.hexalia.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class AttractedToLightGoal extends Goal {
    private final MobEntity entity;
    private final double speed;
    private BlockPos targetPos;

    public AttractedToLightGoal(MobEntity entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return this.findAttractingLightSource();
    }

    @Override
    public void start() {
        if (this.targetPos != null) {
            this.entity.getNavigation().startMovingTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), this.speed);
        }
    }

    @Override
    public boolean shouldContinue() {
        // Stay put if the moth is near the light source and is on the ground
        if (this.entity.isOnGround() && this.isNearLightSource()) {
            this.entity.getNavigation().stop();
            return true;
        }
        return this.targetPos != null && !this.entity.getNavigation().isIdle();
    }

    private boolean isNearLightSource() {
        return this.targetPos != null && this.entity.getBlockPos().isWithinDistance(this.targetPos, 1.5);
    }

    @Override
    public void stop() {
        this.targetPos = null;
    }

    private boolean findAttractingLightSource() {
        World world = this.entity.getWorld();
        BlockPos entityPos = this.entity.getBlockPos();
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int x = -10; x <= 10; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -10; z <= 10; z++) {
                    mutablePos.set(entityPos.getX() + x, entityPos.getY() + y, entityPos.getZ() + z);
                    Block block = world.getBlockState(mutablePos).getBlock();
                    if (block.getDefaultState().isIn(ModTags.Blocks.ATTRACTS_MOTH)) {
                        this.targetPos = mutablePos.toImmutable();
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
