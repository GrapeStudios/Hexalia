package net.astralya.hexalia.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class WildCropFeature extends Feature<WildCropConfiguration> {

    public WildCropFeature(Codec<WildCropConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<WildCropConfiguration> context) {
        WildCropConfiguration config = context.getConfig();
        Random random = context.getRandom();
        BlockPos origin = context.getOrigin();
        StructureWorldAccess world = context.getWorld();

        int placed = 0;

        for (int i = 0; i < config.tries(); i++) {
            BlockPos candidate = origin.add(
                    random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread(),
                    random.nextInt(config.ySpread() * 2 + 1) - config.ySpread(),
                    random.nextInt(config.xzSpread() * 2 + 1) - config.xzSpread()
            );

            if (world.isOutOfHeightLimit(candidate)) {
                continue;
            }

            boolean positionValid = config.waterPlant()
                    ? world.getBlockState(candidate).isOf(Blocks.WATER)
                    : world.getBlockState(candidate).isAir();

            if (positionValid && config.groundPredicate().test(world, candidate.down())) {
                world.setBlockState(candidate, config.toPlace().get(random, candidate), Block.NOTIFY_LISTENERS);
                placed++;
            }
        }

        return placed > 0;
    }
}