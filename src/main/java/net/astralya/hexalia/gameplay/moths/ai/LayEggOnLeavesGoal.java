package net.astralya.hexalia.gameplay.moths.ai;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class LayEggOnLeavesGoal extends Goal {

    private static final int LAY_DURATION_TICKS = 40;
    private static final double ARRIVE_DISTANCE_SQR = 2.0;

    private final SilkMothEntity moth;
    private final double speed;
    private int layTicks;

    public LayEggOnLeavesGoal(SilkMothEntity moth, double speed) {
        this.moth = moth;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return !this.moth.getWorld().isClient
                && this.moth.isEggReady()
                && this.moth.getEggLayPos() != null;
    }

    @Override
    public boolean shouldContinue() {
        return this.canStart();
    }

    @Override
    public void start() {
        this.layTicks = 0;
    }

    @Override
    public void stop() {
        this.layTicks = 0;
    }

    @Override
    public void tick() {
        BlockPos target = this.moth.getEggLayPos();
        if (target == null) {
            this.moth.clearEggTarget();
            return;
        }

        this.moth.getNavigation().startMovingTo(
                target.getX() + 0.5,
                target.getY() + 0.5,
                target.getZ() + 0.5,
                this.speed
        );

        double distSqr = this.moth.getPos().squaredDistanceTo(Vec3d.ofCenter(target));
        if (distSqr > ARRIVE_DISTANCE_SQR) {
            return;
        }

        this.moth.getNavigation().stop();

        ServerWorld world = (ServerWorld) this.moth.getWorld();
        BlockPos below = target.down();
        BlockState belowState = world.getBlockState(below);

        if (!this.moth.isValidEggTarget(world, target)) {
            this.moth.clearEggTarget();
            return;
        }

        this.spawnLayingParticles(world, target, belowState);

        if (this.layTicks == 0) {
            world.playSound(null, target, SoundEvents.BLOCK_WET_GRASS_STEP, SoundCategory.NEUTRAL, 0.6F, 1.2F);
        } else if (this.layTicks % 10 == 0) {
            float pitch = 0.9F + world.getRandom().nextFloat() * 0.2F;
            world.playSound(null, target, SoundEvents.BLOCK_GRASS_STEP, SoundCategory.NEUTRAL, 0.3F, pitch);
        }

        this.layTicks++;

        if (this.layTicks >= LAY_DURATION_TICKS) {
            world.playSound(null, target, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.NEUTRAL, 0.8F, 1.0F);
            world.setBlockState(target, ModBlocks.EGG_CLUSTER.getDefaultState(), 3);
            this.moth.finishEggLaying();
        }
    }

    private void spawnLayingParticles(ServerWorld world, BlockPos target, BlockState belowState) {
        BlockStateParticleEffect option = new BlockStateParticleEffect(ParticleTypes.BLOCK, belowState);
        world.spawnParticles(
                option,
                target.getX() + 0.5,
                target.getY() + 0.1,
                target.getZ() + 0.5,
                8,
                0.25,
                0.05,
                0.25,
                0.02
        );
    }
}