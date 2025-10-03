package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface HeatingBlock {

    default boolean isHeated (World world, BlockPos pos) {
        BlockState stateBelow = world.getBlockState(pos.down());
        if (!stateBelow.isIn(ModTags.Blocks.HEATING_BLOCKS)) return false;
        if (stateBelow.contains(Properties.LIT)) return stateBelow.get(Properties.LIT);
        return true;
    }
}
