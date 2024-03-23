package net.grapes.hexalia.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SaltLampBlock extends Block {
    public static final VoxelShape SHAPE = VoxelShapes.union(Block.createCuboidShape(4.0, 0, 4.0, 12.0, 10.0, 12.0),
            Block.createCuboidShape(5.0, 2.0, 5.0, 11.0, 11.0, 11.0));

    public SaltLampBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
