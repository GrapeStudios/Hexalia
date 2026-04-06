package net.astralya.hexalia.gameplay.moths.ai;

import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;

public class HoverAroundLightGoal extends Goal {

    private static final int SCAN_COOLDOWN_MIN = 40;
    private static final int SCAN_COOLDOWN_MAX = 80;
    private static final int HOVER_REPATH_INTERVAL = 20;
    private static final int LIGHT_SEARCH_XZ = 10;
    private static final int LIGHT_SEARCH_Y = 5;
    private static final double ORBIT_RADIUS_MIN = 1.5;
    private static final double ORBIT_RADIUS_MAX = 3.0;
    private static final double HOVER_Y_MIN = 0.7;
    private static final double HOVER_Y_MAX = 1.4;
    private static final int ORBIT_DURATION_MIN_TICKS = 20 * 6;
    private static final int ORBIT_DURATION_MAX_TICKS = 20 * 14;
    private static final double ARRIVE_DIST_SQR = 1.2 * 1.2;

    private final MobEntity mob;
    private final double speed;
    private BlockPos lightPos;
    private Vec3d hoverTarget;
    private int scanCooldown;
    private int orbitTicksRemaining;
    private int repathCooldown;

    public HoverAroundLightGoal(MobEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (this.mob.getWorld().isClient()) {
            return false;
        }
        if (this.mob instanceof SilkMothEntity moth && moth.isEggReady()) {
            return false;
        }
        if (this.scanCooldown > 0) {
            this.scanCooldown--;
            return false;
        }
        this.scanCooldown = SCAN_COOLDOWN_MIN + this.mob.getRandom().nextInt(SCAN_COOLDOWN_MAX - SCAN_COOLDOWN_MIN + 1);
        return this.findClosestLight();
    }

    @Override
    public boolean shouldContinue() {
        if (this.mob.getWorld().isClient()) {
            return false;
        }
        if (this.mob instanceof SilkMothEntity moth && moth.isEggReady()) {
            return false;
        }
        if (this.lightPos == null) {
            return false;
        }
        if (this.orbitTicksRemaining <= 0) {
            return false;
        }
        BlockState state = this.mob.getWorld().getBlockState(this.lightPos);
        return state.isIn(ModTags.Blocks.ATTRACTS_MOTH);
    }

    @Override
    public void start() {
        this.orbitTicksRemaining = ORBIT_DURATION_MIN_TICKS + this.mob.getRandom().nextInt(ORBIT_DURATION_MAX_TICKS - ORBIT_DURATION_MIN_TICKS + 1);
        this.repathCooldown = 0;
        this.pickAndMoveToNextHoverTarget();
    }

    @Override
    public void stop() {
        this.lightPos = null;
        this.hoverTarget = null;
        this.orbitTicksRemaining = 0;
        this.repathCooldown = 0;
    }

    @Override
    public void tick() {
        this.orbitTicksRemaining--;
        if (this.lightPos == null) {
            return;
        }
        if (this.repathCooldown > 0) {
            this.repathCooldown--;
        }
        this.mob.getLookControl().lookAt(
                this.lightPos.getX() + 0.5,
                this.lightPos.getY() + 0.5,
                this.lightPos.getZ() + 0.5
        );
        if (this.hoverTarget == null) {
            this.pickAndMoveToNextHoverTarget();
            return;
        }
        double distSqr = this.mob.getPos().squaredDistanceTo(this.hoverTarget);
        if (distSqr <= ARRIVE_DIST_SQR) {
            this.pickAndMoveToNextHoverTarget();
            return;
        }
        if (this.mob.getNavigation().isIdle() && this.repathCooldown <= 0) {
            this.pickAndMoveToNextHoverTarget();
        }
    }

    private void pickAndMoveToNextHoverTarget() {
        World world = this.mob.getWorld();
        Vec3d target = this.pickHoverTarget(world, this.lightPos);
        if (target == null) {
            this.lightPos = null;
            return;
        }
        target = this.applySeparationBias(world, target);
        this.hoverTarget = target;
        this.repathCooldown = HOVER_REPATH_INTERVAL;
        this.mob.getNavigation().startMovingTo(this.hoverTarget.x, this.hoverTarget.y, this.hoverTarget.z, this.speed);
    }

    private boolean findClosestLight() {
        World world = this.mob.getWorld();
        BlockPos origin = this.mob.getBlockPos();
        BlockPos.Mutable cursor = new BlockPos.Mutable();
        BlockPos best = null;
        int bestDistSqr = Integer.MAX_VALUE;
        int ox = origin.getX();
        int oy = origin.getY();
        int oz = origin.getZ();
        for (int dx = -LIGHT_SEARCH_XZ; dx <= LIGHT_SEARCH_XZ; dx++) {
            for (int dy = -LIGHT_SEARCH_Y; dy <= LIGHT_SEARCH_Y; dy++) {
                for (int dz = -LIGHT_SEARCH_XZ; dz <= LIGHT_SEARCH_XZ; dz++) {
                    cursor.set(ox + dx, oy + dy, oz + dz);
                    BlockState state = world.getBlockState(cursor);
                    if (!state.isIn(ModTags.Blocks.ATTRACTS_MOTH)) {
                        continue;
                    }
                    int distSqr = dx * dx + dy * dy + dz * dz;
                    if (distSqr < bestDistSqr) {
                        bestDistSqr = distSqr;
                        best = cursor.toImmutable();
                    }
                }
            }
        }
        if (best == null) {
            return false;
        }
        this.lightPos = best;
        return true;
    }

    private Vec3d pickHoverTarget(World world, BlockPos lightPos) {
        Vec3d center = Vec3d.ofCenter(lightPos);
        for (int attempt = 0; attempt < 10; attempt++) {
            float angle = this.mob.getRandom().nextFloat() * MathHelper.TAU;
            double radius = ORBIT_RADIUS_MIN + this.mob.getRandom().nextDouble() * (ORBIT_RADIUS_MAX - ORBIT_RADIUS_MIN);
            double y = HOVER_Y_MIN + this.mob.getRandom().nextDouble() * (HOVER_Y_MAX - HOVER_Y_MIN);
            double x = center.x + MathHelper.cos(angle) * radius;
            double z = center.z + MathHelper.sin(angle) * radius;
            Vec3d candidate = new Vec3d(x, center.y + y, z);
            BlockPos pos = BlockPos.ofFloored(candidate);
            if (!world.getBlockState(pos).isAir()) {
                continue;
            }
            if (!world.getBlockState(pos.up()).isAir()) {
                continue;
            }
            return candidate;
        }
        return null;
    }

    private Vec3d applySeparationBias(World world, Vec3d target) {
        Box box = this.mob.getBoundingBox().expand(2.5);
        List<MobEntity> nearby = world.getEntitiesByClass(MobEntity.class, box, e -> e != this.mob && e.getType() == this.mob.getType());
        if (nearby.isEmpty()) {
            return target;
        }
        Vec3d away = Vec3d.ZERO;
        for (MobEntity other : nearby) {
            double distSqr = this.mob.getPos().squaredDistanceTo(other.getPos());
            if (distSqr <= 0.0001) {
                continue;
            }
            if (distSqr > 1.6 * 1.6) {
                continue;
            }
            Vec3d delta = this.mob.getPos().subtract(other.getPos());
            Vec3d norm = delta.normalize();
            away = away.add(norm);
        }
        if (away.lengthSquared() <= 0.0001) {
            return target;
        }
        Vec3d bias = away.normalize().multiply(0.6);
        return new Vec3d(target.x + bias.x, target.y, target.z + bias.z);
    }
}