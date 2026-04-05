package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.custom.RitualBrazierBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RitualBrazierBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty SALTED = BooleanProperty.of("salted");

    protected static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0)
    );

    public RitualBrazierBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(SALTED, false));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState()
                .with(FACING, context.getHorizontalPlayerFacing().getOpposite())
                .with(SALTED, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, SALTED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RitualBrazierBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RitualBrazierBlockEntity brazier) {
                ItemStack stored = brazier.getStoredItem();
                if (!stored.isEmpty()) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stored);
                }
                if (state.get(SALTED)) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModItems.SALT));
                }
                world.updateComparators(pos, this);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof RitualBrazierBlockEntity brazier)) {
            return ActionResult.PASS;
        }

        ItemStack heldStack = player.getStackInHand(hand);
        ItemStack offhandStack = player.getOffHandStack();

        if (hand == Hand.MAIN_HAND && !state.get(SALTED)) {
            boolean isSalt = heldStack.isIn(ModTags.Items.SALT_DUSTS) || heldStack.isOf(ModItems.SALT);
            if (isSalt) {
                if (!world.isClient) {
                    world.setBlockState(pos, state.with(SALTED, true), Block.NOTIFY_ALL);
                    if (!player.isCreative()) {
                        heldStack.decrement(1);
                    }
                    world.playSound(null, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
                return ActionResult.success(world.isClient);
            }
        }

        if (brazier.isEmpty()) {
            if (!offhandStack.isEmpty()) {
                if (hand == Hand.MAIN_HAND && offhandStack.isIn(ModTags.Items.OFFHAND_EQUIPMENT) && !(heldStack.getItem() instanceof BlockItem)) {
                    return ActionResult.PASS;
                }
                if (hand == Hand.OFF_HAND && offhandStack.isIn(ModTags.Items.OFFHAND_EQUIPMENT)) {
                    return ActionResult.PASS;
                }
            }

            if (!heldStack.isEmpty()) {
                if (!world.isClient) {
                    ItemStack toInsert = player.getAbilities().creativeMode ? heldStack.copy() : heldStack;
                    if (brazier.addItem(toInsert)) {
                        playItemSound(world, pos);
                    }
                }
                return ActionResult.success(world.isClient);
            }

            return ActionResult.PASS;
        }

        ItemStack focusStack = heldStack.isOf(ModItems.HEX_FOCUS) ? heldStack : offhandStack.isOf(ModItems.HEX_FOCUS) ? offhandStack : ItemStack.EMPTY;
        if (!focusStack.isEmpty()) {
            if (!world.isClient) {
                RitualBrazierBlockEntity.RitualResult result = brazier.tryStartCelestialInfusion();
                switch (result) {
                    case SUCCESS -> {
                        spawnPoofParticles(world, pos);
                        world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.25f, 0.25f);
                    }
                    case ALREADY_CHANNELING -> {
                    }
                    case NO_CELESTIAL_BLOOMS -> player.sendMessage(Text.translatable("message.hexalia.ritual_brazier.no_celestial_blooms"), true);
                    case NO_SKY -> player.sendMessage(Text.translatable("message.hexalia.ritual_brazier.no_sky"), true);
                    case INVALID_ITEM -> player.sendMessage(Text.translatable("message.hexalia.ritual_brazier.invalid_item"), true);
                }
            }
            return ActionResult.success(world.isClient);
        }

        if (hand == Hand.MAIN_HAND && heldStack.isEmpty()) {
            if (!world.isClient) {
                ItemStack removed = brazier.removeItem();
                if (!player.isCreative() && !removed.isEmpty()) {
                    if (!player.getInventory().insertStack(removed)) {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), removed);
                    }
                }
                playItemSound(world, pos);
            }
            return ActionResult.success(world.isClient);
        }

        return ActionResult.PASS;
    }

    private static void spawnPoofParticles(World world, BlockPos pos) {
        if (world instanceof ServerWorld server) {
            server.spawnParticles(
                    ParticleTypes.POOF,
                    pos.getX() + 0.5,
                    pos.getY() + 1.0,
                    pos.getZ() + 0.5,
                    10,
                    0.2,
                    0.2,
                    0.2,
                    0.02
            );
        }
    }

    private void playItemSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.25f, 0.25f);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, ModBlockEntityTypes.RITUAL_BRAZIER, RitualBrazierBlockEntity::serverTick);
    }
}