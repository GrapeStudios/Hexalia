package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.Configuration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class DreamcatcherBlock extends WallMountedBlock {

    public DreamcatcherBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);

        int range = Configuration.DREAMCATCHER_RADIUS.get();
        int igniteTime = Configuration.PHANTOM_IGNITE_DURATION.get();

        List<Phantom> phantoms = level.getEntitiesOfClass(
                Phantom.class,
                new AABB(pos.offset(-range, -range, -range),
                        pos.offset(range + 1, range + 1, range + 1))
        );

        for (Phantom phantom : phantoms) {
            phantom.setSecondsOnFire(igniteTime);
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
