package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class MorphoraBlock extends EnchantedPlantBlock {

    private static final Map<Block, Block> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(Blocks.MAGMA_BLOCK, Blocks.LAVA);
        CONVERSION_MAP.put(Blocks.SNOW_BLOCK, Blocks.PACKED_ICE);
        CONVERSION_MAP.put(Blocks.SOUL_SAND, Blocks.SOUL_SOIL);
        CONVERSION_MAP.put(Blocks.DIRT, Blocks.PODZOL);
        CONVERSION_MAP.put(Blocks.SAND, Blocks.RED_SAND);
        CONVERSION_MAP.put(Blocks.DIORITE, Blocks.GRANITE);
        CONVERSION_MAP.put(Blocks.GRANITE, Blocks.ANDESITE);
        CONVERSION_MAP.put(Blocks.ANDESITE, Blocks.DIORITE);
        CONVERSION_MAP.put(Blocks.ICE, Blocks.BLUE_ICE);
        CONVERSION_MAP.put(Blocks.BLACKSTONE, Blocks.CALCITE);
    }

    public MorphoraBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return floor.is(Blocks.MAGMA_BLOCK) || floor.isSolid();
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        if (!pLevel.isClientSide) {
            pLevel.scheduleTick(pPos, this, 80);
        }
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockPos belowPos = pPos.below();
        boolean converted = false;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos targetPos = belowPos.offset(dx, 0, dz);
                BlockState targetState = pLevel.getBlockState(targetPos);
                Block targetBlock = CONVERSION_MAP.get(targetState.getBlock());

                if (targetBlock != null) {
                    converted = true;
                    pLevel.setBlockAndUpdate(targetPos, pushEntitiesUp(targetState,
                            targetBlock.defaultBlockState(), pLevel, targetPos));
                    pLevel.levelEvent(2001, targetPos, Block.getId(targetState));
                }

                if (converted) {
                pLevel.playSound(null, pPos, ModSounds.CONVERSION.get(), SoundSource.BLOCKS, 0.02F, 1.0F);
                pLevel.destroyBlock(pPos, false);
                }
            }
        }
    }
}


