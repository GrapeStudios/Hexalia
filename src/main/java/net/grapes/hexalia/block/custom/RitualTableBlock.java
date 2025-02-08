package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.RitualTableBlockEntity;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.particle.ModParticles;
import net.grapes.hexalia.sound.ModSounds;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
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

import java.util.concurrent.ThreadLocalRandom;

public class RitualTableBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE = createShape();

    public RitualTableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    private static VoxelShape createShape() {
        return VoxelShapes.union(
                VoxelShapes.cuboid(0.1875, 0, 0.1875, 0.8125, 0.125, 0.8125),
                VoxelShapes.cuboid(0.25, 0.125, 0.25, 0.75, 0.625, 0.75),
                VoxelShapes.cuboid(0.1875, 0.625, 0.1875, 0.8125, 0.6875, 0.8125),
                VoxelShapes.cuboid(0.125, 0.6875, 0.125, 0.875, 0.8125, 0.875)
        );
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RitualTableBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof RitualTableBlockEntity ritualTableBlockEntity) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), ritualTableBlockEntity.getStoredItem());
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof RitualTableBlockEntity ritualTableBlockEntity)) {
            return ActionResult.PASS;
        }

        ItemStack heldItem = player.getStackInHand(hand);

        if (hand == Hand.MAIN_HAND) {
            if (!ritualTableBlockEntity.isEmpty() && heldItem.isEmpty()) {
                removeItemFromBlock(world, ritualTableBlockEntity, player);
                return ActionResult.SUCCESS;
            }

            if (!heldItem.isEmpty() && ritualTableBlockEntity.isEmpty() && !heldItem.isOf(ModItems.HEX_FOCUS)) {
                if (ritualTableBlockEntity.addItem(heldItem.split(1))) {
                    playItemSound(world, pos);
                    spawnParticleEffect(world, pos, ParticleTypes.POOF, 5, 10);
                    return ActionResult.SUCCESS;
                }
            }

            if (heldItem.isOf(ModItems.HEX_FOCUS)) {
                if (ritualTableBlockEntity.canStartRitual(world, pos)) {
                    boolean success = ritualTableBlockEntity.startRitual();
                    if (success) {
                        spawnSuccessEffect(world, pos);
                    }
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }



    private void removeItemFromBlock(World world, RitualTableBlockEntity ritualTableBlockEntity, PlayerEntity player) {
        ItemStack stack = ritualTableBlockEntity.removeItem();
        if (!player.getInventory().insertStack(stack)) {
            ItemScatterer.spawn(world, player.getX(), player.getY(), player.getZ(), stack);
        }
        playItemSound(world, ritualTableBlockEntity.getPos());
    }

    private void spawnSuccessEffect(World world, BlockPos pos) {
        spawnParticleEffect(world, pos, ParticleTypes.ENCHANT, 10, 20);
        spawnParticleEffect(world, pos, ModParticles.LEAVES_PARTICLE, 10, 20);
        playRitualSound(world, pos);
    }

    private void spawnParticleEffect(World world, BlockPos pos, DefaultParticleType particleType, int minParticles, int maxParticles) {
        int particleCount = ThreadLocalRandom.current().nextInt(minParticles, maxParticles);
        for (int i = 0; i < particleCount; i++) {
            double offsetX = ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
            double offsetY = ThreadLocalRandom.current().nextDouble(0, 0.5);
            double offsetZ = ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
            world.addParticle(particleType, pos.getX() + 0.5 + offsetX, pos.getY() + 1.0 + offsetY,
                    pos.getZ() + 0.5 + offsetZ, 0, 0, 0);
        }
    }

    private void playItemSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.8f, 0.5f);
        world.playSound(null, pos, SoundEvents.BLOCK_CHISELED_BOOKSHELF_PICKUP_ENCHANTED,
                SoundCategory.BLOCKS, 0.8f, 0.5f);
    }

    private void playRitualSound(World world, BlockPos pos) {
        world.playSound(null, pos, ModSounds.RITUAL_SUCCESS, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
}
