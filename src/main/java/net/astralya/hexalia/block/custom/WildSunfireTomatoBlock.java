package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
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

    public static final MapCodec<WildSunfireTomatoBlock> CODEC = simpleCodec(WildSunfireTomatoBlock::new);
    protected static final VoxelShape SHAPE = Shapes.or(Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0));

    public MapCodec<WildSunfireTomatoBlock> codec() {
        return CODEC;
    }

    public WildSunfireTomatoBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        spawnFireParticles(level, pos);
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void entityInside(BlockState pState, Level pLevel, BlockPos pos, Entity pEntity) {
        if (!pLevel.isClientSide && pLevel.getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL) {
            if (pEntity instanceof LivingEntity livingEntity) {
                if (!livingEntity.isSteppingCarefully() && !livingEntity.fireImmune()) {
                    if (livingEntity instanceof Player player && player.isCreative()) {
                        return;
                    }
                    livingEntity.igniteForSeconds(5);
                }
            }
        }
    }

    private void spawnFireParticles(Level level, BlockPos pos) {
        final double maxHorizontalOffset = 0.5;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 8; i++) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + random.nextDouble(0.33);
            double z = pos.getZ() + 0.5;
            z += random.nextDouble(-maxHorizontalOffset, maxHorizontalOffset);
            x += random.nextDouble(-maxHorizontalOffset, maxHorizontalOffset);
            level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.05D, 0.0D);
        }
    }
}
