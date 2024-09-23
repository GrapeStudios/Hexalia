package net.grapes.hexalia.block.entity;

import net.grapes.hexalia.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface HeatingBlockEntity {

    /* This is intended to be implemented and represent blocks capable of heating
    surrounding (placed above) blocks. Also checks if the block is lit. */

    default boolean isHeated (World world, BlockPos pos) {
        BlockState stateBelow = world.getBlockState(pos.down());
        if (!stateBelow.isIn(ModTags.Blocks.HEATING_BLOCKS)) return false;
        if (stateBelow.contains(Properties.LIT)) return stateBelow.get(Properties.LIT);
        return true;
    }
}
