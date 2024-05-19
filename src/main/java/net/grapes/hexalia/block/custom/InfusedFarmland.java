package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.particle.ModParticles;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;

import java.util.concurrent.ThreadLocalRandom;

public class InfusedFarmland extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);
    private static final double PARTICLE_OFFSET = 0.5;
    private static final int PARTICLE_COUNT = 8;

    public InfusedFarmland(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() instanceof ShovelItem && state.getBlock() == ModBlocks.INFUSED_FARMLAND &&
                hit.getSide() != Direction.DOWN && world.getBlockState(pos.up()).isAir()) {
            world.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0f, 1.0f);
            if (!world.isClient) {
                setToInfusedDirt(world, pos);
                itemStack.damage(1, player, p -> p.sendToolBreakStatus(hand));
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.up());
        return !blockState.isSolid() || blockState.getBlock() instanceof FenceGateBlock || blockState.getBlock() instanceof PistonExtensionBlock;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            setToInfusedDirt(world, pos);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP && !state.canPlaceAt(world, pos)) {
            world.scheduleBlockTick(pos, this, 1);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        spawnBubbleParticles(world, pos);
    }

    private static void setToInfusedDirt(World world, BlockPos pos) {
        world.setBlockState(pos, pushEntitiesUpBeforeBlockChange(world.getBlockState(pos),
                ModBlocks.INFUSED_DIRT.getDefaultState(), world, pos));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    private void spawnBubbleParticles(World world, BlockPos pos) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double x = pos.getX() + 0.5 + random.nextDouble(-PARTICLE_OFFSET, PARTICLE_OFFSET);
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5 + random.nextDouble(-PARTICLE_OFFSET, PARTICLE_OFFSET);
            world.addParticle(ModParticles.BUBBLE_PARTICLE, x, y, z, 0.0D, 0.05D, 0.0D);
        }
    }
}
