package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.ShelfBlockEntity;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShelfBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static final Map<Direction, VoxelShape> SHAPES = createShapes();

    public ShelfBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    private static VoxelShape createShape() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.25, 0.5, 1, 0.3125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.125, 0.875, 1, 0.25, 1));
        return shape;
    }

    private static Map<Direction, VoxelShape> createShapes() {
        Map<Direction, VoxelShape> shapes = new HashMap<>();
        VoxelShape baseShape = createShape();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            shapes.put(direction, rotateShape(Direction.NORTH, direction, baseShape));
        }
        return shapes;
    }

    private static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};
        int times = (to.getHorizontal() - from.getHorizontal() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) ->
                    buffer[1] = VoxelShapes.union(buffer[1],
                            VoxelShapes.cuboid(1.0 - maxZ, minY, minX,
                                    1.0 - minZ, maxY, maxX))
            );
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }
        return buffer[0];
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.get(FACING));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                              Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ShelfBlockEntity shelf) {
            Vec3d hitPos = hit.getPos();
            Direction facing = state.get(FACING);

            double x = hitPos.getX() - pos.getX();
            double y = hitPos.getY() - pos.getY();
            double z = hitPos.getZ() - pos.getZ();

            double relativeX, relativeZ;

            switch (facing) {
                case NORTH:
                    relativeX = 1.0 - x;
                    relativeZ = z;
                    break;
                case SOUTH:
                    relativeX = x;
                    relativeZ = 1.0 - z;
                    break;
                case EAST:
                    relativeX = 1.0 - z;
                    relativeZ = 1.0 - x;
                    break;
                case WEST:
                    relativeX = z;
                    relativeZ = x;
                    break;
                default:
                    return ActionResult.PASS;
            }

            if (relativeZ < 0.5 || relativeZ > 1.0 || y < 0.25 || y > 0.75) {
                return ActionResult.PASS;
            }

            int row;
            if (relativeZ < 0.75) {
                row = 1;
            } else {
                row = 0;
            }

            int column;
            if (relativeX < 0.33) {
                column = 0;
            } else if (relativeX < 0.67) {
                column = 1;
            } else {
                column = 2;
            }

            int slot = column + (row * 3);

            ItemStack heldItem = player.getStackInHand(hand);
            ItemStack shelfItem = shelf.getStack(slot);

            if (!shelfItem.isEmpty()) {
                ItemStack removedItem = shelf.removeStack(slot);
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);

                if (!player.getInventory().insertStack(removedItem)) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), removedItem);
                }

                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                return ActionResult.success(world.isClient);
            } else if (isValidItem(heldItem)) {
                ItemStack toPlace;
                if (player.isCreative()) {
                    toPlace = heldItem.copy();
                    toPlace.setCount(1);
                } else {
                    toPlace = heldItem.split(1);
                }
                shelf.setStack(slot, toPlace);
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                return ActionResult.success(world.isClient);
            }
        }
        return ActionResult.PASS;
    }

    private boolean isValidItem(ItemStack stack) {
        return !stack.isEmpty() && stack.isIn(ModTags.Items.BREWS)|| stack.isOf(Items.POTION)
                || stack.isOf(Items.LINGERING_POTION) || stack.isOf(Items.SPLASH_POTION);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShelfBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction clickedFace = ctx.getSide();
        if (clickedFace.getAxis() == Direction.Axis.Y) {
            return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        }
        return this.getDefaultState().with(FACING, clickedFace);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = state.get(FACING).getOpposite();
        BlockPos supportPos = pos.offset(direction);
        BlockState supportState = world.getBlockState(supportPos);
        return supportState.isSideSolidFullSquare(world, supportPos, direction.getOpposite());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.scheduleBlockTick(pos, this, 1);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ShelfBlockEntity shelf) {
                if (world instanceof ServerWorld) {
                    for (int i = 0; i < 6; i++) {
                        ItemStack stack = shelf.getStack(i);
                        if (!stack.isEmpty()) {
                            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                        }
                    }
                }
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("tooltip.hexalia.shelf").formatted(Formatting.GRAY));
    }
}