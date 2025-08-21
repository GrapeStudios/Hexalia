package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.Configuration;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DreamcatcherBlock extends HexaliaWallBlock {

    public DreamcatcherBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        int range = Configuration.get().phantomRadius;
        int igniteTicks = Configuration.get().phantomIgniteDuration;

        List<PhantomEntity> phantoms = world.getEntitiesByClass(
                PhantomEntity.class,
                new Box(pos.add(-range, -range, -range),
                        pos.add(range + 1, range + 1, range + 1)),
                entity -> true
        );

        for (PhantomEntity phantom : phantoms) {
            phantom.setOnFireFor(igniteTicks);
        }

        world.scheduleBlockTick(pos, this, 20);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, 20);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
