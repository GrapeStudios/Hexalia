package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.particle.ModParticleType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CelestialBloomBlock extends HerbBlock {

    public CelestialBloomBlock(StatusEffect suspiciousStewEffect, int effectDuration, Settings settings) {
        super(suspiciousStewEffect, effectDuration, settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!Configuration.CELESTIAL_BLOOM_EMITS_PARTICLES.get()) {
            return;
        }

        boolean withered = state.isOf(ModBlocks.WITHERED_CELESTIAL_BLOOM);
        int spawnChance = withered ? 6 : 3;
        if (random.nextInt(spawnChance) != 0) {
            return;
        }

        double centerX = pos.getX() + 0.5D;
        double centerZ = pos.getZ() + 0.5D;
        double x = centerX + (random.nextDouble() - 0.5D) * 0.35D;
        double y = pos.getY() + 0.45D + random.nextDouble() * 0.45D;
        double z = centerZ + (random.nextDouble() - 0.5D) * 0.35D;
        double velocityScale = withered ? 0.6D : 1.0D;
        double velocityX = (random.nextDouble() - 0.5D) * 0.003D;
        double velocityY = 0.010D + random.nextDouble() * 0.010D;
        double velocityZ = (random.nextDouble() - 0.5D) * 0.003D;

        world.addParticle(ModParticleType.SPARKLE, x, y, z, velocityX, velocityY, velocityZ);

        int clusterChance = withered ? 14 : 8;
        if (random.nextInt(clusterChance) == 0) {
            int clusterCount = withered ? 1 : 3;
            for (int i = 0; i < clusterCount; i++) {
                double clusterX = centerX + (random.nextDouble() - 0.5D) * 0.25D;
                double clusterY = pos.getY() + 0.60D + random.nextDouble() * 0.35D;
                double clusterZ = centerZ + (random.nextDouble() - 0.5D) * 0.25D;
                double clusterVelocityX = (random.nextDouble() - 0.5D) * 0.004D * velocityScale;
                double clusterVelocityY = (0.014D + random.nextDouble() * 0.012D) * velocityScale;
                double clusterVelocityZ = (random.nextDouble() - 0.5D) * 0.004D * velocityScale;
                world.addParticle(ModParticleType.SPARKLE, clusterX, clusterY, clusterZ, clusterVelocityX, clusterVelocityY, clusterVelocityZ);
            }
        }
    }
}
