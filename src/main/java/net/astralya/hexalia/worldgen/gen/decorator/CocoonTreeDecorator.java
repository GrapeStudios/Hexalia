package net.astralya.hexalia.worldgen.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.SilkwormCocoonBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class CocoonTreeDecorator extends TreeDecorator {
    public static final MapCodec<CocoonTreeDecorator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.floatRange(0.0F, 1.0F)
                            .fieldOf("chance")
                            .forGetter(d -> d.chance)
            ).apply(instance, CocoonTreeDecorator::new)
    );

    private final float chance;

    public CocoonTreeDecorator(float chance) {
        this.chance = chance;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecorators.COCOON_TREE.get();
    }

    @Override
    public void place(Context context) {
        RandomSource random = context.random();
        for (BlockPos pos : context.logs()) {
            if (random.nextFloat() <= chance) {
                Direction[] directions = {Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
                Direction direction = directions[random.nextInt(directions.length)];
                BlockPos target = pos.relative(direction);
                if (context.isAir(target) && context.isAir(target.below())) {
                    context.setBlock(target, ModBlocks.SILKWORM_COCOON.get().defaultBlockState().setValue(SilkwormCocoonBlock.FACING, direction));
                    break;
                }
            }
        }
    }
}
