package net.astralya.hexalia.gameplay.cacofey.ai;

import net.astralya.hexalia.entity.custom.CacofeyEntity;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;

public class CacofeyStealGoal extends Goal {

    private enum Phase {
        SCAN,
        APPROACH,
        STEAL,
        FLEE,
        CONSUME
    }

    private static final double SCAN_RADIUS = 12.0;
    private static final double STEAL_RADIUS = 1.8;
    private static final int SCAN_INTERVAL = 40;
    private static final int FLEE_TICKS = 80;
    private static final int STEAL_COOLDOWN = 1200;
    private static final float APPROACH_SPEED = 1.0F;
    private static final float FLEE_SPEED = 2.4F;

    private final CacofeyEntity cacofey;
    private Phase phase = Phase.SCAN;
    private PlayerEntity target;
    private int phaseTimer = 0;
    private int scanTimer = 0;
    private double fleeOriginY = 0;

    public CacofeyStealGoal(CacofeyEntity cacofey) {
        this.cacofey = cacofey;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (cacofey.isTamed()) return false;
        if (cacofey.stealCooldown > 0) return false;
        if (phase != Phase.SCAN) return true;
        if (scanTimer++ < SCAN_INTERVAL) return false;
        scanTimer = 0;
        if (findTarget()) {
            phase = Phase.APPROACH;
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        if (cacofey.isTamed()) return false;
        if (phase == Phase.FLEE || phase == Phase.CONSUME) return true;
        return target != null && target.isAlive() && !target.isCreative();
    }

    @Override
    public void start() {
        phaseTimer = 0;
        cacofey.setInspecting(true);
        cacofey.getWorld().playSound(
                null,
                cacofey.getX(),
                cacofey.getY(),
                cacofey.getZ(),
                ModSoundEvents.CACOFEY_GIGGLE,
                SoundCategory.NEUTRAL,
                0.6F,
                1.1F + cacofey.getRandom().nextFloat() * 0.2F
        );
    }

    @Override
    public void stop() {
        cacofey.setInspecting(false);
        phase = Phase.SCAN;
        target = null;
        phaseTimer = 0;
        scanTimer = 0;
    }

    @Override
    public void tick() {
        switch (phase) {
            case APPROACH -> tickApproach();
            case STEAL -> tickSteal();
            case FLEE -> tickFlee();
            case CONSUME -> tickConsume();
            default -> {
            }
        }
    }

    private void tickApproach() {
        if (target == null || !target.isAlive()) {
            reset();
            return;
        }

        cacofey.getLookControl().lookAt(target, 30.0F, 30.0F);
        cacofey.getNavigation().startMovingTo(target, APPROACH_SPEED);

        if (cacofey.distanceTo(target) <= STEAL_RADIUS) {
            cacofey.setInspecting(false);
            cacofey.getNavigation().stop();
            phase = Phase.STEAL;
            phaseTimer = 0;
        }
    }

    private void tickSteal() {
        if (target == null || !target.isAlive()) {
            reset();
            return;
        }

        ItemStack stolen = findEdibleItem(target);
        if (stolen.isEmpty()) {
            reset();
            return;
        }

        ItemStack display = stolen.copy();
        display.setCount(1);
        stolen.decrement(1);
        cacofey.setHeldItem(display);
        fleeOriginY = cacofey.getY();
        phase = Phase.FLEE;
        phaseTimer = 0;
    }

    private void tickFlee() {
        Vec3d awayDir = cacofey.getPos().subtract(target != null ? target.getPos() : cacofey.getPos()).multiply(1.0, 0.0, 1.0).normalize();
        if (awayDir.lengthSquared() < 0.001) {
            awayDir = new Vec3d(1.0, 0.0, 0.0);
        }

        double clampedY = Math.min(cacofey.getY() + 1.0, fleeOriginY + 4.0);
        Vec3d fleeTarget = new Vec3d(
                cacofey.getX() + awayDir.x * 8.0,
                clampedY,
                cacofey.getZ() + awayDir.z * 8.0
        );

        cacofey.getNavigation().startMovingTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, FLEE_SPEED);

        if (++phaseTimer >= FLEE_TICKS) {
            cacofey.getNavigation().stop();
            phase = Phase.CONSUME;
            phaseTimer = 0;
        }
    }

    private void tickConsume() {
        if (++phaseTimer >= 20) {
            cacofey.setHeldItem(ItemStack.EMPTY);
            cacofey.stealCooldown = STEAL_COOLDOWN;
            phase = Phase.SCAN;
            target = null;
            phaseTimer = 0;
        }
    }

    private boolean findTarget() {
        List<PlayerEntity> players = cacofey.getWorld().getEntitiesByClass(
                PlayerEntity.class,
                cacofey.getBoundingBox().expand(SCAN_RADIUS),
                player -> !player.isCreative() && !player.isSpectator() && hasEdibleItem(player)
        );

        if (players.isEmpty()) return false;

        players.sort((a, b) -> Double.compare(cacofey.squaredDistanceTo(a), cacofey.squaredDistanceTo(b)));
        target = players.get(0);
        return true;
    }

    private void reset() {
        cacofey.setInspecting(false);
        phase = Phase.SCAN;
        target = null;
        phaseTimer = 0;
    }

    private static boolean hasEdibleItem(PlayerEntity player) {
        return !findEdibleItem(player).isEmpty();
    }

    private static ItemStack findEdibleItem(PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            if (stack.isOf(ModItems.GALEBERRIES_COOKIE)) continue;
            if (stack.isFood()) return stack;
        }
        return ItemStack.EMPTY;
    }
}