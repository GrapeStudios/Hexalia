package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.particle.ModParticleType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class GhostFernBlock extends HerbBlock {

    public GhostFernBlock(RegistryEntry<StatusEffect> stewEffect, float effectLengthInSeconds, Settings settings) {
        super(stewEffect, effectLengthInSeconds, settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!Configuration.GHOST_FERN_EMITS_PARTICLES.get()) return;

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;

        double ox = (random.nextDouble() - 0.5) * 0.2;
        double oz = (random.nextDouble() - 0.5) * 0.2;

        world.addParticle(
                ModParticleType.GHOST,
                cx + ox, cy, cz + oz,
                (random.nextDouble() - 0.5) * 0.02,
                0.08,
                (random.nextDouble() - 0.5) * 0.02
        );
    }
}
