package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.SaltBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class SaltBlock extends BlockWithEntity implements BlockEntityProvider {

    private static final VoxelShape SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);

    public SaltBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return this.canRunOnTop(world, blockPos, blockState);
    }

    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(Blocks.HOPPER);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SaltBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof SaltBlockEntity saltBlockEntity) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), saltBlockEntity.getStoredItem());
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ActionResult result = ActionResult.PASS;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof SaltBlockEntity saltBlockEntity)) {
            return result;
        }

        if (saltBlockEntity.isEmpty()) {
            result = addItemFromHand(world, saltBlockEntity, player, hand);
        } else if (hand.equals(Hand.MAIN_HAND)) {
            removeItemFromBlock(world, saltBlockEntity, player);
            result = ActionResult.SUCCESS;
        }

        return result;
    }

    private ActionResult addItemFromHand(World world, SaltBlockEntity saltBlockEntity, PlayerEntity player, Hand hand) {
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
        } else if (saltBlockEntity.addItem(player.getAbilities().creativeMode ? heldItem.copy() : heldItem)) {
            playPlaceSound(world, saltBlockEntity.getPos());
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private void removeItemFromBlock(World world, SaltBlockEntity saltBlockEntity, PlayerEntity player) {
        BlockPos pos = saltBlockEntity.getPos();
        if (player.isCreative()) {
            saltBlockEntity.removeItem();
        } else if (!player.getInventory().insertStack(saltBlockEntity.removeItem())) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), saltBlockEntity.removeItem());
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

