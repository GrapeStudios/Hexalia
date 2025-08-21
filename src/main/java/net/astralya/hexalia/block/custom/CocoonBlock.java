package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.entity.variant.SilkMothVariant;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CocoonBlock extends Block {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty HATCH = IntegerProperty.create("hatch", 0, 2);

    private static final VoxelShape NORTH_SHAPE = Shapes.create(new AABB(5.0 / 16.0, 5.0 / 16.0, 11.0 / 16.0, 11.0 / 16.0, 12.0 / 16.0, 1.0));
    private static final VoxelShape SOUTH_SHAPE = Shapes.create(new AABB(5.0 / 16.0, 5.0 / 16.0, 0.0, 11.0 / 16.0, 12.0 / 16.0, 5.0 / 16.0));
    private static final VoxelShape WEST_SHAPE = Shapes.create(new AABB(11.0 / 16.0, 5.0 / 16.0, 5.0 / 16.0, 1.0, 12.0 / 16.0, 11.0 / 16.0));
    private static final VoxelShape EAST_SHAPE = Shapes.create(new AABB(0.0, 5.0 / 16.0, 5.0 / 16.0, 5.0 / 16.0, 12.0 / 16.0, 11.0 / 16.0));


    public CocoonBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(HATCH, 0));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if (itemStack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block.defaultBlockState().getLightEmission() > 8) {
                pLevel.scheduleTick(pPos, this, 200);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        pLevel.removeBlock(pPos, false);
        SilkMothEntity silkMoth = ModEntities.SILK_MOTH_ENTITY.get().create(pLevel);
        if (silkMoth != null) {
            silkMoth.moveTo(pPos.getX() + 0.5, pPos.getY(), pPos.getZ() + 0.5, 0.0F, 0.0F);

            SilkMothVariant variant = SilkMothVariant.byId(pRandom.nextInt(SilkMothVariant.values().length));
            silkMoth.setSilkMothVariant(variant);

            pLevel.addFreshEntity(silkMoth);
        }
        pLevel.gameEvent(null, GameEvent.BLOCK_DESTROY, pPos);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction = pState.getValue(FACING);
        return switch (direction) {
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pDirection.getOpposite() == pState.getValue(FACING) && !pState.canSurvive(pLevel, pPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, net.minecraft.world.level.block.Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, net.minecraft.world.level.block.Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        Direction facing = pState.getValue(FACING);
        BlockPos oppositePos = pPos.relative(facing.getOpposite());
        BlockState oppositeState = pLevel.getBlockState(oppositePos);
        return oppositeState.is(ModTags.Blocks.COCOON_LOGS);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, HATCH);
    }
}
