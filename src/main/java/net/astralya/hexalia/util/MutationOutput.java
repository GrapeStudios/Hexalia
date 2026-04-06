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
        if (!Configuration.MUTATION_SPAWNS_ITEM_ENTITY.get()) {
            if (result.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                BlockState placed = block.getDefaultState();
                world.setBlockState(pos, placed, Block.NOTIFY_ALL);
                return;
            }
        }
        world.spawnEntity(new ItemEntity(
                world,
                pos.getX() + 0.5,
                pos.getY() + 0.25,
                pos.getZ() + 0.5,
                result.copy()
        ));
    }
}