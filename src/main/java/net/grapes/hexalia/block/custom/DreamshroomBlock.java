package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.particle.ModParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomPlantBlock;
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
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class DreamshroomBlock extends MushroomPlantBlock {

    // Constants for particle
    private static final double MAX_HORIZONTAL_OFFSET = 0.1;
    private static final double PARTICLE_START_Y_OFFSET = 0.3;
    private static final double PARTICLE_FALL_SPEED = -0.02;
    private static final double PARTICLE_MOTION_VARIANCE = 0.02;
    private static final int PARTICLE_FREQUENCY = 5;

    public DreamshroomBlock(Settings settings) {
        super(settings, TreeConfiguredFeatures.HUGE_BROWN_MUSHROOM);
    }

    @Override
    public boolean trySpawningBigMushroom(ServerWorld world, BlockPos pos, BlockState state, Random random) {
        return false;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        createSporeParticles(world, pos, random, PARTICLE_FREQUENCY);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return (floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(ModBlocks.INFUSED_DIRT)) && !floor.isOf(Blocks.MAGMA_BLOCK);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public static void createSporeParticles(World world, BlockPos pos, Random random, int particleFrequency) {
        double centerX = pos.getX() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        for (double y = pos.getY() + PARTICLE_START_Y_OFFSET; y > pos.getY(); y -= 0.1) {
            if (random.nextInt(particleFrequency) == 0) {
                double x = centerX + random.nextDouble() * 2 * MAX_HORIZONTAL_OFFSET - MAX_HORIZONTAL_OFFSET;
                double z = centerZ + random.nextDouble() * 2 * MAX_HORIZONTAL_OFFSET - MAX_HORIZONTAL_OFFSET;
                double motionX = random.nextGaussian() * PARTICLE_MOTION_VARIANCE;
                double motionZ = random.nextGaussian() * PARTICLE_MOTION_VARIANCE;
                world.addParticle(ModParticles.SPORE_PARTICLE, x, y, z, motionX, PARTICLE_FALL_SPEED, motionZ);
            }
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
            if (!world.isClient) {
                if (this.canGrow(world, world.random, pos, state)) {
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
}
