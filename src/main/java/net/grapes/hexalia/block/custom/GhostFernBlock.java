package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public class GhostFernBlock extends HPlantBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0.125, 0, 0.0625, 0.9375, 0.4375, 0.9375)
    );

    public GhostFernBlock(Supplier<MobEffect> effectSupplier, int pEffectDuration, Properties pProperties) {
        super(effectSupplier, pEffectDuration, pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        double d = (double)pPos.getX() + 0.5;
        double e = (double)pPos.getY() + 0.2;
        double f = (double)pPos.getZ() + 0.5;
        pLevel.addParticle(ModParticles.GHOST_PARTICLE.get(),
                d, e, f, 0.0, 1.0, 0.0);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
