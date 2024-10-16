package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
public class InfusedDirtBlock extends Block {

    public InfusedDirtBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if (itemStack.getItem() instanceof HoeItem && pState.getBlock() == ModBlocks.INFUSED_DIRT.get() &&
                pHit.getDirection() != Direction.DOWN && pLevel.getBlockState(pPos.above()).isAir()) {
            pLevel.playSound(pPlayer, pPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (!pLevel.isClientSide) {
                pLevel.setBlockAndUpdate(pPos, ModBlocks.INFUSED_FARMLAND.get().defaultBlockState());
                itemStack.hurtAndBreak(1, pPlayer,  p -> p.broadcastBreakEvent(pHand));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

}
