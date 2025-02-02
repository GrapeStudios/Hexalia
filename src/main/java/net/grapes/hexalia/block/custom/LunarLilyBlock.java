package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.LunarLilyBlockEntity;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class LunarLilyBlock extends MagicalFlowerBlock implements EntityBlock {

    public LunarLilyBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LunarLilyBlockEntity lunarLily && lunarLily.isActive()) {
                for (int i = 0; i < 6; i++) {
                    double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
                    double y = pos.getY() + 0.7 + random.nextDouble() * 0.3;
                    double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
                    level.addParticle(ParticleTypes.EFFECT, x, y, z, 0, 0.01, 0);
                }
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!isNight(level)) {
            return InteractionResult.FAIL;
        }

        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() == ModItems.MOON_CRYSTAL.get()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LunarLilyBlockEntity lunarLily) {
                if (!lunarLily.isActive()) {
                    lunarLily.activate(level.getGameTime());
                    level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_HIT,
                            SoundSource.BLOCKS, 1.0f, 1.0f);
                    if (!player.isCreative()) {
                        itemStack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LunarLilyBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof LunarLilyBlockEntity lunarLilyBlockEntity) {
                LunarLilyBlockEntity.tick(level, pos, state, lunarLilyBlockEntity);
            }
        };
    }

    private static boolean isNight(Level level) {
        return level.getMoonBrightness() > 0.25;
    }
}