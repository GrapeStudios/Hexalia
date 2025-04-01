package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.LunarLilyBlockEntity;
import net.grapes.hexalia.block.entity.ModBlockEntities;
import net.grapes.hexalia.block.entity.RitualBrazierBlockEntity;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class RitualBrazierBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty SALTED = BooleanProperty.of("salted");

    private static final VoxelShape SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);

    public RitualBrazierBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(SALTED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        if (world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof RitualBrazierBlockEntity brazier && brazier.isActive()) {
                double x = pos.getX() + 0.5;
                double y = pos.getY() + 0.7;
                double z = pos.getZ() + 0.5;

                world.addParticle(ParticleTypes.ELECTRIC_SPARK,
                        x + (random.nextDouble()-0.5)*0.3,
                        y + random.nextDouble()*0.3,
                        z + (random.nextDouble()-0.5)*0.3,
                        0, 0.02, 0);
            }
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        ItemStack heldItem = player.getStackInHand(hand);

        if (!(blockEntity instanceof RitualBrazierBlockEntity ritualBrazierBlockEntity)) {
            return ActionResult.PASS;
        }

        if (hand == Hand.MAIN_HAND) {
            if (heldItem.isOf(ModItems.SALT) && !state.get(SALTED)) {
                world.setBlockState(pos, state.with(SALTED, true), Block.NOTIFY_ALL);
                if (!player.isCreative()) {
                    heldItem.decrement(1);
                }
                world.playSound(null, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.success(world.isClient);
            }

            if (heldItem.isOf(ModItems.HEX_FOCUS) && !state.get(SALTED)) {
                if (ritualBrazierBlockEntity.startMoonRitual(player)) {
                    return ActionResult.success(world.isClient);
                } else {
                    return ActionResult.PASS;
                }
            }

            if (!heldItem.isEmpty() && ritualBrazierBlockEntity.isEmpty() && !heldItem.isOf(ModItems.HEX_FOCUS) && !heldItem.isOf(ModItems.SALT)) {
                if (ritualBrazierBlockEntity.addStack(heldItem.split(1))) {
                    playItemSound(world, pos);
                    return ActionResult.success(world.isClient);
                }
            }

            if (!ritualBrazierBlockEntity.isEmpty() && heldItem.isEmpty()) {
                removeItemFromBlock(world, ritualBrazierBlockEntity, player);
                return ActionResult.success(world.isClient);
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RitualBrazierBlockEntity ritualBrazierBlockEntity) {
                ItemScatterer.spawn(world, pos, ritualBrazierBlockEntity);
                if (state.get(SALTED)) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModItems.SALT));
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing()
                .getOpposite()).with(SALTED, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, SALTED);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    private void removeItemFromBlock(World world, RitualBrazierBlockEntity ritualBrazierBlockEntity, PlayerEntity player) {
        ItemStack stack = ritualBrazierBlockEntity.removeStack();
        if (!player.getInventory().insertStack(stack)) {
            ItemScatterer.spawn(world, player.getX(), player.getY(), player.getZ(), stack);
        }
        playItemSound(world, ritualBrazierBlockEntity.getPos());
    }

    private void playItemSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 0.5f);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RitualBrazierBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }
        return type == ModBlockEntities.RITUAL_BRAZIER_BE ? (world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof RitualBrazierBlockEntity ritualBrazier) {
                RitualBrazierBlockEntity.tick(world1, pos, state1, ritualBrazier);
            }
        } : null;
    }
}