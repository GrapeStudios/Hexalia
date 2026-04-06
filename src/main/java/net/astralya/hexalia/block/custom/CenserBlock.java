package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.custom.CenserBlockEntity;
import net.astralya.hexalia.gameplay.censer.CenserEffectHandler;
import net.astralya.hexalia.gameplay.censer.HerbCombination;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.ItemScatterer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class CenserBlock extends BlockWithEntity {

    private static final VoxelShape SHAPE_NORTH = VoxelShapes.union(
            VoxelShapes.cuboid(0.0625, 0, 0, 0.3125, 0.25, 1),
            VoxelShapes.cuboid(0.6875, 0, 0, 0.9375, 0.25, 1),
            VoxelShapes.cuboid(0.3125, 0, 0, 0.6875, 0.0625, 1),
            VoxelShapes.cuboid(0, 0.25, 0.0625, 1, 0.375, 0.9375),
            VoxelShapes.cuboid(0.125, 0.375, 0.1875, 0.25, 0.5, 0.8125),
            VoxelShapes.cuboid(0.75, 0.375, 0.1875, 0.875, 0.5, 0.8125),
            VoxelShapes.cuboid(0.25, 0.375, 0.6875, 0.75, 0.5, 0.8125),
            VoxelShapes.cuboid(0.25, 0.375, 0.1875, 0.75, 0.5, 0.3125)
    );

    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        SHAPES.put(Direction.NORTH, SHAPE_NORTH);
        SHAPES.put(Direction.SOUTH, rotateShape(Direction.NORTH, Direction.SOUTH, SHAPE_NORTH));
        SHAPES.put(Direction.EAST,  rotateShape(Direction.NORTH, Direction.EAST,  SHAPE_NORTH));
        SHAPES.put(Direction.WEST,  rotateShape(Direction.NORTH, Direction.WEST,  SHAPE_NORTH));
    }

    private static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, VoxelShapes.empty() };
        int steps = (to.getHorizontal() - from.getHorizontal() + 4) % 4;
        for (int i = 0; i < steps; i++) {
            buffer[0].forEachBox((x1, y1, z1, x2, y2, z2) ->
                    buffer[1] = VoxelShapes.union(buffer[1],
                            VoxelShapes.cuboid(1 - z2, y1, x1, 1 - z1, y2, x2)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }
        return buffer[0];
    }

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = Properties.LIT;

    public static final MapCodec<CenserBlock> CODEC = createCodec(CenserBlock::new);

    public CenserBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
    }

    @Override
    public MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.getOrDefault(state.get(FACING), SHAPE_NORTH);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.getOrDefault(state.get(FACING), SHAPE_NORTH);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getStackInHand(hand);
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CenserBlockEntity censer)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (heldItem.getItem() instanceof FlintAndSteelItem && !state.get(LIT)) {
            ItemStack herb1 = censer.getStack(0);
            ItemStack herb2 = censer.getStack(1);
            if (herb1.isEmpty() || herb2.isEmpty()) {
                if (world.isClient()) player.sendMessage(Text.translatable("message.hexalia.censer_not_full"), true);
                return ItemActionResult.FAIL;
            }
            HerbCombination combo = new HerbCombination(herb1.getItem(), herb2.getItem());
            if (!CenserEffectHandler.isValidCombination(herb1.getItem(), herb2.getItem())) {
                if (world.isClient()) player.sendMessage(Text.translatable("message.hexalia.invalid_herb_combination"), true);
                return ItemActionResult.FAIL;
            }
            if (world.isClient()) {
                return ItemActionResult.SUCCESS;
            }
            censer.clearItems();
            world.setBlockState(pos, state.with(LIT, true));
            censer.setActiveCombination(combo);
            censer.setBurnTime(Configuration.CENSER_EFFECT_DURATION.get());
            sendEffectActivationMessage(world, pos, combo, player);
            CenserEffectHandler.startEffect(world, pos, combo);
            heldItem.damage(1, player, player.getPreferredEquipmentSlot(heldItem));
            world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, world.random.nextFloat() * 0.4F + 0.8F);
            return ItemActionResult.SUCCESS;
        }

        if (heldItem.getItem() instanceof ShovelItem && state.get(LIT)) {
            if (world.isClient()) {
                censer.clearItems();
            } else {
                world.setBlockState(pos, state.with(LIT, false));
                censer.setBurnTime(0);
                censer.clearItems();
                censer.setActiveCombination(null);
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.5f, 1.0f);
            }
            return ItemActionResult.SUCCESS;
        }

        if (!state.get(LIT)) {
            if (heldItem.isEmpty()) {
                for (int i = 0; i < 2; i++) {
                    ItemStack stackInSlot = censer.getStack(i);
                    if (!stackInSlot.isEmpty()) {
                        ItemStack removed = censer.removeStack(i);
                        if (!player.getInventory().insertStack(removed)) player.dropItem(removed, false);
                        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5f, 1.0f);
                        return ItemActionResult.SUCCESS;
                    }
                }
            } else if (heldItem.isIn(ModTags.Items.HERBS)) {
                for (int i = 0; i < 2; i++) {
                    if (censer.getStack(i).isEmpty()) {
                        ItemStack toInsert = heldItem.copy();
                        toInsert.setCount(1);
                        censer.setStack(i, toInsert);
                        if (!player.isCreative()) heldItem.decrement(1);
                        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 0.5f, 1.0f);
                        return ItemActionResult.SUCCESS;
                    }
                }
            } else {
                if (world.isClient()) player.sendMessage(Text.translatable("message.hexalia.invalid_item"), true);
                return ItemActionResult.FAIL;
            }
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private void sendEffectActivationMessage(World world, BlockPos pos, HerbCombination combo, PlayerEntity activatingPlayer) {
        String key = CenserEffectHandler.getMessageKeyForCombination(combo);
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        Box area = new Box(pos).expand(radius);
        for (PlayerEntity p : world.getEntitiesByClass(PlayerEntity.class, area, e -> true)) {
            if (!p.getUuid().equals(activatingPlayer.getUuid()) && p instanceof ServerPlayerEntity sp) {
                sp.sendMessage(Text.translatable(key), true);
            }
        }
        if (!world.isClient() && activatingPlayer instanceof ServerPlayerEntity sp) {
            sp.sendMessage(Text.translatable(key), true);
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT)) {
            if (random.nextInt(10) == 0) {
                world.playSound(
                        pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                        SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS,
                        0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false
                );
            }
            world.addImportantParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true,
                    pos.getX() + 0.5D + random.nextDouble() / 3.0D * (random.nextBoolean() ? 1 : -1),
                    pos.getY() + random.nextDouble() + random.nextDouble(),
                    pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (random.nextBoolean() ? 1 : -1),
                    0.0D, 0.07D, 0.0D
            );
            world.addParticle(ParticleTypes.SMOKE,
                    pos.getX() + 0.5D + random.nextDouble() / 4.0D * (random.nextBoolean() ? 1 : -1),
                    pos.getY() + 0.4D,
                    pos.getZ() + 0.5D + random.nextDouble() / 4.0D * (random.nextBoolean() ? 1 : -1),
                    0.0D, 0.005D, 0.0D
            );
            if (random.nextInt(25) == 0) {
                world.addParticle(ParticleTypes.LAVA,
                        pos.getX() + 0.5D, pos.getY() + 0.3D, pos.getZ() + 0.5D,
                        random.nextFloat() / 8.0F, 0.0D, random.nextFloat() / 8.0F
                );
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CenserBlockEntity censer) {
                if (world instanceof ServerWorld) {
                    ItemScatterer.spawn(world, pos, censer.getDropsContainer());
                }
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public @Nullable BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
        return getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(LIT, false);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CenserBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) {
            return null;
        }
        return validateTicker(type, ModBlockEntityTypes.CENSER,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
}