package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.particle.ModParticleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

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

    @Override
    public void fallOn(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float pFallDistance) {
        spawnBubbleParticles(pLevel, pPos);
    }

    private void spawnBubbleParticles(Level pLevel, BlockPos pPos) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 8; i++) {
            double x = pPos.getX() + 0.5 + random.nextDouble(-0.5, 0.5);
            double y = pPos.getY() + 1.0;
            double z = pPos.getZ() + 0.5 + random.nextDouble(-0.5, 0.5);
            pLevel.addParticle(ModParticleType.INFUSED_BUBBLE.get(), x, y, z, 0.0d,
                    0.05d, 0.0d);
        }
    }
}
