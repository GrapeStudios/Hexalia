package net.grapes.hexalia.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CelestialBloomBlock extends HerbBlock {

    private static final double MIN_X = 5.0;
    private static final double MAX_X = 11.0;
    private static final double MIN_Z = 5.0;
    private static final double MAX_Z = 11.0;
    private static final double HEIGHT = 10.0;

    public CelestialBloomBlock(StatusEffect suspiciousStewEffect, int effectDuration, Settings settings) {
        super(suspiciousStewEffect, effectDuration, settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if (random.nextFloat() < 0.2f) {
            double centerX = pos.getX() + 0.5;
            double centerZ = pos.getZ() + 0.5;

            double x = centerX + (random.nextDouble() - 0.5) * (MAX_X - MIN_X) / 16.0;
            double y = pos.getY() + random.nextDouble() * HEIGHT / 16.0;
            double z = centerZ + (random.nextDouble() - 0.5) * (MAX_Z - MIN_Z) / 16.0;

            if (random.nextBoolean()) {
                x = centerX + (random.nextBoolean() ? (MAX_X + 0.5) : (MIN_X - 0.5)) / 16.0;
            } else {
                z = centerZ + (random.nextBoolean() ? (MAX_Z + 0.5) : (MIN_Z - 0.5)) / 16.0;
            }

            double motionX = (random.nextDouble() - 0.5) * 0.02;
            double motionY = random.nextDouble() * 0.02;
            double motionZ = (random.nextDouble() - 0.5) * 0.02;

            world.addParticle(ParticleTypes.END_ROD,
                    x, y, z,
                    motionX, motionY, motionZ);
        }
    }
}
