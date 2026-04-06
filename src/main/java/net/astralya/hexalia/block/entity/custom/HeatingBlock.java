package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public interface HeatingBlock {
    default boolean isHeated (Level plevel, BlockPos pPos) {
        BlockState stateBelow = plevel.getBlockState(pPos.below());
        if (!stateBelow.is(ModTags.Blocks.HEATING_BLOCKS)) return false;
        if (stateBelow.hasProperty(BlockStateProperties.LIT)) return stateBelow.getValue(BlockStateProperties.LIT);
        return true;
    }
}
