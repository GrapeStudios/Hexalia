package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.particle.ModParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.MushroomPlantBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

import java.util.concurrent.ThreadLocalRandom;

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
        createSporeParticles(world, pos, 5);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return false;
    }

    public static void createSporeParticles(World world, BlockPos pos, int particleFrequency) {
        final double maxHorizontalOffset = 0.1;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double yStart = pos.getY() + 0.3;
        for (double y = yStart; y > pos.getY(); y -= 0.05) {
            if (random.nextInt(particleFrequency) == 0) {
                double x = pos.getX() + 0.5;
                double z = pos.getZ() + 0.5;
                z += random.nextDouble(-maxHorizontalOffset, maxHorizontalOffset);
                x += random.nextDouble(-maxHorizontalOffset, maxHorizontalOffset);
                world.addParticle(ModParticles.SPORE_PARTICLE, x, y, z, 0.001D, -0.001D, 0.001D); // Adjusting the Y velocity to be negative
            }
        }
    }

}