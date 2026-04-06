package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.custom.CenserBlockEntity;
import net.astralya.hexalia.gameplay.censer.CenserEffectHandler;
import net.astralya.hexalia.gameplay.censer.HerbCombination;
import net.astralya.hexalia.util.ModTags;
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
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class CenserBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Shapes.box(0.0625, 0, 0, 0.3125, 0.25, 1),
            Shapes.box(0.6875, 0, 0, 0.9375, 0.25, 1),
            Shapes.box(0.3125, 0, 0, 0.6875, 0.0625, 1),
            Shapes.box(0, 0.25, 0.0625, 1, 0.375, 0.9375),
            Shapes.box(0.125, 0.375, 0.1875, 0.25, 0.5, 0.8125),
            Shapes.box(0.75, 0.375, 0.1875, 0.875, 0.5, 0.8125),
            Shapes.box(0.25, 0.375, 0.6875, 0.75, 0.5, 0.8125),
            Shapes.box(0.25, 0.375, 0.1875, 0.75, 0.5, 0.3125)
    );

    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        SHAPES.put(Direction.NORTH, SHAPE_NORTH);
        SHAPES.put(Direction.SOUTH, rotateShape(Direction.NORTH, Direction.SOUTH, SHAPE_NORTH));
        SHAPES.put(Direction.EAST,  rotateShape(Direction.NORTH, Direction.EAST,  SHAPE_NORTH));
        SHAPES.put(Direction.WEST,  rotateShape(Direction.NORTH, Direction.WEST,  SHAPE_NORTH));
    }

    // Rotates a VoxelShape from one horizontal direction to another by applying
    // 90° CW Y-axis rotations. Each step transforms (x1,z1,x2,z2) → (1-z2, x1, 1-z1, x2).
    private static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, Shapes.empty() };
        int steps = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < steps; i++) {
            buffer[0].forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                    buffer[1] = Shapes.or(buffer[1],
                            Shapes.box(1 - z2, y1, x1, 1 - z1, y2, x2)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final MapCodec<CenserBlock> CODEC = simpleCodec(CenserBlock::new);

    public CenserBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.getOrDefault(state.getValue(FACING), SHAPE_NORTH);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.getOrDefault(state.getValue(FACING), SHAPE_NORTH);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof CenserBlockEntity censer)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (heldItem.getItem() instanceof FlintAndSteelItem && !state.getValue(LIT)) {
            ItemStack herb1 = censer.getItem(0);
            ItemStack herb2 = censer.getItem(1);
            if (herb1.isEmpty() || herb2.isEmpty()) {
                if (level.isClientSide()) player.displayClientMessage(Component.translatable("message.hexalia.censer_not_full"), true);
                return ItemInteractionResult.FAIL;
            }
            HerbCombination combo = new HerbCombination(herb1.getItem(), herb2.getItem());
            if (!CenserEffectHandler.isValidCombination(herb1.getItem(), herb2.getItem())) {
                if (level.isClientSide()) player.displayClientMessage(Component.translatable("message.hexalia.invalid_herb_combination"), true);
                return ItemInteractionResult.FAIL;
            }
            if (level.isClientSide()) {
                return ItemInteractionResult.SUCCESS;
            }
            censer.clearItems();
            level.setBlockAndUpdate(pos, state.setValue(LIT, true));
            censer.setActiveCombination(combo);
            censer.setBurnTime(Configuration.CENSER_EFFECT_DURATION.get());
            sendEffectActivationMessage(level, pos, combo, player);
            CenserEffectHandler.startEffect(level, pos, combo);
            heldItem.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, level.random.nextFloat() * 0.4F + 0.8F);
            return ItemInteractionResult.SUCCESS;
        }
        if (heldItem.getItem() instanceof ShovelItem && state.getValue(LIT)) {
            if (level.isClientSide()) {
                censer.clearItems();
            } else {
                level.setBlockAndUpdate(pos, state.setValue(LIT, false));
                censer.setBurnTime(0);
                censer.clearItems();
                censer.setActiveCombination(null);
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 1.0f);
            }
            return ItemInteractionResult.SUCCESS;
        }
        if (!state.getValue(LIT)) {
            if (heldItem.isEmpty()) {
                for (int i = 0; i < 2; i++) {
                    ItemStack stackInSlot = censer.getItem(i);
                    if (!stackInSlot.isEmpty()) {
                        ItemStack removed = censer.removeStack(i);
                        if (!player.getInventory().add(removed)) player.drop(removed, false);
                        level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.5f, 1.0f);
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            } else if (heldItem.is(ModTags.Items.HERBS)) {
                for (int i = 0; i < 2; i++) {
                    if (censer.getItem(i).isEmpty()) {
                        ItemStack toInsert = heldItem.copy();
                        toInsert.setCount(1);
                        censer.setItem(i, toInsert);
                        if (!player.isCreative()) heldItem.shrink(1);
                        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.5f, 1.0f);
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            } else {
                if (level.isClientSide()) player.displayClientMessage(Component.translatable("message.hexalia.invalid_item"), true);
                return ItemInteractionResult.FAIL;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private void sendEffectActivationMessage(Level level, BlockPos pos, HerbCombination combo, Player activatingPlayer) {
        String key = CenserEffectHandler.getMessageKeyForCombination(combo);
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        AABB area = new AABB(pos).inflate(radius);
        for (Player p : level.getEntitiesOfClass(Player.class, area)) {
            if (!p.getUUID().equals(activatingPlayer.getUUID()) && p instanceof ServerPlayer sp) {
                sp.displayClientMessage(Component.translatable(key), true);
            }
        }
        if (!level.isClientSide() && activatingPlayer instanceof ServerPlayer sp) {
            sp.displayClientMessage(Component.translatable(key), true);
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(LIT)) {
            if (pRandom.nextInt(10) == 0) {
                pLevel.playLocalSound(
                        pPos.getX() + 0.5D, pPos.getY() + 0.5D, pPos.getZ() + 0.5D,
                        SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS,
                        0.5F + pRandom.nextFloat(), pRandom.nextFloat() * 0.7F + 0.6F, false
                );
            }
            pLevel.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true,
                    pPos.getX() + 0.5D + pRandom.nextDouble() / 3.0D * (pRandom.nextBoolean() ? 1 : -1),
                    pPos.getY() + pRandom.nextDouble() + pRandom.nextDouble(),
                    pPos.getZ() + 0.5D + pRandom.nextDouble() / 3.0D * (pRandom.nextBoolean() ? 1 : -1),
                    0.0D, 0.07D, 0.0D
            );
            pLevel.addParticle(ParticleTypes.SMOKE,
                    pPos.getX() + 0.5D + pRandom.nextDouble() / 4.0D * (pRandom.nextBoolean() ? 1 : -1),
                    pPos.getY() + 0.4D,
                    pPos.getZ() + 0.5D + pRandom.nextDouble() / 4.0D * (pRandom.nextBoolean() ? 1 : -1),
                    0.0D, 0.005D, 0.0D
            );
            if (pRandom.nextInt(25) == 0) {
                pLevel.addParticle(ParticleTypes.LAVA,
                        pPos.getX() + 0.5D, pPos.getY() + 0.3D, pPos.getZ() + 0.5D,
                        pRandom.nextFloat() / 8.0F, 0.0D, pRandom.nextFloat() / 8.0F
                );
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CenserBlockEntity censer) {
                if (world instanceof ServerLevel) {
                    Containers.dropContents(world, pos, censer.getDropsContainer());
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
    protected RenderShape getRenderShape(BlockState state) {
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

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(blockEntityType, ModBlockEntityTypes.CENSER.get(),
                (level1, pos, state1, blockEntity) -> blockEntity.tick(level1, pos, state1));
    }
}