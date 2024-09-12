package net.grapes.hexalia.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class GhostFernBlock extends PlantBlock {
    public static final VoxelShape SHAPE =  VoxelShapes.union(
            VoxelShapes.cuboid(0.125, 0, 0.1875, 0.875, 0.25, 0.8125)
    );
    public GhostFernBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
