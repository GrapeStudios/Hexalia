package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class ShroomBlock extends PlantBlock {

    public static final MapCodec<WildSunfireTomatoBlock> CODEC = createCodec(WildSunfireTomatoBlock::new);
    public static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

    public ShroomBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends PlantBlock> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        if (!canPlaceAt(getDefaultState(), ctx.getWorld(), ctx.getBlockPos())) {
            return null;
        }
        return getDefaultState();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos down = pos.down();
        BlockState soil = world.getBlockState(down);
        if (soil.isIn(BlockTags.MUSHROOM_GROW_BLOCK) || soil.isOf(ModBlocks.INFUSED_DIRT)) {
            return true;
        }
        return world.getBaseLightLevel(pos, 0) < 13 && mayPlaceOn(soil, world, down);
    }

    protected boolean mayPlaceOn(BlockState state, BlockView world, BlockPos pos) {
        return state.isOpaqueFullCube(world, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        if (!canPlaceAt(state, world, pos)) {
            dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
    }
}
