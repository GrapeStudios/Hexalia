package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import javax.annotation.Nullable;

public class InfusedDirtBlock extends Block {

    public InfusedDirtBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    @Nullable
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (toolAction.equals(ToolActions.HOE_TILL) && context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
            return ModBlocks.INFUSED_FARMLAND.get().defaultBlockState();
        }
        return null;
    }
}
