package net.grapes.hexalia.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.block.custom.CocoonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class CocoonTreeDecorator extends TreeDecorator {
    public static final Codec<CocoonTreeDecorator> CODEC = Codec.unit(CocoonTreeDecorator::new);

    public CocoonTreeDecorator() {
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return ModTreeDecorators.COCOON_TREE;
    }

    @Override
    public void generate(Generator generator) {
        Random random = generator.getRandom();
        for (BlockPos pos : generator.getLogPositions()) {
            Direction[] directions = {Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
            Direction direction = directions[random.nextInt(directions.length)];
            BlockPos blockPos = pos.offset(direction);
            if (generator.isAir(blockPos) && generator.isAir(blockPos.down())) {
                generator.replace(blockPos, ModBlocks.SILKWORM_COCOON.getDefaultState().with(CocoonBlock.FACING, direction));
                break;
            }
        }
    }
}
