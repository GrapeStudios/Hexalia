package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.particle.ModParticleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DreamshroomBlock extends ShroomBlock {

    private static final double MAX_HORIZONTAL_OFFSET = 0.1;
    private static final double PARTICLE_START_Y_OFFSET = 0.3;
    private static final double PARTICLE_FALL_SPEED = -0.02;
    private static final double PARTICLE_MOTION_VARIANCE = 0.02;
    private static final int PARTICLE_FREQUENCY = 5;

    public DreamshroomBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.isFaceSturdy(level, pos, Direction.UP) || state.is(ModBlocks.INFUSED_DIRT)
                && !state.is(Blocks.MAGMA_BLOCK);
    }

    public boolean isValidBonemealTarget(LevelReader reader, BlockPos pos, BlockState state) {
        BlockState blockBelow = reader.getBlockState(pos.below());
        return blockBelow.is(ModBlocks.INFUSED_DIRT);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockState blockBelow = level.getBlockState(pos.below());
        if (!blockBelow.is(ModBlocks.INFUSED_DIRT.get())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(Items.BONE_MEAL)) {
            if (!level.isClientSide()) {
                if (this.isValidBonemealTarget(level, pos, state)) {
                    popResource(level, pos, new ItemStack(this));
                    if (!player.isCreative()) {
                        itemStack.shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    ((ServerLevel) level).sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1,
                            pos.getZ() + 0.5, 10, 0.25, 0.25, 0.25, 0.05);
                    return ItemInteractionResult.SUCCESS;
                }
            } else {
                level.playSound(player, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                level.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1.0,
                        pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!Configuration.DREAMSHROOM_EMITS_PARTICLES.get()) return;
        spawnSporesParticle(level, pos, random, PARTICLE_FREQUENCY);
    }

    public static void spawnSporesParticle(Level level, BlockPos pos, RandomSource random, int particleFrequency) {
        double centerX = pos.getX() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        for (double y = pos.getY() + PARTICLE_START_Y_OFFSET; y > pos.getY(); y -= 0.1) {
            if (random.nextInt(particleFrequency) == 0) {
                double x = centerX + random.nextDouble() * 2 * MAX_HORIZONTAL_OFFSET - MAX_HORIZONTAL_OFFSET;
                double z = centerZ + random.nextDouble() * 2 * MAX_HORIZONTAL_OFFSET - MAX_HORIZONTAL_OFFSET;
                double motionX = random.nextGaussian() * PARTICLE_MOTION_VARIANCE;
                double motionZ = random.nextGaussian() * PARTICLE_MOTION_VARIANCE;
                level.addParticle(ModParticleType.SPORE.get(), x, y, z, motionX, PARTICLE_FALL_SPEED, motionZ);
            }
        }
    }
}
