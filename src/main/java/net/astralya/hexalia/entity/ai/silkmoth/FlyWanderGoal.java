package net.astralya.hexalia.entity.ai.silkmoth;

import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (moth.isLeashed() || moth.isPassenger()) return false;
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        return moth.getNavigation().isDone();
    }

    @Override
    public boolean canContinueToUse() {
        return !moth.getNavigation().isDone();
    }

    @Override
    public void start() {
        Vec3 target = findAirTarget();
        if (target != null) {
            moth.getNavigation().moveTo(target.x, target.y, target.z, this.speed);
        }
        cooldown = 12 + moth.getRandom().nextInt(12);
    }

    private Vec3 findAirTarget() {
        Level level = moth.level();
        Vec3 base = moth.position();
        RandomSource rng = moth.getRandom();

        for (int i = 0; i < ATTEMPTS; i++) {
            double dx = (rng.nextDouble() - 0.5) * 2.0 * H_RANGE;
            double dz = (rng.nextDouble() - 0.5) * 2.0 * H_RANGE;
            double dy = V_MIN + rng.nextDouble() * (V_MAX - V_MIN);

            Vec3 candidate = base.add(dx, dy, dz);
            BlockPos pos = BlockPos.containing(candidate);

            if (!level.isLoaded(pos)) continue;
            if (!level.getBlockState(pos).isAir()) continue;

            Vec3 delta = candidate.subtract(base);
            AABB movedBox = moth.getBoundingBox().move(delta);
            if (!level.noCollision(moth, movedBox)) continue;

            BlockPos below = pos.below();
            if (!level.getBlockState(below).isAir()) {
                candidate = candidate.add(0.0, 0.75, 0.0);
                delta = candidate.subtract(base);
                movedBox = moth.getBoundingBox().move(delta);
                if (!level.noCollision(moth, movedBox)) continue;
            }

            return candidate;
        }

        double dx = (rng.nextDouble() - 0.5) * 4.0;
        double dz = (rng.nextDouble() - 0.5) * 4.0;
        double dy = 0.5 + rng.nextDouble();
        return base.add(dx, dy, dz);
    }
}
