package net.grapes.hexalia.item.custom;

import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class HexFocusItem extends Item {
    public HexFocusItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level pLevel = pContext.getLevel();
        Player pPlayer = pContext.getPlayer();
        BlockPos pPos = pContext.getClickedPos();
        BlockState pState = pLevel.getBlockState(pPos);

        if (pPlayer != null && !pLevel.isClientSide()) {
            if (pPlayer.getCooldowns().isOnCooldown(this)) {
                return InteractionResult.FAIL;
            }

            pLevel.levelEvent(2001, pPos, Block.getId(pState));

            if (pState.getBlock() == Blocks.COBBLED_DEEPSLATE
                    || pState.getBlock() == Blocks.DEEPSLATE) {
                pLevel.playSound(null, pPos, SoundEvents.AMETHYST_BLOCK_HIT,
                        SoundSource.BLOCKS, 1.0f, 1.0f);
                pLevel.setBlockAndUpdate(pPos, ModBlocks.RITUAL_TABLE.get().defaultBlockState());
                pPlayer.getCooldowns().addCooldown(this, 3);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(pContext);
    }
}
