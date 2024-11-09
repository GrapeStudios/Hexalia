package net.grapes.hexalia.worldgen.gen.decorator;

import com.mojang.serialization.Codec;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.block.custom.CocoonBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class CocoonTreeDecorator extends TreeDecorator {
    public static final Codec<CocoonTreeDecorator> CODEC = Codec.unit(CocoonTreeDecorator::new);

    public CocoonTreeDecorator() {}

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecorators.COCOON.get();
    }

    @Override
    public void place(Context pContext) {
        RandomSource random = pContext.random();
        for (BlockPos pPos : pContext.logs()) {
            Direction[] directions = {Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
            Direction direction = directions[random.nextInt(directions.length)];
            BlockPos blockPos = pPos.offset(direction.getNormal());
            if (pContext.isAir(blockPos) && pContext.isAir(blockPos.below())) {
                pContext.setBlock(blockPos, ModBlocks.SILKWORM_COCOON.get().defaultBlockState().setValue(CocoonBlock.FACING, direction));
                break;
            }
        }
    }
}
