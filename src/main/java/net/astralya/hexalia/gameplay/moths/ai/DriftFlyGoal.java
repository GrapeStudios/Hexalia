package net.astralya.hexalia.gameplay.moths.ai;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class DriftFlyGoal extends Goal {

    private static final int COOLDOWN_MIN = 40;
    private static final int COOLDOWN_MAX = 120;
    private static final int RANGE_XZ = 5;
    private static final int RANGE_Y = 2;

    private final MobEntity mob;
    private final double speed;
    private int cooldown;

    public DriftFlyGoal(MobEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (this.mob.getWorld().isClient()) {
            return false;
        }
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }
        if (!this.mob.getNavigation().isIdle()) {
            return false;
        }
        return this.mob.getRandom().nextInt(4) == 0;
    }

    @Override
    public void start() {
        World world = this.mob.getWorld();
        BlockPos origin = this.mob.getBlockPos();
        BlockPos target = this.pickAirTarget(world, origin);
        this.cooldown = COOLDOWN_MIN + this.mob.getRandom().nextInt(COOLDOWN_MAX - COOLDOWN_MIN + 1);
        if (target == null) {
            return;
        }
        this.mob.getNavigation().startMovingTo(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, this.speed);
    }

    private BlockPos pickAirTarget(World world, BlockPos origin) {
        for (int attempt = 0; attempt < 12; attempt++) {
            int dx = this.mob.getRandom().nextInt(RANGE_XZ * 2 + 1) - RANGE_XZ;
            int dy = this.mob.getRandom().nextInt(RANGE_Y * 2 + 1) - RANGE_Y;
            int dz = this.mob.getRandom().nextInt(RANGE_XZ * 2 + 1) - RANGE_XZ;
            BlockPos pos = origin.add(dx, dy, dz);
            if (!world.getBlockState(pos).isAir()) {
                continue;
            }
            if (!world.getBlockState(pos.up()).isAir()) {
                continue;
            }
            return pos;
        }
        return null;
    }
}