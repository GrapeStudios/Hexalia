package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.entity.custom.AegifloraBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class AegifloraBlock extends EnchantedPlantBlock implements BlockEntityProvider {

    public AegifloraBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AegifloraBlockEntity(pos, state);
    }
}
