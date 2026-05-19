package net.astralya.hexalia.worldgen.gen.decorator;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class CatkinTreeDecorator extends TreeDecorator {
  public static final MapCodec<CatkinTreeDecorator> CODEC =
      MapCodec.unit(new CatkinTreeDecorator());

  @Override
  protected TreeDecoratorType<?> type() {
    return ModTreeDecorators.CATKIN.get();
  }

  @Override
  public void place(Context context) {
    RandomSource random = context.random();
    List<BlockPos> leaves = context.leaves();
    int placed = 0;
    int max = 3 + random.nextInt(2);

    for (BlockPos pos : leaves) {
      if (placed >= max) {
        break;
      }
      BlockPos below = pos.below();
      if (context.isAir(below)) {
        context.setBlock(below, ModBlocks.COTTONWOOD_CATKIN.get().defaultBlockState());
        placed++;
      }
    }
  }
}
