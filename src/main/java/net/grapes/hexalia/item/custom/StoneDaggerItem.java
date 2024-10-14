package net.grapes.hexalia.item.custom;

import net.grapes.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class StoneDaggerItem extends Item {
    public StoneDaggerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();
        BlockPos blockPos = pContext.getClickedPos();
        BlockState state = level.getBlockState(blockPos);

        if (state.getBlock() == Blocks.DARK_OAK_LOG || state.getBlock() == Blocks.ACACIA_LOG) {
            level.playSound(null, blockPos, SoundEvents.AXE_STRIP,
                    SoundSource.BLOCKS, 1.0f, 1.0f);
            Block strippedBlock = (state.getBlock() == Blocks.DARK_OAK_LOG) ? Blocks.STRIPPED_DARK_OAK_LOG :
                Blocks.STRIPPED_ACACIA_LOG;
            level.setBlock(blockPos, strippedBlock.defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)), 3);
            if (player != null) {
                player.addItem(new ItemStack(ModItems.RESIN.get()));
                ItemStack stack = pContext.getItemInHand();
                if (!player.isCreative() && stack.isDamageableItem()) {
                    stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(pContext.getHand()));
                    if (stack.isEmpty()) {
                        player.setItemInHand(pContext.getHand(), ItemStack.EMPTY);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(pContext);
    }
}
