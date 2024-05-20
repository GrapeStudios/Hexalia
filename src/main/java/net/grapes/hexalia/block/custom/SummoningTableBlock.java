package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.DisplayBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SummoningTableBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE = createShape();

    public SummoningTableBlock(Settings settings) {
        super(settings);
    }

    private static VoxelShape createShape() {
        return VoxelShapes.union(
                VoxelShapes.cuboid(0.1875, 0, 0.1875, 0.8125, 0.125, 0.8125),
                VoxelShapes.cuboid(0.25, 0.125, 0.25, 0.75, 0.625, 0.75),
                VoxelShapes.cuboid(0.1875, 0.625, 0.1875, 0.8125, 0.6875, 0.8125),
                VoxelShapes.cuboid(0.125, 0.6875, 0.125, 0.875, 0.8125, 0.875),
                VoxelShapes.cuboid(0.11875000000000002, 0.8186874747276306, 0.24374999850988388, 0.8875, 0.8186874747276306, 0.7437499985098839),
                VoxelShapes.cuboid(0.11875, 0.4437499403953552, 0.24374999850988388, 0.11875, 0.8187499403953552, 0.7437499985098839),
                VoxelShapes.cuboid(0.8874374985694886, 0.4437499403953552, 0.24374999850988388, 0.8874374985694886, 0.8187499403953552, 0.7437499985098839)
        );
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DisplayBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof DisplayBlockEntity displayBlockEntity) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), displayBlockEntity.getStoredItem());
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ActionResult result = ActionResult.PASS;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof DisplayBlockEntity displayBlockEntity)) {
            return result;
        }

        if (displayBlockEntity.isEmpty()) {
            result = addItemFromHand(world, displayBlockEntity, player, hand);
        } else if (hand.equals(Hand.MAIN_HAND)) {
            removeItemFromTable(world, displayBlockEntity, player);
            result = ActionResult.SUCCESS;
        }

        return result;
    }

    private ActionResult addItemFromHand(World world, DisplayBlockEntity displayBlockEntity, PlayerEntity player, Hand hand) {
        ItemStack heldItem = player.getStackInHand(hand);
        ItemStack offHandItem = player.getOffHandStack();

        if (!offHandItem.isEmpty()) {
            if (hand.equals(Hand.MAIN_HAND) && !(heldItem.getItem() instanceof BlockItem)) {
                return ActionResult.PASS;
            }
            if (hand.equals(Hand.OFF_HAND)) {
                return ActionResult.PASS;
            }
        }
        if (heldItem.isEmpty()) {
            return ActionResult.PASS;
        } else if (displayBlockEntity.addItem(player.getAbilities().creativeMode ? heldItem.copy() : heldItem)) {
            playPlaceSound(world, displayBlockEntity.getPos());
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private void removeItemFromTable(World world, DisplayBlockEntity displayBlockEntity, PlayerEntity player) {
        BlockPos pos = displayBlockEntity.getPos();
        if (player.isCreative()) {
            displayBlockEntity.removeItem();
        } else if (!player.getInventory().insertStack(displayBlockEntity.removeItem())) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), displayBlockEntity.removeItem());
        }
        playRemoveSound(world, pos);
    }

    private void playPlaceSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1f, 1f);
    }

    private void playRemoveSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
    }
}
