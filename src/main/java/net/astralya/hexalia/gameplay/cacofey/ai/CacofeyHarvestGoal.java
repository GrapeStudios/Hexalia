package net.astralya.hexalia.gameplay.cacofey.ai;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.entity.custom.CacofeyEntity;
import net.astralya.hexalia.entity.custom.CacofeyMode;
import net.astralya.hexalia.particle.ModParticleType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class CacofeyHarvestGoal extends Goal {

    private enum Phase { IDLE, MOVING_TO_CROP, HARVESTING, MOVING_TO_CONTAINER, DEPOSITING }

    private static final double ARRIVE_DISTANCE = 2.0;
    private static final float  TRAVEL_SPEED    = 1.2F;
    private static final int    SCAN_COOLDOWN   = 40;

    private final CacofeyEntity cacofey;
    private Phase    phase     = Phase.IDLE;
    private BlockPos cropPos   = null;
    private int      scanTimer = 0;

    public CacofeyHarvestGoal(CacofeyEntity cacofey) {
        this.cacofey = cacofey;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (!cacofey.isTamed()) return false;
        if (cacofey.getMode() != CacofeyMode.WANDER) return false;
        if (cacofey.getAnchorPos() == null) return false;
        if (phase != Phase.IDLE) return true;
        if (scanTimer > 0) { scanTimer--; return false; }
        return findMatureCrop();
    }

    @Override
    public boolean shouldContinue() {
        if (cacofey.getMode() != CacofeyMode.WANDER) return false;
        if (cacofey.getAnchorPos() == null) return false;
        return phase != Phase.IDLE;
    }

    @Override
    public void start() {
        phase = Phase.MOVING_TO_CROP;
        navigateToCrop();
    }

    @Override
    public void stop() {
        cacofey.getNavigation().stop();
        phase = Phase.IDLE;
        cropPos = null;
        scanTimer = SCAN_COOLDOWN;
    }

    @Override
    public void tick() {
        switch (phase) {
            case MOVING_TO_CROP      -> tickMoveToCrop();
            case HARVESTING          -> tickHarvest();
            case MOVING_TO_CONTAINER -> tickMoveToContainer();
            case DEPOSITING          -> tickDeposit();
            default                  -> {}
        }
    }

    private void tickMoveToCrop() {
        if (cropPos == null || !isMatureCrop(cacofey.getWorld().getBlockState(cropPos))) {
            phase = Phase.IDLE;
            return;
        }
        cacofey.getLookControl().lookAt(cropPos.getX() + 0.5, cropPos.getY() + 0.5, cropPos.getZ() + 0.5);
        if (distanceToCrop() <= ARRIVE_DISTANCE) {
            cacofey.getNavigation().stop();
            phase = Phase.HARVESTING;
        }
    }

    private void tickHarvest() {
        if (!(cacofey.getWorld() instanceof ServerWorld serverWorld)) return;
        BlockState state = serverWorld.getBlockState(cropPos);
        if (!isMatureCrop(state)) {
            phase = Phase.IDLE;
            return;
        }
        List<ItemStack> drops = Block.getDroppedStacks(state, serverWorld, cropPos, serverWorld.getBlockEntity(cropPos), null, ItemStack.EMPTY);
        ItemStack harvest = drops.stream().filter(s -> !s.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
        if (!harvest.isEmpty()) {
            cacofey.setHeldItem(harvest.copyWithCount(1));
        }
        resetCropAge(serverWorld, cropPos, state);
        spawnHarvestParticles(serverWorld, cropPos);
        phase = Phase.MOVING_TO_CONTAINER;
        navigateToContainer();
    }

    private void tickMoveToContainer() {
        BlockPos anchor = cacofey.getAnchorPos();
        cacofey.getLookControl().lookAt(cropPos.getX() + 0.5, cropPos.getY() + 0.5, cropPos.getZ() + 0.5);
        if (cacofey.squaredDistanceTo(Vec3d.ofCenter(anchor.up())) <= ARRIVE_DISTANCE * ARRIVE_DISTANCE) {
            cacofey.getNavigation().stop();
            phase = Phase.DEPOSITING;
        }
    }

    private void tickDeposit() {
        if (!(cacofey.getWorld() instanceof ServerWorld)) return;
        BlockPos anchor = cacofey.getAnchorPos();
        BlockEntity be = cacofey.getWorld().getBlockEntity(anchor);
        if (be instanceof Inventory container) {
            ItemStack held = cacofey.getHeldItem();
            if (!held.isEmpty()) {
                insertIntoContainer(container, held);
            }
        }
        cacofey.setHeldItem(ItemStack.EMPTY);
        phase = Phase.IDLE;
        scanTimer = SCAN_COOLDOWN;
    }

    private boolean findMatureCrop() {
        BlockPos anchor = cacofey.getAnchorPos();
        int radius = Configuration.CACOFEY_HARVEST_RADIUS.get();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) continue;
                for (int dy = -3; dy <= 3; dy++) {
                    BlockPos candidate = anchor.add(dx, dy, dz);
                    if (isMatureCrop(cacofey.getWorld().getBlockState(candidate))) {
                        cropPos = candidate;
                        return true;
                    }
                }
            }
        }
        scanTimer = SCAN_COOLDOWN;
        return false;
    }

    private static boolean isMatureCrop(BlockState state) {
        for (var property : state.getProperties()) {
            if (property.getName().equals("age") && property instanceof IntProperty intProp) {
                int maxAge = Collections.max(intProp.getValues());
                return state.get(intProp) == maxAge;
            }
        }
        return false;
    }

    private static void resetCropAge(ServerWorld world, BlockPos pos, BlockState state) {
        for (var property : state.getProperties()) {
            if (property.getName().equals("age") && property instanceof IntProperty intProp) {
                world.setBlockState(pos, state.with(intProp, 0), 3);
                return;
            }
        }
    }

    private static void insertIntoContainer(Inventory container, ItemStack stack) {
        for (int i = 0; i < container.size(); i++) {
            ItemStack slot = container.getStack(i);
            if (slot.isEmpty()) {
                container.setStack(i, stack.copy());
                container.markDirty();
                return;
            }
            if (ItemStack.areItemsAndComponentsEqual(slot, stack) && slot.getCount() < slot.getMaxCount()) {
                slot.increment(stack.getCount());
                container.markDirty();
                return;
            }
        }
    }

    private void navigateToCrop() {
        if (cropPos != null) {
            cacofey.getNavigation().startMovingTo(cropPos.getX() + 0.5, cropPos.getY() + 1.5, cropPos.getZ() + 0.5, TRAVEL_SPEED);
        }
    }

    private void navigateToContainer() {
        BlockPos anchor = cacofey.getAnchorPos();
        if (anchor != null) {
            cacofey.getNavigation().startMovingTo(anchor.getX() + 0.5, anchor.getY() + 1.5, anchor.getZ() + 0.5, TRAVEL_SPEED);
        }
    }

    private double distanceToCrop() {
        return cacofey.getPos().distanceTo(Vec3d.ofCenter(cropPos.up()));
    }

    private static void spawnHarvestParticles(ServerWorld world, BlockPos pos) {
        world.spawnParticles(
                ModParticleType.CACOFEY_DUST,
                pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                12,
                0.3, 0.2, 0.3,
                0.012
        );
        world.spawnParticles(
                ModParticleType.CACOFEY_DUST_HELD,
                pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                6,
                0.2, 0.15, 0.2,
                0.018
        );
    }
}
