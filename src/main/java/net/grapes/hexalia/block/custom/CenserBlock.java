package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.CenserBlockEntity;
import net.grapes.hexalia.censer.CenserEffectHandler;
import net.grapes.hexalia.censer.HerbCombination;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CenserBlock extends BaseEntityBlock {

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public CenserBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Vec3 vec3 = pState.getOffset(pLevel, pPos);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (!pOldState.is(pState.getBlock())) {
            pLevel.scheduleTick(pPos, this, 1);
        }
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof CenserBlockEntity censer) {
            censer.tick(pLevel, pPos, pState);
        }
        pLevel.scheduleTick(pPos, this, 1);
    }

    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (level instanceof Level world && oldState.getBlock() == newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CenserBlockEntity censer) {
                boolean wasLit = oldState.getValue(LIT);
                boolean isLit = newState.getValue(LIT);

                if (wasLit && !isLit) {
                    // Turning off the censer: clear rendering items
                    censer.clearItems();
                    world.sendBlockUpdated(pos, oldState, newState, Block.UPDATE_ALL);
                }
            }
        }
        super.onBlockStateChange(level, pos, oldState, newState);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack heldItem = pPlayer.getItemInHand(pHand);
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);

        if (!(blockEntity instanceof CenserBlockEntity censer)) {
            return InteractionResult.PASS;
        }

        // Handle lighting
        if (heldItem.getItem() instanceof FlintAndSteelItem && !pState.getValue(LIT)) {
            ItemStack herb1 = censer.getItem(0);
            ItemStack herb2 = censer.getItem(1);

            if (herb1.isEmpty() || herb2.isEmpty()) {
                if (pLevel.isClientSide()) {
                    pPlayer.displayClientMessage(Component.translatable("message.hexalia.censer_not_full"), true);
                }
                return InteractionResult.FAIL;
            }

            HerbCombination combo = new HerbCombination(herb1.getItem(), herb2.getItem());
            if (!CenserEffectHandler.isValidCombination(herb1.getItem(), herb2.getItem())) {
                if (pLevel.isClientSide()) {
                    pPlayer.displayClientMessage(Component.translatable("message.hexalia.invalid_herb_combination"), true);
                }
                return InteractionResult.FAIL;
            }

            if (!pLevel.isClientSide()) {
                // Send effect message before activating
                sendEffectActivationMessage(pLevel, pPos, combo, pPlayer);

                censer.setActiveCombination(combo);
                censer.clearItems();
                pLevel.setBlockAndUpdate(pPos, pState.setValue(LIT, true));
                censer.setBurnTime(CenserBlockEntity.MAX_BURN_TIME);
                CenserEffectHandler.startEffect(pLevel, pPos, combo);
            }

            heldItem.hurtAndBreak(1, pPlayer, p -> p.broadcastBreakEvent(pHand));
            pLevel.playSound(null, pPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, pLevel.random.nextFloat() * 0.4F + 0.8F);
            return InteractionResult.SUCCESS;
        }

        else if (heldItem.getItem() instanceof ShovelItem && pState.getValue(LIT)) {
            if (!pLevel.isClientSide()) {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(LIT, false));
                censer.setBurnTime(0);
            }
            return InteractionResult.SUCCESS;
        }

        if (!pState.getValue(LIT)) {
            if (heldItem.isEmpty()) {
                // Retrieve items with empty hand
                for (int i = 0; i < censer.getItems().size(); i++) {
                    ItemStack stackInSlot = censer.getItem(i);
                    if (!stackInSlot.isEmpty()) {
                        ItemStack removedStack = censer.removeStack(i);
                        if (!pPlayer.getInventory().add(removedStack)) {
                            pPlayer.drop(removedStack, false);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            } else {
                // Only allow items with HERBS tag to be stored
                if (heldItem.is(ModTags.Items.HERBS)) {
                    for (int i = 0; i < censer.getItems().size(); i++) {
                        if (censer.getItem(i).isEmpty()) {
                            ItemStack stackToInsert = heldItem.copy();
                            stackToInsert.setCount(1);
                            censer.setItem(i, stackToInsert);

                            if (!pPlayer.isCreative()) {
                                heldItem.shrink(1);
                            }

                            pLevel.playSound(null, pPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.5f, 1.0f);
                            return InteractionResult.SUCCESS;
                        }
                    }
                } else {
                    if (pLevel.isClientSide()) {
                        pPlayer.displayClientMessage(Component.translatable("message.hexalia.invalid_item"), true);
                    }
                    return InteractionResult.FAIL;
                }
            }
        }

        return InteractionResult.PASS;
    }

    private void sendEffectActivationMessage(Level level, BlockPos pos, HerbCombination combo, Player activatingPlayer) {
        String messageKey = CenserEffectHandler.getMessageKeyForCombination(combo);
        AABB area = new AABB(pos).inflate(CenserEffectHandler.AREA_RADIUS);

        for (Player player : level.getEntitiesOfClass(Player.class, area)) {
            if (!player.getUUID().equals(activatingPlayer.getUUID()) && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.displayClientMessage(Component.translatable(messageKey), true);
            }
        }

        if (!level.isClientSide() && activatingPlayer instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(Component.translatable(messageKey), true);
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(LIT)) {
            // Play crackling sounds (same as campfire)
            if (pRandom.nextInt(10) == 0) {
                pLevel.playLocalSound(
                        pPos.getX() + 0.5D,
                        pPos.getY() + 0.5D,
                        pPos.getZ() + 0.5D,
                        SoundEvents.CAMPFIRE_CRACKLE,
                        SoundSource.BLOCKS,
                        0.5F + pRandom.nextFloat(),
                        pRandom.nextFloat() * 0.7F + 0.6F,
                        false
                );
            }

            // Main smoke particles (always visible, frequent like campfire)
            pLevel.addAlwaysVisibleParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    true,
                    pPos.getX() + 0.5D + pRandom.nextDouble() / 3.0D * (pRandom.nextBoolean() ? 1 : -1),
                    pPos.getY() + pRandom.nextDouble() + pRandom.nextDouble(),
                    pPos.getZ() + 0.5D + pRandom.nextDouble() / 3.0D * (pRandom.nextBoolean() ? 1 : -1),
                    0.0D,
                    0.07D,
                    0.0D
            );

            // Secondary smoke particles (denser at source)
            pLevel.addParticle(
                    ParticleTypes.SMOKE,
                    pPos.getX() + 0.5D + pRandom.nextDouble() / 4.0D * (pRandom.nextBoolean() ? 1 : -1),
                    pPos.getY() + 0.4D,
                    pPos.getZ() + 0.5D + pRandom.nextDouble() / 4.0D * (pRandom.nextBoolean() ? 1 : -1),
                    0.0D,
                    0.005D,
                    0.0D
            );

            // Rare lava particles (less frequent than before)
            if (pRandom.nextInt(25) == 0) {
                pLevel.addParticle(
                        ParticleTypes.LAVA,
                        pPos.getX() + 0.5D,
                        pPos.getY() + 0.3D,
                        pPos.getZ() + 0.5D,
                        pRandom.nextFloat() / 8.0F,  // Very subtle movement
                        0.0D,
                        pRandom.nextFloat() / 8.0F
                );
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CenserBlockEntity censer) {  // Changed from ShelfBlockEntity
                if (world instanceof ServerLevel) {
                    Containers.dropContents(world, pos, censer.getItems());  // Simplified drop
                }
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState()
                .setValue(FACING, pContext.getHorizontalDirection().getOpposite())
                .setValue(LIT, false);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LIT);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CenserBlockEntity(pos, state);
    }
}
