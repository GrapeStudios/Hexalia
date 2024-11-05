package net.grapes.hexalia.block.entity;

import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public interface HeatingBlockEntity {

     /* This is intended to be implemented and represent blocks capable of heating
    the placed above block. Also checks if the block is lit. */

    default boolean isHeated (Level plevel, BlockPos pPos) {
        BlockState stateBelow = plevel.getBlockState(pPos.below());
        if (!stateBelow.is(ModBlocks.RUSTIC_OVEN.get())) return false;
        if (stateBelow.hasProperty(BlockStateProperties.LIT)) return stateBelow.getValue(BlockStateProperties.LIT);
        return true;
    }
}
