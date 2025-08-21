package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.particle.ModParticles;
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

public class InfusedFarmlandBlock extends FarmlandBlock {

    private static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

    public InfusedFarmlandBlock(Settings settings) {
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
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.hasRain(pos.up())) {
            return;
        }

        BlockState aboveState = world.getBlockState(pos.up());
        Block aboveBlock = aboveState.getBlock();

        if (aboveBlock instanceof Fertilizable fertilizable) {
            if (fertilizable.isFertilizable(world, pos.up(), aboveState, false)) {
                if (fertilizable.canGrow(world, world.random, pos.up(), aboveState)) {
                    fertilizable.grow(world, world.random, pos.up(), aboveState);

                    world.getPlayers().forEach(player -> {
                        if (player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64) {
                            world.spawnParticles(player,
                                    ModParticles.INFUSED_BUBBLE_PARTICLE,
                                    true,
                                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                                    8,
                                    0.5, 0.0, 0.5,
                                    0.05);
                        }
                    });

                    world.syncWorldEvent(2005, pos.up(), 0);
                }
            }
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState aboveState = world.getBlockState(pos.up());
        return super.canPlaceAt(state, world, pos) || aboveState.getBlock() instanceof GourdBlock;
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
        if (world.isClient) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < 8; i++) {
                double x = pos.getX() + 0.5 + random.nextDouble(-0.5, 0.5);
                double y = pos.getY() + 1.0;
                double z = pos.getZ() + 0.5 + random.nextDouble(-0.5, 0.5);
                world.addParticle(ModParticles.INFUSED_BUBBLE_PARTICLE, x, y, z, 0.0D, 0.05D, 0.0D);
            }
        }
    }
}
