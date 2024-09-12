package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class HFlowerBlock extends FlowerBlock implements Fertilizable {

    public HFlowerBlock(StatusEffect suspiciousStewEffect, int effectDuration, Settings settings) {
        super(suspiciousStewEffect, effectDuration, settings);
    }

    /* Hexalia's InfusableFlowerBlock can be used for flowers blocks that can be applied
    with Bone Meal when planted in an Infused Dirt block to duplicate the flower.  */

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return (floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(ModBlocks.INFUSED_DIRT)) && !floor.isOf(Blocks.MAGMA_BLOCK);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockState belowState = world.getBlockState(pos.down());
        if (belowState.isOf(ModBlocks.INFUSED_DIRT)) {
            dropStack(world, pos, new ItemStack(this));
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockState belowState = world.getBlockState(pos.down());
        if (!belowState.isOf(ModBlocks.INFUSED_DIRT)) {
            return ActionResult.PASS;
        }

        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.BONE_MEAL)) {
            if (!world.isClient && this.isFertilizable(world, pos, state, false) && this.canGrow(world, world.random, pos, state)) {
                this.grow((ServerWorld) world, world.random, pos, state);
                if (!player.isCreative()) {
                    itemStack.decrement(1);
                }
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        return ActionResult.PASS;
    }
}
