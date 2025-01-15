package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.SaltBlockEntity;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

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
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof SaltBlockEntity saltBlockEntity) {
                Containers.dropContents(pLevel, pPos, saltBlockEntity);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        ItemStack heldItem = pPlayer.getItemInHand(pHand);

        if (!(blockEntity instanceof SaltBlockEntity saltBlockEntity)) {
            return InteractionResult.PASS;
        }

        if (pHand == InteractionHand.MAIN_HAND) {
            if (!heldItem.isEmpty() && saltBlockEntity.isEmpty() && !heldItem.is(ModItems.HEX_FOCUS.get())) {
                if (saltBlockEntity.addStack(heldItem.split(1))) {
                    playItemSound(pLevel, pPos);
                    return InteractionResult.sidedSuccess(pLevel.isClientSide);
                }
            }

            if (!saltBlockEntity.isEmpty() && heldItem.isEmpty()) {
                removeItemFromBlock(pLevel, saltBlockEntity, pPlayer);
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    private void removeItemFromBlock(Level pLevel, SaltBlockEntity saltBlockEntity, Player player) {
        ItemStack stack = saltBlockEntity.removeStack();
        if (!player.getInventory().add(stack)) {
            Containers.dropItemStack(pLevel, player.getX(), player.getY(), player.getZ(), stack);
        }
        playItemSound(pLevel, saltBlockEntity.getBlockPos());
    }

    private void playItemSound(Level pLevel, BlockPos pos) {
        pLevel.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5f, 0.5f);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SaltBlockEntity(pPos, pState);
    }
}
