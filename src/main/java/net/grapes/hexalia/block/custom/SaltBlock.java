package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.SaltBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SaltBlock extends BaseEntityBlock {

    protected static final VoxelShape SHAPE = Shapes.or(Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 13.0));

    public SaltBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockPos = pPos.below();
        BlockState blockState = pLevel.getBlockState(blockPos);
        return this.canRunOnTop(pLevel, blockPos, blockState);
    }

    private boolean canRunOnTop(BlockGetter world, BlockPos pos, BlockState floor) {
        return floor.isFaceSturdy(world, pos, Direction.UP) || floor.getBlock() == Blocks.HOPPER;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState blockState, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(blockState, builder);
        BlockEntity blockE = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockE instanceof SaltBlockEntity saltBlockEntity) {
            drops.add(saltBlockEntity.getItem(0));
        }
        return drops;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = InteractionResult.PASS;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof SaltBlockEntity saltBlockEntity)) {
            return result;
        }

        if (saltBlockEntity.isEmpty()) {
            result = addItemFromHand(world, saltBlockEntity, player, hand);
        } else if (hand.equals(InteractionHand.MAIN_HAND)) {
            removeItemFromBlock(world, saltBlockEntity, player);
            result = InteractionResult.SUCCESS;
        }

        return result;
    }


    private InteractionResult addItemFromHand(Level world, SaltBlockEntity saltBlockEntity, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack offHandItem = player.getOffhandItem();

        if (!offHandItem.isEmpty()) {
            if (hand.equals(InteractionHand.MAIN_HAND) && !(heldItem.getItem() instanceof BlockItem)) {
                return InteractionResult.PASS;
            }
            if (hand.equals(InteractionHand.OFF_HAND)) {
                return InteractionResult.PASS;
            }
        }
        if (heldItem.isEmpty()) {
            return InteractionResult.PASS;
        } else if (saltBlockEntity.addStack(player.getAbilities().instabuild ? heldItem.copy() : heldItem)) {
            playPlaceSound(world, saltBlockEntity.getBlockPos());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private void removeItemFromBlock(Level world, SaltBlockEntity saltBlockEntity, Player player) {
        BlockPos pos = saltBlockEntity.getBlockPos();
        if (player.getAbilities().instabuild) {
            saltBlockEntity.removeStack();
        } else if (!player.getInventory().add(saltBlockEntity.removeStack())) {
            Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), saltBlockEntity.removeStack());
        }
        playRemoveSound(world, pos);
    }


    private void playPlaceSound(Level pLevel, BlockPos pPos) {
        pLevel.playSound(null, pPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    private void playRemoveSound(Level pLevel, BlockPos pPos) {
        pLevel.playSound(null, pPos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SaltBlockEntity(pPos, pState);
    }
}
