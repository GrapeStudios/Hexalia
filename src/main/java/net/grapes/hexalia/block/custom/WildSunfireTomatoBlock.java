package net.grapes.hexalia.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.concurrent.ThreadLocalRandom;

public class WildSunfireTomatoBlock extends BushBlock {
    protected static final VoxelShape SHAPE = Shapes.or(Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0));

    public WildSunfireTomatoBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        createFireParticles(level, pos); 
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (!pLevel.isClientSide && pLevel.getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL) {
            if (pEntity instanceof LivingEntity livingEntity) {
                if (!livingEntity.isSteppingCarefully() && !livingEntity.fireImmune()) {
                    if (livingEntity instanceof Player player && player.isCreative()) {
                        return;
                    }
                    livingEntity.setSecondsOnFire(5);
                }
            }
        }
    }

    public static void createFireParticles(Level pLevel, BlockPos pPos) {
        final double maxHorizontalOffset = 0.5;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 8; i++) {
            double x = pPos.getX() + 0.5;
            double y = pPos.getY() + random.nextDouble(0.33);
            double z = pPos.getZ() + 0.5;
            z += random.nextDouble(-maxHorizontalOffset, maxHorizontalOffset);
            x += random.nextDouble(-maxHorizontalOffset, maxHorizontalOffset);
            pLevel.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.05D, 0.0D);
        }
    }
}
