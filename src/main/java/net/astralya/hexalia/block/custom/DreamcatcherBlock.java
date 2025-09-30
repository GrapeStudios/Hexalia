package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.Configuration;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class DreamcatcherBlock extends WallMountedBlock {

    public DreamcatcherBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int rangeBlocks = Configuration.common().functional_blocks.dreamcatcherRadius;
        int igniteTicks = Configuration.common().functional_blocks.phantomIgniteDuration;

        Vec3d center = Vec3d.ofCenter(pos);
        double r = rangeBlocks;
        Box checkArea = new Box(center.x - r, center.y - r, center.z - r,
                center.x + r, center.y + r, center.z + r);

        List<PhantomEntity> phantoms = world.getEntitiesByClass(PhantomEntity.class, checkArea, e -> true);

        if (!phantoms.isEmpty()) {
            int seconds = Math.max(1, (igniteTicks + 19) / 20);
            for (PhantomEntity phantom : phantoms) {
                phantom.setOnFireFor(seconds);
            }
        }

        world.scheduleBlockTick(pos, this, 20);
    }


    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, 20);
        }
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
