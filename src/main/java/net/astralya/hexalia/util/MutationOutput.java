package net.astralya.hexalia.util;

import net.astralya.hexalia.Configuration;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public final class MutationOutput {

    private MutationOutput() {
    }

    public static void apply(ServerWorld world, BlockPos pos, ItemStack result) {
        if (result.isEmpty()) {
            return;
        }

        if (!Configuration.MUTATION_SPAWNS_ITEM_ENTITY.get() && result.getItem() instanceof BlockItem blockItem) {
            BlockState placedState = blockItem.getBlock().getDefaultState();
            if (placedState.canPlaceAt(world, pos) && world.getBlockState(pos).isReplaceable()) {
                world.setBlockState(pos, placedState, Block.NOTIFY_ALL);
                return;
            }
        }

        world.spawnEntity(new ItemEntity(
                world,
                pos.getX() + 0.5D,
                pos.getY() + 0.25D,
                pos.getZ() + 0.5D,
                result.copy()
        ));
    }
}