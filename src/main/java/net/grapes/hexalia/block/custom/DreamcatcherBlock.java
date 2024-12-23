package net.grapes.hexalia.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class DreamcatcherBlock extends HWallBlock {

    private static final int CHECK_RADIUS = 16;

    public DreamcatcherBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        List<Phantom> phantoms = level.getEntitiesOfClass(
                Phantom.class,
                new AABB(pos.offset(-CHECK_RADIUS, -CHECK_RADIUS, -CHECK_RADIUS),
                        pos.offset(CHECK_RADIUS + 1, CHECK_RADIUS + 1, CHECK_RADIUS + 1))
        );

        for (Phantom phantom : phantoms) {
            phantom.setSecondsOnFire(5);
        }

        level.scheduleTick(pos, this, 20);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 20);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
