package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.custom.DreamcatcherBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DreamcatcherBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final MapCodec<DreamcatcherBlock> CODEC = createCodec(DreamcatcherBlock::new);

    private static final VoxelShape NORTH_SHAPE = VoxelShapes.cuboid(0.125, 0.0625, 0.9375, 0.875, 0.875, 1);
    private static final VoxelShape SOUTH_SHAPE = VoxelShapes.cuboid(0.125, 0.0625, 0, 0.875, 0.875, 0.0625);
    private static final VoxelShape WEST_SHAPE  = VoxelShapes.cuboid(0.9375, 0.0625, 0.125, 1, 0.875, 0.875);
    private static final VoxelShape EAST_SHAPE  = VoxelShapes.cuboid(0, 0.0625, 0.125, 0.0625, 0.875, 0.875);

    private static final int PHANTOM_CHECK_INTERVAL = 20;

    public DreamcatcherBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                   WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.offset(state.get(FACING).getOpposite())).isSolidBlock(world, pos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case SOUTH -> SOUTH_SHAPE;
            case WEST  -> WEST_SHAPE;
            case EAST  -> EAST_SHAPE;
            default    -> NORTH_SHAPE;
        };
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world,
                                             BlockPos pos, PlayerEntity player, Hand hand,
                                             BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof DreamcatcherBlockEntity be)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        ItemStack held = player.getStackInHand(hand);
        if (held.isEmpty() && player.isSneaking()) {
            if (!world.isClient()) {
                ItemStack returned = be.tryExtractFuel(player);
                if (!returned.isEmpty()) {
                    if (!player.getInventory().insertStack(returned)) player.dropItem(returned, false);
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5f, 1.0f);
                }
            }
            return ItemActionResult.SUCCESS;
        }
        if (held.isOf(ModItems.FIRE_NODE)) {
            if (!world.isClient()) {
                var result = be.tryInsertFuel(player, held);
                if (result == ActionResult.SUCCESS) {
                    world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS,
                            0.6f, 0.8f + world.random.nextFloat() * 0.4f);
                } else if (result == ActionResult.FAIL) {
                    player.sendMessage(Text.translatable("message.hexalia.dreamcatcher_full"), true);
                }
            }
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!world.isClient) return;
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof DreamcatcherBlockEntity dreamcatcher && dreamcatcher.hasFuel()) {
            dreamcatcher.spawnActiveParticles(world, pos, random);
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DreamcatcherBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                            BlockEntityType<T> type) {
        if (world.isClient()) return null;
        return validateTicker(type, ModBlockEntityTypes.DREAMCATCHER,
                (w, p, st, be) -> {
                    DreamcatcherBlockEntity.tick(w, p, st, be);
                    if (w.getTime() % PHANTOM_CHECK_INTERVAL == 0) {
                        tickPhantoms((ServerWorld) w, p, be);
                    }
                });
    }

    private static void tickPhantoms(ServerWorld world, BlockPos pos, DreamcatcherBlockEntity be) {
        if (!be.hasFuel() || !world.isNight()) return;
        int radius = Configuration.DREAMCATCHER_RADIUS.get();
        Box area = new Box(pos).expand(radius);
        List<PhantomEntity> phantoms = world.getEntitiesByClass(PhantomEntity.class, area, e -> true);
        if (phantoms.isEmpty()) return;
        for (PhantomEntity phantom : phantoms) {
            phantom.setOnFireFor(Configuration.PHANTOM_IGNITE_DURATION.get());
            phantom.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SLOWNESS, 60, 1, false, false, false));
            spawnIgniteParticles(world, phantom.getPos());
        }
        world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS,
                0.4f, 1.2f + world.random.nextFloat() * 0.3f);
    }

    private static void spawnIgniteParticles(ServerWorld world, Vec3d pos) {
        world.spawnParticles(ParticleTypes.FLAME,
                pos.x, pos.y + 1.0, pos.z, 8, 0.3, 0.5, 0.3, 0.05);
        world.spawnParticles(ParticleTypes.SMOKE,
                pos.x, pos.y + 1.0, pos.z, 4, 0.2, 0.3, 0.2, 0.02);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}