package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.particle.ModParticleType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CelestialBloomBlock extends HerbBlock {

    public CelestialBloomBlock(RegistryEntry<StatusEffect> effect, float seconds, Settings settings) {
        super(effect, seconds, settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!Configuration.CELESTIAL_BLOOM_EMITS_PARTICLES.get()) return;

        boolean withered = state.isOf(ModBlocks.WITHERED_CELESTIAL_BLOOM);
        int spawnChance = withered ? 6 : 3;
        if (random.nextInt(spawnChance) != 0) {
            return;
        }
        double cx = pos.getX() + 0.5D;
        double cz = pos.getZ() + 0.5D;
        double x = cx + (random.nextDouble() - 0.5D) * 0.35D;
        double y = pos.getY() + 0.45D + random.nextDouble() * 0.45D;
        double z = cz + (random.nextDouble() - 0.5D) * 0.35D;
        double velocityScale = withered ? 0.6D : 1.0D;
        double vx = (random.nextDouble() - 0.5D) * 0.003D;
        double vy = 0.010D + random.nextDouble() * 0.010D;
        double vz = (random.nextDouble() - 0.5D) * 0.003D;
        world.addParticle(ModParticleType.SPARKLE, x, y, z, vx, vy, vz);
        int clusterChance = withered ? 14 : 8;
        if (!withered && random.nextInt(clusterChance) == 0 || withered && random.nextInt(clusterChance) == 0) {
            int clusterCount = withered ? 1 : 3;
            for (int i = 0; i < clusterCount; i++) {
                double x2 = cx + (random.nextDouble() - 0.5D) * 0.25D;
                double y2 = pos.getY() + 0.60D + random.nextDouble() * 0.35D;
                double z2 = cz + (random.nextDouble() - 0.5D) * 0.25D;
                double vx2 = (random.nextDouble() - 0.5D) * 0.004D * velocityScale;
                double vy2 = (0.014D + random.nextDouble() * 0.012D) * velocityScale;
                double vz2 = (random.nextDouble() - 0.5D) * 0.004D * velocityScale;
                world.addParticle(ModParticleType.SPARKLE, x2, y2, z2, vx2, vy2, vz2);
            }
        }
    }
}