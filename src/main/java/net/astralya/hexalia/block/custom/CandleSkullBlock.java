package net.astralya.hexalia.block.custom;

import com.google.common.collect.ImmutableList;
import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CandleSkullBlock extends Block {

    public static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0.25, 0, 0.25, 0.75, 0.5, 0.75),
            Shapes.box(0.4375, 0.453125, 0.4375, 0.5625, 0.640625, 0.5625)
    );
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = AbstractCandleBlock.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final Iterable<Vec3> PARTICLE_OFFSETS = ImmutableList.of(new Vec3(0.5, 0.75, 0.5));

    public CandleSkullBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(LIT, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        Level level = context.getLevel();
        FluidState fluidState = level.getFluidState(blockPos);
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
                .setValue(LIT, false);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isSolid();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() instanceof FlintAndSteelItem) {
            if (!state.getValue(LIT) && !state.getValue(WATERLOGGED)) {
                lightCandleSkull(state, level, pos, player, hand, itemStack);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        } else if (itemStack.isEmpty()) {
            if (state.getValue(LIT)) {
                extinguishCandleSkull(state, level, pos);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    private static void lightCandleSkull(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack itemStack) {
        level.setBlockAndUpdate(pos, state.setValue(LIT, true));
        itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, level.random.nextFloat() * 0.4F + 0.8F);
    }

    private static void extinguishCandleSkull(BlockState state, Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos, state.setValue(LIT, false));
        level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, level.random.nextFloat() * 0.4F + 0.8F);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }
        getParticleOffsets().forEach(offset -> CandleSkullBlock.spawnCandleParticles(level, offset.add(pos.getX(), pos.getY(), pos.getZ()), random));
    }

    private static void spawnCandleParticles(Level pLevel, Vec3 vec3, RandomSource pRandom) {
        float chance = pRandom.nextFloat();
        if (chance < 0.3f) {
            pLevel.addParticle(ParticleTypes.SMOKE, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
            if (chance < 0.17f) {
                pLevel.playLocalSound(vec3.x + 0.5, vec3.y + 0.5, vec3.z + 0.5, SoundEvents.CANDLE_AMBIENT,
                        SoundSource.BLOCKS, 1.0f + pRandom.nextFloat(), pRandom.nextFloat() * 0.7f + 0.3f, false);
            }
        }

        BlockPos pos = new BlockPos((int)Math.floor(vec3.x), (int)Math.floor(vec3.y), (int)Math.floor(vec3.z));
        BlockState state = pLevel.getBlockState(pos);
        Block block = state.getBlock();

        if (block == ModBlocks.WITHER_CANDLE_SKULL.get()) {
            pLevel.addParticle(ParticleTypes.SOUL_FIRE_FLAME, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
        } else {
            pLevel.addParticle(ParticleTypes.FLAME, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
        }
    }

    public static Iterable<Vec3> getParticleOffsets() {
        return PARTICLE_OFFSETS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, LIT);
    }
}
