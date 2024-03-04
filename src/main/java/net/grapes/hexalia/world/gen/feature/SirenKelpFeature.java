package net.grapes.hexalia.world.gen.feature;

import com.mojang.serialization.Codec;
import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallSeagrassBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SirenKelpFeature extends Feature<ProbabilityConfig> {
    public SirenKelpFeature(Codec<ProbabilityConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<ProbabilityConfig> context) {
        boolean bl = false;
        Random random = context.getRandom();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        ProbabilityConfig probabilityConfig = context.getConfig();
        int i = random.nextInt(8) - random.nextInt(8);
        int j = random.nextInt(8) - random.nextInt(8);
        int k = structureWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR, blockPos.getX() + i, blockPos.getZ() + j);
        BlockPos blockPos2 = new BlockPos(blockPos.getX() + i, k, blockPos.getZ() + j);
        if (structureWorldAccess.getBlockState(blockPos2).isOf(Blocks.WATER)) {
            BlockState blockState;
            boolean bl2 = random.nextDouble() < (double)probabilityConfig.probability;
                BlockState blockState2 = blockState = ModBlocks.SIREN_KELP.getDefaultState();
            if (blockState.canPlaceAt(structureWorldAccess, blockPos2)) {
                if (bl2) {
                    BlockState blockState22 = (BlockState)blockState.with(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
                    BlockPos blockPos3 = blockPos2.up();
                    if (structureWorldAccess.getBlockState(blockPos3).isOf(Blocks.WATER)) {
                        structureWorldAccess.setBlockState(blockPos2, blockState, Block.NOTIFY_LISTENERS);
                        structureWorldAccess.setBlockState(blockPos3, blockState22, Block.NOTIFY_LISTENERS);
                    }
                } else {
                    structureWorldAccess.setBlockState(blockPos2, blockState, Block.NOTIFY_LISTENERS);
                }
                bl = true;
            }
        }
        return bl;
    }
}
