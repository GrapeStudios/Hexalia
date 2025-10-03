package net.astralya.hexalia.entity.ai.silkmoth;

import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.EnumSet;

public class FlyWanderGoal extends Goal {
    private final SilkMothEntity moth;
    private final double speed;
    private int cooldown;
    private static final int ATTEMPTS = 12;
    private static final double H_RANGE = 8.0;
    private static final double V_MIN = 0.5;
    private static final double V_MAX = 3.0;

    public FlyWanderGoal(SilkMothEntity moth, double speed) {
        this.moth = moth;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (moth.isLeashed() || moth.hasVehicle()) return false;
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        return moth.getNavigation().isIdle();
    }

    @Override
    public boolean shouldContinue() {
        return !moth.getNavigation().isIdle();
    }

    @Override
    public void start() {
        Vec3d target = findAirTarget();
        if (target != null) {
            moth.getNavigation().startMovingTo(target.x, target.y, target.z, this.speed);
        }
        cooldown = 12 + moth.getRandom().nextInt(12);
    }

    private Vec3d findAirTarget() {
        final World world = moth.getWorld();
        final Vec3d base = moth.getPos();
        final Random rng = moth.getRandom();

        for (int i = 0; i < ATTEMPTS; i++) {
            double dx = (rng.nextDouble() - 0.5) * 2.0 * H_RANGE;
            double dz = (rng.nextDouble() - 0.5) * 2.0 * H_RANGE;
            double dy = V_MIN + rng.nextDouble() * (V_MAX - V_MIN);

            Vec3d candidate = base.add(dx, dy, dz);

            if (!world.isChunkLoaded(BlockPos.ofFloored(candidate))) continue;

            if (!world.isAir(BlockPos.ofFloored(candidate))) continue;

            Vec3d delta = candidate.subtract(base);
            if (!world.isSpaceEmpty(moth, moth.getBoundingBox().offset(delta))) continue;

            BlockPos below = BlockPos.ofFloored(candidate).down();
            if (!world.isAir(below)) {
                candidate = candidate.add(0, 0.75, 0);
                delta = candidate.subtract(base);
                if (!world.isSpaceEmpty(moth, moth.getBoundingBox().offset(delta))) continue;
            }

            return candidate;
        }

        double dx = (rng.nextDouble() - 0.5) * 4.0;
        double dz = (rng.nextDouble() - 0.5) * 4.0;
        double dy = 0.5 + rng.nextDouble();
        return base.add(dx, dy, dz);
    }
}