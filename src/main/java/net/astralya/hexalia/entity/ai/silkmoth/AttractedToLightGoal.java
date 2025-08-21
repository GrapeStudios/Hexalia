package net.astralya.hexalia.entity.ai.silkmoth;

import net.astralya.hexalia.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.EnumSet;

public class AttractedToLightGoal extends Goal {
    private final Mob entity;
    private final double speed;
    private BlockPos targetPos;

    public AttractedToLightGoal(Mob entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public void start() {
        if (this.targetPos != null) {
            this.entity.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), this.speed);
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.entity.onGround() && this.isNearLightSource()) {
            this.entity.getNavigation().stop();
            return true;
        }
        return this.targetPos != null && !this.entity.getNavigation().isDone();
    }

    @Override
    public boolean canUse() {
        return this.findAttractingLightSource();
    }

    @Override
    public void stop() {
        this.targetPos = null;
    }

    private boolean findAttractingLightSource() {
        Level world = this.entity.level();
        BlockPos entityPos = this.entity.blockPosition();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        int entityX = entityPos.getX(), entityY = entityPos.getY(), entityZ = entityPos.getZ();

        for (int x = -10; x <= 10; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -10; z <= 10; z++) {
                    mutablePos.set(entityX + x, entityY + y, entityZ + z);
                    Block block = world.getBlockState(mutablePos).getBlock();

                    if (block.defaultBlockState().is(ModTags.Blocks.ATTRACTS_MOTH)) {
                        this.targetPos = mutablePos.immutable();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isNearLightSource() {
        return this.targetPos != null && this.entity.blockPosition().closerThan(this.targetPos, 1.5);
    }
}
