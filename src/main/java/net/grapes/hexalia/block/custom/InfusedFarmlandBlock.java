package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("deprecation")

public class InfusedFarmlandBlock extends Block {

    public static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

    public InfusedFarmlandBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }


    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if (itemStack.getItem() instanceof ShovelItem && pState.getBlock() == ModBlocks.INFUSED_FARMLAND.get() &&
                pHit.getDirection() != Direction.DOWN && pLevel.getBlockState(pPos.above()).isAir()) {
            pLevel.playSound(pPlayer, pPos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (!pLevel.isClientSide) {
                setToInfusedDirt(pLevel, pPos);
                itemStack.hurtAndBreak(1, pPlayer,  p -> p.broadcastBreakEvent(pHand));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockState blockState = pLevel.getBlockState(pPos.above());
        return !blockState.isSolid() || blockState.getBlock() instanceof FenceGateBlock
                || blockState.getBlock() instanceof PistonHeadBlock;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!pState.canSurvive(pLevel, pPos)) {
            setToInfusedDirt(pLevel, pPos);
        }
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pDirection == Direction.UP && !pState.canSurvive(pLevel, pPos)) {
            pLevel.scheduleTick(pPos, this, 1);
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    private void setToInfusedDirt(Level pLevel, BlockPos pPos) {
        pLevel.setBlockAndUpdate(pPos, pushEntitiesUp(pLevel.getBlockState(pPos),
                ModBlocks.INFUSED_DIRT.get().defaultBlockState(), pLevel, pPos));
    }

    @Override
    public void fallOn(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float pFallDistance) {
        spawnBubbleParticles(pLevel, pPos);
    }

    private void spawnBubbleParticles(Level pLevel, BlockPos pPos) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 8; i++) {
            double x = pPos.getX() + 0.5 + random.nextDouble(-0.5, 0.5);
            double y = pPos.getY() + 1.0;
            double z = pPos.getZ() + 0.5 + random.nextDouble(-0.5, 0.5);
            pLevel.addParticle(ModParticles.INFUSED_BUBBLE_PARTICLE.get(), x, y, z, 0.0d,
                    0.05d, 0.0d);
        }
    }
}
