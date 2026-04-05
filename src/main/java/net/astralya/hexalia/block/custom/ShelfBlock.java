package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.entity.custom.ShelfBlockEntity;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

    public ShelfBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
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
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction clickedFace = ctx.getSide();
        if (clickedFace.getAxis() == Direction.Axis.Y) {
            return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        }
        return getDefaultState().with(FACING, clickedFace);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = state.get(FACING).getOpposite();
        BlockPos supportPos = pos.offset(direction);
        BlockState supportState = world.getBlockState(supportPos);
        return supportState.isSideSolidFullSquare(world, supportPos, direction.getOpposite());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof ShelfBlockEntity shelf)) {
            return ActionResult.PASS;
        }

        Vec3d hitPos = hit.getPos();
        Direction facing = state.get(FACING);

        double x = hitPos.x - pos.getX();
        double y = hitPos.y - pos.getY();
        double z = hitPos.z - pos.getZ();

        double relativeX;
        double relativeZ;

        switch (facing) {
            case NORTH -> {
                relativeX = 1.0 - x;
                relativeZ = z;
            }
            case SOUTH -> {
                relativeX = x;
                relativeZ = 1.0 - z;
            }
            case EAST -> {
                relativeX = 1.0 - z;
                relativeZ = 1.0 - x;
            }
            case WEST -> {
                relativeX = z;
                relativeZ = x;
            }
            default -> {
                return ActionResult.PASS;
            }
        }

        if (relativeZ < 0.5 || relativeZ > 1.0 || y < 0.25 || y > 0.75) {
            return ActionResult.PASS;
        }

        int row = relativeZ < 0.75 ? 1 : 0;

        int column;
        if (relativeX < 0.33) {
            column = 0;
        } else if (relativeX < 0.67) {
            column = 1;
        } else {
            column = 2;
        }

        int slot = column + row * 3;
        ItemStack heldItem = player.getStackInHand(hand);
        ItemStack shelfItem = shelf.getStack(slot);

        if (!shelfItem.isEmpty()) {
            if (world.isClient) {
                return ActionResult.SUCCESS;
            }

            ItemStack removed = shelf.removeStack(slot);
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (!player.getInventory().insertStack(removed)) {
                Block.dropStack(world, pos, removed);
            }

            shelf.markDirty();
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return ActionResult.CONSUME;
        }

        if (isValidItem(heldItem)) {
            if (world.isClient) {
                return ActionResult.SUCCESS;
            }

            ItemStack toPlace = player.getAbilities().creativeMode ? heldItem.copyWithCount(1) : heldItem.split(1);
            shelf.setStack(slot, toPlace);
            shelf.markDirty();
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ShelfBlockEntity shelf && !world.isClient) {
                for (ItemStack stack : shelf.getItems()) {
                    if (!stack.isEmpty()) {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
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

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShelfBlockEntity(pos, state);
    }

    private static VoxelShape createShape() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0, 0.25, 0.5, 1.0, 0.3125, 1.0));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0, 0.125, 0.875, 1.0, 0.25, 1.0));
        return shape;
    }

    private static Map<Direction, VoxelShape> createShapes() {
        Map<Direction, VoxelShape> shapes = new HashMap<>();
        VoxelShape baseShape = createShape();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            shapes.put(direction, rotateShape(direction, baseShape));
        }
        return shapes;
    }

    private static VoxelShape rotateShape(Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};
        int times = (to.getHorizontal() - Direction.NORTH.getHorizontal() + 4) % 4;

        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.union(
                    buffer[1],
                    VoxelShapes.cuboid(1.0 - maxZ, minY, minX, 1.0 - minZ, maxY, maxX)
            ));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }

    private boolean isValidItem(ItemStack stack) {
        return !stack.isEmpty() && (
                stack.isIn(ModTags.Items.BREWS)
                        || stack.isOf(Items.POTION)
                        || stack.isOf(Items.LINGERING_POTION)
                        || stack.isOf(Items.SPLASH_POTION)
        );
    }
}