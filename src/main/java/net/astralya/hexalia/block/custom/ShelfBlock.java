package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.block.entity.custom.ShelfBlockEntity;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShelfBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final Map<Direction, VoxelShape> SHAPES = createShapes();
    public static final MapCodec<ShelfBlock> CODEC = simpleCodec(ShelfBlock::new);

    public ShelfBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ShelfBlockEntity shelf) {
            Vec3 hitPos = hitResult.getLocation();
            Direction facing = state.getValue(FACING);

            double x = hitPos.x() - pos.getX();
            double y = hitPos.y() - pos.getY();
            double z = hitPos.z() - pos.getZ();

            double relativeX, relativeZ;

            switch (facing) {
                case NORTH:
                    relativeX = 1.0 - x;
                    relativeZ = z;
                    break;
                case SOUTH:
                    relativeX = x;
                    relativeZ = 1.0 - z;
                    break;
                case EAST:
                    relativeX = 1.0 - z;
                    relativeZ = 1.0 - x;
                    break;
                case WEST:
                    relativeX = z;
                    relativeZ = x;
                    break;
                default:
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            if (relativeZ < 0.5 || relativeZ > 1.0 || y < 0.25 || y > 0.75) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            int row;
            if (relativeZ < 0.75) {
                row = 1;
            } else {
                row = 0;
            }

            int column;
            if (relativeX < 0.33) {
                column = 0;
            } else if (relativeX < 0.67) {
                column = 1;
            } else {
                column = 2;
            }

            int slot = column + (row * 3);

            ItemStack heldItem = player.getItemInHand(hand);
            ItemStack shelfItem = shelf.getItem(slot);

            if (!shelfItem.isEmpty()) {
                ItemStack removedItem = shelf.removeStack(slot);
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);

                if (!player.getInventory().add(removedItem)) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), removedItem);
                }

                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            } else if (isValidItem(heldItem)) {
                ItemStack toPlace;
                if (player.isCreative()) {
                    toPlace = heldItem.copy();
                    toPlace.setCount(1);
                } else {
                    toPlace = heldItem.split(1);
                }
                shelf.setItem(slot, toPlace);
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private boolean isValidItem(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModTags.Items.BREWS) || stack.is(Items.POTION)
                || stack.is(Items.LINGERING_POTION) || stack.is(Items.SPLASH_POTION);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING).getOpposite();
        BlockPos supportPos = pos.relative(direction);
        BlockState supportState = level.getBlockState(supportPos);
        return supportState.isFaceSturdy(level, supportPos, direction.getOpposite());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ShelfBlockEntity shelf) {
                if (level instanceof ServerLevel) {
                    for (int i = 0; i < 6; i++) {
                        ItemStack stack = shelf.getItem(i);
                        if (!stack.isEmpty()) {
                            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                        }
                    }
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.hexalia.shelf").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ShelfBlockEntity(blockPos, blockState);
    }

    private static VoxelShape createShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.25, 0.5, 1, 0.3125, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.125, 0.875, 1, 0.25, 1), BooleanOp.OR);
        return shape;
    }

    private static Map<Direction, VoxelShape> createShapes() {
        Map<Direction, VoxelShape> shapes = new HashMap<>();
        VoxelShape baseShape = createShape();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            shapes.put(direction, rotateShape(Direction.NORTH, direction, baseShape));
        }
        return shapes;
    }

    private static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    buffer[1] = Shapes.or(buffer[1],
                            Shapes.box(1.0 - maxZ, minY, minX,
                                    1.0 - minZ, maxY, maxX))
            );
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
    }
}
