package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.SeagrassBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class SirenKelpBlock extends SeagrassBlock {

    public SirenKelpBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return (floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(ModBlocks.INFUSED_DIRT)) && !floor.isOf(Blocks.MAGMA_BLOCK);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        BlockState blockBelow = world.getBlockState(pos.down());
        return blockBelow.isOf(ModBlocks.INFUSED_DIRT);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockState belowState = world.getBlockState(pos.down());
        if (!belowState.isOf(ModBlocks.INFUSED_DIRT)) {
            return ActionResult.PASS;
        }

        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.BONE_MEAL)) {
            if (!world.isClient) {
                if (this.canGrow(world, pos)) {
                    dropStack(world, pos, new ItemStack(this));
                    if (!player.isCreative()) {
                        itemStack.decrement(1);
                    }
                    world.playSound(null, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    ((ServerWorld) world).spawnParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 10, 0.25, 0.25, 0.25, 0.05);
                    return ActionResult.SUCCESS;
                }
            } else {
                world.playSound(player, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public boolean canGrow (World world, BlockPos pos) {
        BlockState belowState = world.getBlockState(pos.down());
        return belowState.isOf(ModBlocks.INFUSED_DIRT);
    }
}

