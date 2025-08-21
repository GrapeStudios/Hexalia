package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.block.ModBlocks;
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

import java.util.Set;

public class HexFocusItem extends Item {
    private static final int BLOCK_BREAK_EVENT_ID = 2001;
    private static final Set<Block> VALID_BLOCKS = Set.of(Blocks.COBBLED_DEEPSLATE, Blocks.DEEPSLATE);

    public HexFocusItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level pLevel = pContext.getLevel();
        Player pPlayer = pContext.getPlayer();
        BlockPos pPos = pContext.getClickedPos();
        BlockState pState = pLevel.getBlockState(pPos);
        Block block = pState.getBlock();

        if (pPlayer != null && !pLevel.isClientSide()) {
            if (pPlayer.getCooldowns().isOnCooldown(this)) {
                return InteractionResult.FAIL;
            }

            if (VALID_BLOCKS.contains(block)) {
                pLevel.levelEvent(BLOCK_BREAK_EVENT_ID, pPos, Block.getId(pState));
                pLevel.playSound(null, pPos, SoundEvents.AMETHYST_BLOCK_HIT,
                        SoundSource.BLOCKS, 1.0f, 1.0f);
                pLevel.setBlockAndUpdate(pPos, ModBlocks.RITUAL_TABLE.get().defaultBlockState());
                pPlayer.getCooldowns().addCooldown(this, 60);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}