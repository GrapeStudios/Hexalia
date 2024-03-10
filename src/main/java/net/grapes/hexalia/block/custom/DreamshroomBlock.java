package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.particle.ModParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.MushroomPlantBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class DreamshroomBlock extends MushroomPlantBlock {

    public DreamshroomBlock(Settings settings) {

        super(settings, TreeConfiguredFeatures.HUGE_BROWN_MUSHROOM);
    }

    @Override
    public boolean trySpawningBigMushroom(ServerWorld world, BlockPos pos, BlockState state, Random random) {
        return false;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        createSporeParticles(world, pos, random,5);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return false;
    }

    public static void createSporeParticles(World world, BlockPos pos, Random random, int particleFrequency) {
        final double maxHorizontalOffset = 0.1;
        double centerX = pos.getX() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        for (double y = pos.getY() + 0.3; y > pos.getY(); y -= 0.1) {
            if (random.nextInt(particleFrequency) == 0) {
                double x = centerX + random.nextDouble() * 2 * maxHorizontalOffset - maxHorizontalOffset;
                double z = centerZ + random.nextDouble() * 2 * maxHorizontalOffset - maxHorizontalOffset;
                double motionX = random.nextGaussian() * 0.02;
                double motionY = random.nextGaussian() * 0.02;
                double motionZ = random.nextGaussian() * 0.02;
                world.addParticle(ModParticles.SPORE_PARTICLE, x, y, z, motionX, motionY, motionZ);
            }
        }
    }


}
