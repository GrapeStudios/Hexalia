package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.custom.MortarAndPestleBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MortarAndPestleBlock extends BlockWithEntity {

    public static final MapCodec<MortarAndPestleBlock> CODEC = createCodec(MortarAndPestleBlock::new);

    private static final VoxelShape SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0.1875, 0, 0.1875, 0.8125, 0.0625, 0.8125),
            VoxelShapes.cuboid(0.6875, 0.0625, 0.3125, 0.8125, 0.25, 0.6875),
            VoxelShapes.cuboid(0.1875, 0.0625, 0.3125, 0.3125, 0.25, 0.6875),
            VoxelShapes.cuboid(0.1875, 0.0625, 0.1875, 0.8125, 0.25, 0.3125),
            VoxelShapes.cuboid(0.1875, 0.0625, 0.6875, 0.8125, 0.25, 0.8125)
    );

    public MortarAndPestleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MortarAndPestleBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntityTypes.MORTAR_AND_PESTLE, MortarAndPestleBlockEntity::tick);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof MortarAndPestleBlockEntity mortar) {
                mortar.drops();
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.getMainHandStack().isEmpty()) {
            return ActionResult.PASS;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof MortarAndPestleBlockEntity mortar)) {
            return ActionResult.PASS;
        }
        if (!player.isSneaking()) {
            if (world.isClient) {
                return (mortar.hasOutput() || mortar.hasAnyInputs()) ? ActionResult.SUCCESS : ActionResult.PASS;
            }
            ItemStack extracted = mortar.hasOutput() ? mortar.takeOutputOne() : mortar.extractOneInput();
            if (extracted.isEmpty()) {
                return ActionResult.PASS;
            }
            if (!player.getInventory().insertStack(extracted)) {
                ItemEntity entity = new ItemEntity(
                        world,
                        pos.getX() + 0.5,
                        pos.getY() + 1.0,
                        pos.getZ() + 0.5,
                        extracted
                );
                world.spawnEntity(entity);
            }
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.8F, 1.2F);
            return ActionResult.CONSUME;
        }
        if (world.isClient) {
            return mortar.canStartSpin() ? ActionResult.SUCCESS : ActionResult.PASS;
        }
        if (mortar.startSpin()) {
            world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.isSneaking()) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (stack.isEmpty()) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof MortarAndPestleBlockEntity mortar)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (world.isClient) {
            return mortar.canInsertOne(stack) ? ItemActionResult.SUCCESS : ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!mortar.canInsertOne(stack)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        boolean inserted = mortar.insertOneIntoNextEmpty(stack);
        if (!inserted) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!player.getAbilities().creativeMode) {
            stack.decrement(1);
        }
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.8F, 1.2F);
        return ItemActionResult.CONSUME;
    }
}