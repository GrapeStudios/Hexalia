package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.custom.censer.CenserEffectHandler;
import net.astralya.hexalia.block.custom.censer.HerbCombination;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.custom.CenserBlockEntity;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CenserBlock extends BlockWithEntity {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = Properties.LIT;
    public static final MapCodec<CenserBlock> CODEC = createCodec(CenserBlock::new);

    public CenserBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Vec3d offset = state.getModelOffset(world, pos);
        return SHAPE.offset(offset.x, offset.y, offset.z);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction clickedFace = ctx.getSide();
        if (clickedFace.getAxis() == Direction.Axis.Y) {
            return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        }
        return getDefaultState().with(FACING, clickedFace);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CenserBlockEntity censer)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        ItemStack heldItem = player.getStackInHand(hand);

        if (heldItem.getItem() instanceof FlintAndSteelItem && !state.get(LIT)) {
            ItemStack herb1 = censer.getItem(0);
            ItemStack herb2 = censer.getItem(1);

            if (herb1.isEmpty() || herb2.isEmpty()) {
                if (world.isClient) player.sendMessage(Text.translatable("message.hexalia.censer_not_full"), true);
                return ItemActionResult.FAIL;
            }

            HerbCombination combo = new HerbCombination(herb1.getItem(), herb2.getItem());
            if (!CenserEffectHandler.isValidCombination(herb1.getItem(), herb2.getItem())) {
                if (world.isClient) player.sendMessage(Text.translatable("message.hexalia.invalid_herb_combination"), true);
                return ItemActionResult.FAIL;
            }

            if (!world.isClient) {
                sendEffectActivationMessage(world, pos, combo, player);
                censer.clearItems();
                world.setBlockState(pos, state.with(LIT, true), 3);
                censer.setActiveCombination(combo);
                censer.setBurnTime(Configuration.common().functional_blocks.censerEffectDuration);
                CenserEffectHandler.startEffect(world, pos, combo);
            } else {
                censer.clearItems();
            }

            if (!player.getAbilities().creativeMode && heldItem.isDamageable()) {
                heldItem.damage(1, player, LivingEntity.getSlotForHand(hand));
                if (heldItem.isEmpty()) player.setStackInHand(hand, ItemStack.EMPTY);
            }

            world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, world.random.nextFloat() * 0.4F + 0.8F);
            return ItemActionResult.SUCCESS;
        }

        if (heldItem.getItem() instanceof ShovelItem && state.get(LIT)) {
            if (!world.isClient) {
                world.setBlockState(pos, state.with(LIT, false), 3);
                censer.setBurnTime(0);
                censer.clearItems();
                censer.setActiveCombination(null);
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.5f, 1.0f);
            } else {
                censer.clearItems();
            }
            return ItemActionResult.SUCCESS;
        }

        if (!state.get(LIT)) {
            if (heldItem.isEmpty()) {
                for (int i = 0; i < censer.getItems().size(); i++) {
                    ItemStack inSlot = censer.getItem(i);
                    if (!inSlot.isEmpty()) {
                        ItemStack removed = censer.removeStack(i);
                        if (!player.getInventory().insertStack(removed)) player.dropItem(removed, false);
                        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5f, 1.0f);
                        return ItemActionResult.SUCCESS;
                    }
                }
            } else if (heldItem.isIn(ModTags.Items.HERBS)) {
                for (int i = 0; i < censer.getItems().size(); i++) {
                    if (censer.getItem(i).isEmpty()) {
                        ItemStack toInsert = heldItem.copy();
                        toInsert.setCount(1);
                        censer.setItem(i, toInsert);
                        if (!player.isCreative()) heldItem.decrement(1);
                        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 0.5f, 1.0f);
                        return ItemActionResult.SUCCESS;
                    }
                }
            } else {
                if (world.isClient) player.sendMessage(Text.translatable("message.hexalia.invalid_item"), true);
                return ItemActionResult.FAIL;
            }
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private void sendEffectActivationMessage(World world, BlockPos pos, HerbCombination combo, PlayerEntity activatingPlayer) {
        String key = CenserEffectHandler.getMessageKeyForCombination(combo);
        int radius = Configuration.common().functional_blocks.censerEffectRadius;
        Box area = new Box(pos).expand(radius);

        for (PlayerEntity player : world.getEntitiesByClass(PlayerEntity.class, area, p -> true)) {
            if (!player.getUuid().equals(activatingPlayer.getUuid()) && player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.sendMessage(Text.translatable(key));
            }
        }

        if (!world.isClient && activatingPlayer instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(Text.translatable(key));
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CenserBlockEntity censer) {
                if (!world.isClient) {
                    for (ItemStack stack : censer.getItems()) {
                        if (!stack.isEmpty()) {
                            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                        }
                    }
                    censer.clearItems();
                }

                CenserEffectHandler.clearPlayerEffectsInRange(world, pos);
                CenserEffectHandler.removeActiveEffect(pos);

                world.removeBlockEntity(pos);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CenserBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntityTypes.CENSER, (w, p, s, be) -> be.tick(w, p, s));
    }
}