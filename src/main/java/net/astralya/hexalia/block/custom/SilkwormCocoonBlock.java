package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.entity.custom.variant.SilkMothVariant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class SilkwormCocoonBlock extends Block {

    public static final DirectionProperty FACING = Properties.FACING;

    private static final VoxelShape NORTH_SHAPE = VoxelShapes.cuboid(new Box(5.0 / 16.0, 5.0 / 16.0, 11.0 / 16.0, 11.0 / 16.0, 12.0 / 16.0, 1.0));
    private static final VoxelShape SOUTH_SHAPE = VoxelShapes.cuboid(new Box(5.0 / 16.0, 5.0 / 16.0, 0.0, 11.0 / 16.0, 12.0 / 16.0, 5.0 / 16.0));
    private static final VoxelShape WEST_SHAPE  = VoxelShapes.cuboid(new Box(11.0 / 16.0, 5.0 / 16.0, 5.0 / 16.0, 1.0, 12.0 / 16.0, 11.0 / 16.0));
    private static final VoxelShape EAST_SHAPE  = VoxelShapes.cuboid(new Box(0.0, 5.0 / 16.0, 5.0 / 16.0, 5.0 / 16.0, 12.0 / 16.0, 11.0 / 16.0));

    public SilkwormCocoonBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            return ItemActionResult.SUCCESS;
        }
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block.getDefaultState().getLuminance() > 8) {
                world.scheduleBlockTick(pos, this, 200);
                return ItemActionResult.SUCCESS;
            }
        }
        return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.removeBlock(pos, false);
        SilkMothEntity silkMoth = ModEntities.SILK_MOTH_ENTITY.create(world);
        if (silkMoth != null) {
            silkMoth.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
            SilkMothVariant variant = SilkMothVariant.byId(random.nextInt(SilkMothVariant.values().length));
            silkMoth.setVariant(variant);
            world.spawnEntity(silkMoth);
        }
        world.emitGameEvent(null, GameEvent.BLOCK_DESTROY, pos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        return switch (direction) {
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext context) {
        Direction face = context.getSide();
        if (face.getAxis().isVertical()) {
            return null;
        }
        return this.getDefaultState().with(FACING, face);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == state.get(FACING).getOpposite() && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction facing = state.get(FACING);
        if (facing.getAxis().isVertical()) {
            return false;
        }
        BlockPos attachedPos = pos.offset(facing.getOpposite());
        return world.getBlockState(attachedPos).isIn(BlockTags.LOGS);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}