package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DreamshroomBlock extends HMushroomBlock {

    private static final double MAX_HORIZONTAL_OFFSET = 0.1;
    private static final double PARTICLE_START_Y_OFFSET = 0.3;
    private static final double PARTICLE_FALL_SPEED = -0.02;
    private static final double PARTICLE_MOTION_VARIANCE = 0.02;
    private static final int PARTICLE_FREQUENCY = 5;

    public DreamshroomBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return (Block.isFaceFull(floor.getCollisionShape(world, pos), Direction.UP) || floor.is(ModBlocks.INFUSED_DIRT.get())) && !floor.is(Blocks.MAGMA_BLOCK);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        createSporeParticles(pLevel, pPos, pRandom, PARTICLE_FREQUENCY);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockState blockBelow = pLevel.getBlockState(pPos.below());
        if (!blockBelow.is(ModBlocks.INFUSED_DIRT.get())) {
            return InteractionResult.PASS;
        }

        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if (itemStack.is(Items.BONE_MEAL)) {
            if (!pLevel.isClientSide()) {
                if (this.canGrow(pLevel, pPos)) {
                    popResource(pLevel, pPos, new ItemStack(this));
                    if (!pPlayer.isCreative()) {
                        itemStack.shrink(1);
                    }
                    pLevel.playSound(null, pPos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    ((ServerLevel) pLevel).sendParticles(ParticleTypes.HAPPY_VILLAGER, pPos.getX() + 0.5, pPos.getY() + 1,
                            pPos.getZ() + 0.5, 10, 0.25, 0.25, 0.25, 0.05);
                    return InteractionResult.SUCCESS;
                }
            } else {
                pLevel.playSound(pPlayer, pPos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                pLevel.addParticle(ParticleTypes.HAPPY_VILLAGER, pPos.getX() + 0.5, pPos.getY() + 1.0,
                        pPos.getZ() + 0.5, 0.0, 0.0, 0.0);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public static void createSporeParticles(Level world, BlockPos pos, RandomSource random, int particleFrequency) {
        double centerX = pos.getX() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        for (double y = pos.getY() + PARTICLE_START_Y_OFFSET; y > pos.getY(); y -= 0.1) {
            if (random.nextInt(particleFrequency) == 0) {
                double x = centerX + random.nextDouble() * 2 * MAX_HORIZONTAL_OFFSET - MAX_HORIZONTAL_OFFSET;
                double z = centerZ + random.nextDouble() * 2 * MAX_HORIZONTAL_OFFSET - MAX_HORIZONTAL_OFFSET;
                double motionX = random.nextGaussian() * PARTICLE_MOTION_VARIANCE;
                double motionZ = random.nextGaussian() * PARTICLE_MOTION_VARIANCE;
                world.addParticle(ModParticles.SPORE_PARTICLE.get(), x, y, z, motionX, PARTICLE_FALL_SPEED, motionZ);
            }
        }
    }


    public boolean canGrow (Level pLevel, BlockPos pPos) {
        BlockState blockBelow = pLevel.getBlockState(pPos.below());
        return blockBelow.is(ModBlocks.INFUSED_DIRT.get());
    }
}
