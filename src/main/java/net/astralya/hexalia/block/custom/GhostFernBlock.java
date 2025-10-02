package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.particle.ModParticleType;
import net.minecraft.block.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GhostFernBlock extends HerbBlock {
    protected static final VoxelShape SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0.125, 0, 0.0625, 0.9375, 0.4375, 0.9375)
    );

    public GhostFernBlock(StatusEffect suspiciousStewEffect, int effectDuration, Settings settings) {
        super(suspiciousStewEffect, effectDuration, settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!Configuration.client().plants.ghostFernEmitsParticles) return;

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

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
