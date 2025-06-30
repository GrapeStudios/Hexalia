package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.AstrylisBlockEntity;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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

public class AstrylisBlock extends EnchantedPlantBlock implements EntityBlock {

    public AstrylisBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AstrylisBlockEntity astrylisBlockEntity && astrylisBlockEntity.isActive()) {
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
        ItemStack itemStack = player.getItemInHand(hand);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AstrylisBlockEntity astrylis) {
            if (itemStack.isEmpty()) {
                if (!astrylis.isActive()) {
                    player.displayClientMessage(
                            Component.translatable("message.hexalia.astrylis.inactive"),
                            true
                    );
                }
                return InteractionResult.SUCCESS;
            }
            if (itemStack.getItem() == ModItems.CELESTIAL_CRYSTAL.get()) {
                if (!astrylis.isActive()) {
                    astrylis.activate(level.getGameTime());
                    level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 1.0f, 1.0f);
                    if (!player.isCreative()) {
                        itemStack.shrink(1);
                    }
                    player.displayClientMessage(
                            Component.translatable("message.hexalia.astrylis.activation"),
                            true
                    );
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AstrylisBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof AstrylisBlockEntity astrylisBlockEntity) {
                AstrylisBlockEntity.tick(level, pos, state, astrylisBlockEntity);
            }
        };
    }
}