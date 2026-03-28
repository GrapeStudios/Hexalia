package net.astralya.hexalia.gameplay.cacofey.ai;

import net.astralya.hexalia.entity.custom.CacofeyEntity;
import net.astralya.hexalia.entity.custom.CacofeyMode;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class CacofeyAnchorHoverGoal extends Goal {

    private static final int HOVER_INTERVAL = 60;
    private static final int HOVER_RADIUS   = 5;
    private static final int HOVER_Y_OFFSET = 3;

    private final CacofeyEntity cacofey;
    private int hoverTimer = 0;

    public CacofeyAnchorHoverGoal(CacofeyEntity cacofey) {
        this.cacofey = cacofey;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return cacofey.isTamed()
                && cacofey.getMode() == CacofeyMode.WANDER
                && cacofey.getAnchorPos() != null;
    }

    @Override
    public boolean shouldContinue() {
        return canStart();
    }

    @Override
    public void tick() {
        if (++hoverTimer < HOVER_INTERVAL) return;
        hoverTimer = 0;
        BlockPos anchor = cacofey.getAnchorPos();
        double targetX = anchor.getX() + cacofey.getRandom().nextBetween(-HOVER_RADIUS, HOVER_RADIUS);
        double targetY = anchor.getY() + HOVER_Y_OFFSET + cacofey.getRandom().nextInt(2);
        double targetZ = anchor.getZ() + cacofey.getRandom().nextBetween(-HOVER_RADIUS, HOVER_RADIUS);
        cacofey.getNavigation().startMovingTo(targetX, targetY, targetZ, 0.8D);
    }

    @Override
    public void stop() {
        cacofey.getNavigation().stop();
        hoverTimer = 0;
    }
}
