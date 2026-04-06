package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.particle.ModParticleType;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import net.minecraft.world.event.GameEvent;

import java.util.concurrent.ThreadLocalRandom;

public class InfusedFarmlandBlock extends FarmlandBlock {

    public static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

    public InfusedFarmlandBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack inHand = player.getStackInHand(hand);
        if (inHand.getItem() instanceof ShovelItem
                && state.isOf(ModBlocks.INFUSED_FARMLAND)
                && hit.getSide() != Direction.DOWN
                && world.getBlockState(pos.up()).isAir()) {

            world.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0f, 1.0f);

            if (!world.isClient) {
                world.setBlockState(pos, ModBlocks.INFUSED_DIRT.getDefaultState(), Block.NOTIFY_ALL);
                inHand.damage(1, player, LivingEntity.getSlotForHand(hand));
            }
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState above = world.getBlockState(pos.up());
        return super.canPlaceAt(state, world, pos) || above.getBlock() instanceof StemBlock;
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (!state.canPlaceAt(world, pos)) {
            setToInfusedDirt(world, pos);
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        BlockPos abovePos = pos.up();
        if (world.isRaining()) {
            BlockState cropState = world.getBlockState(abovePos);
            if (cropState.getBlock() instanceof Fertilizable growable) {
                if (growable.isFertilizable(world, abovePos, cropState)) {
                    if (growable.canGrow(world, random, abovePos, cropState)) {
                        growable.grow(world, random, abovePos, cropState);
                        world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, abovePos, 0);
                        world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, abovePos);
                    }
                }
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return !getDefaultState().canPlaceAt(ctx.getWorld(), ctx.getBlockPos())
                ? ModBlocks.INFUSED_DIRT.getDefaultState()
                : super.getPlacementState(ctx);
    }

    private void setToInfusedDirt(ServerWorld world, BlockPos pos) {
        world.setBlockState(
                pos,
                pushEntitiesUpBeforeBlockChange(world.getBlockState(pos), ModBlocks.INFUSED_DIRT.getDefaultState(), world, pos),
                Block.NOTIFY_ALL
        );
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        spawnBubblesParticles(world, pos);
    }

    private void spawnBubblesParticles(World world, BlockPos pos) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int i = 0; i < 8; i++) {
            double x = pos.getX() + 0.5 + rng.nextDouble(-0.5, 0.5);
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5 + rng.nextDouble(-0.5, 0.5);
            world.addParticle(ModParticleType.INFUSED_BUBBLE, x, y, z, 0.0, 0.05, 0.0);
        }
    }
}
