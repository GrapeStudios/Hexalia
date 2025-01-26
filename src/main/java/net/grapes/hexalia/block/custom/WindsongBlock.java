package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.TidalBloomBlockEntity;
import net.grapes.hexalia.block.entity.WindsongBlockEntity;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WindsongBlock extends BushBlock implements EntityBlock {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0.125, 0, 0.0625, 0.9375, 0.4375, 0.9375)
    );

    public WindsongBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, false));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.getItemInHand(pHand).getItem() == ModItems.HEX_FOCUS.get()) {
            if (!pLevel.isClientSide) {
                BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
                if (blockEntity instanceof WindsongBlockEntity windsongBlockEntity && !windsongBlockEntity.isActive()) {
                    windsongBlockEntity.activate();
                    pLevel.playSound(null, pPos, ModSounds.WIND_BURST.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(ACTIVE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new WindsongBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof WindsongBlockEntity windflowerBlockEntity) {
                windflowerBlockEntity.tick(level, pos, state);
            }
        };
    }

}
