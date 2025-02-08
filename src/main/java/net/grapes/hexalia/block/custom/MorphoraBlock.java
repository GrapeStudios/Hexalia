package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MorphoraBlock extends EnchantedPlantBlock{

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

    public MorphoraBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.MAGMA_BLOCK) || floor.isSolid();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, 80);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos belowPos = pos.down();
        boolean converted = false;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos targetPos = belowPos.add(dx, 0, dz);
                BlockState targetState = world.getBlockState(targetPos);
                Block targetBlock = CONVERSION_MAP.get(targetState.getBlock());

                if (targetBlock != null) {
                    converted = true;
                    world.setBlockState(targetPos, pushEntitiesUpBeforeBlockChange(targetState, targetBlock.getDefaultState(), world, targetPos));
                    world.syncWorldEvent(2001, targetPos, Block.getRawIdFromState(targetState));
                }
            }
        }

        if (converted) {
            world.playSound(null, pos, ModSounds.CONVERSION, SoundCategory.BLOCKS, 0.02F, 1.0F);
            world.breakBlock(pos, false);
        }

    }


}
