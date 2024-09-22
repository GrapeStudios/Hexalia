package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.particle.ModParticles;
import net.minecraft.block.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GhostFernBlock extends HPlantBlock {
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
            double d = (double)pos.getX() + 0.5;
            double e = (double)pos.getY() + 0.2;
            double f = (double)pos.getZ() + 0.5;
            world.addParticle(ModParticles.GHOST_PARTICLE, d, e, f, 0.0, 0.0, 0.0);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
