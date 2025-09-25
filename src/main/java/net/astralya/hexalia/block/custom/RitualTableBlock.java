package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.entity.RitualTableBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class RitualTableBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final VoxelShape SHAPE = createShape();

    public RitualTableBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    private static VoxelShape createShape() {
        return Shapes.or(
                Shapes.box(0.1875, 0, 0.1875, 0.8125, 0.125, 0.8125),
                Shapes.box(0.25, 0.125, 0.25, 0.75, 0.625, 0.75),
                Shapes.box(0.1875, 0.625, 0.1875, 0.8125, 0.6875, 0.8125),
                Shapes.box(0.125, 0.6875, 0.125, 0.875, 0.8125, 0.875)
        );
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RitualTableBlockEntity(pPos, pState);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof RitualTableBlockEntity ritualTable) {
                Containers.dropContents(pLevel, pPos, ritualTable);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);

        if (!(blockEntity instanceof RitualTableBlockEntity ritualTableBlockEntity)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = pPlayer.getItemInHand(pHand);

        if (pHand == InteractionHand.MAIN_HAND) {
            if (!ritualTableBlockEntity.isEmpty() && heldItem.isEmpty()) {
                removeItemFromBlock(pLevel, ritualTableBlockEntity, pPlayer);
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }

            if (!heldItem.isEmpty() && ritualTableBlockEntity.isEmpty() && !heldItem.is(ModItems.HEX_FOCUS.get())) {
                if (ritualTableBlockEntity.addStack(heldItem.split(1))) {
                    playItemSound(pLevel, pPos);
                    spawnParticleEffect(pLevel, pPos, ParticleTypes.POOF, 5, 10);
                    return InteractionResult.sidedSuccess(pLevel.isClientSide);
                }
            }

            if (heldItem.is(ModItems.HEX_FOCUS.get())) {
                if (ritualTableBlockEntity.canStartRitual(pLevel, pPos)) {
                    boolean success = ritualTableBlockEntity.startRitual();
                    if (success) {
                        spawnSuccessEffects(pLevel, pPos);
                    }
                }
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    private void removeItemFromBlock(Level pLevel, RitualTableBlockEntity ritualTableBlockEntity, Player player) {
        ItemStack stack = ritualTableBlockEntity.removeStack();
        if (!player.getInventory().add(stack)) {
            Containers.dropItemStack(pLevel, player.getX(), player.getY(), player.getZ(), stack);
        }
        playItemSound(pLevel, ritualTableBlockEntity.getBlockPos());
    }

    private void spawnSuccessEffects(Level pLevel, BlockPos pPos) {
        spawnParticleEffect(pLevel, pPos, ParticleTypes.ENCHANT, 10, 20);
        spawnParticleEffect(pLevel, pPos, ModParticleType.LEAVES.get(), 10, 20);
        playRitualSound(pLevel, pPos);
    }

    private void spawnParticleEffect(Level pLevel, BlockPos pPos, SimpleParticleType particleType, int minParticles, int maxParticles) {
        int particleCount = ThreadLocalRandom.current().nextInt(minParticles, maxParticles);
        for (int i = 0; i < particleCount; i++) {
            double offsetX = ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
            double offsetY = ThreadLocalRandom.current().nextDouble(0, 0.5);
            double offsetZ = ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
            pLevel.addParticle(particleType, pPos.getX() + 0.5 + offsetX, pPos.getY() + 1.0 + offsetY, pPos.getZ() + 0.5 + offsetZ, 0, 0, 0);
        }
    }

    private void playItemSound(Level pLevel, BlockPos pos) {
        pLevel.playSound(null, pos, SoundEvents.CHISELED_BOOKSHELF_PICKUP_ENCHANTED, SoundSource.BLOCKS, 0.8f, 0.5f);
        pLevel.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8f, 0.5f);
    }

    private void playRitualSound(Level pLevel, BlockPos pos) {
        pLevel.playSound(null, pos, ModSoundEvents.RITUAL_SUCCESS.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
    }
}
