package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.CenserBlockEntity;
import net.grapes.hexalia.block.entity.ModBlockEntities;
import net.grapes.hexalia.censer.CenserEffectHandler;
import net.grapes.hexalia.censer.HerbCombination;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CenserBlock extends BlockWithEntity {

    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = Properties.LIT;

    public CenserBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Vec3d vec3d = state.getModelOffset(world, pos);
        return SHAPE.offset(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            world.scheduleBlockTick(pos, this, 1);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CenserBlockEntity censer) {
            CenserBlockEntity.tick(world, pos, state, censer);
        }
        world.scheduleBlockTick(pos, this, 1);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CenserBlockEntity censer) {
                for (int i = 0; i < censer.getItems().size(); i++) {
                    ItemStack stack = censer.getStack(i);
                    if (!stack.isEmpty()) {
                        Block.dropStack(world, pos, stack);
                    }
                }
                world.updateComparators(pos, this);
            }
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CenserBlockEntity censer) {
                boolean wasLit = state.get(LIT);
                boolean isLit = newState.get(LIT);

                if (wasLit && !isLit) {
                    censer.clearInventory();
                    world.updateListeners(pos, state, newState, Block.NOTIFY_ALL);
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction clickedFace = ctx.getSide();
        if (clickedFace.getAxis() == Direction.Axis.Y) {
            return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        }
        return this.getDefaultState().with(FACING, clickedFace);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getStackInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof CenserBlockEntity censer)) {
            return ActionResult.PASS;
        }

        // Handle lighting
        if (heldItem.getItem() instanceof FlintAndSteelItem && !state.get(LIT)) {
            ItemStack herb1 = censer.getStack(0);
            ItemStack herb2 = censer.getStack(1);

            if (herb1.isEmpty() || herb2.isEmpty()) {
                if (world.isClient()) {
                    player.sendMessage(Text.translatable("message.hexalia.censer_not_full"), true);
                }
                return ActionResult.FAIL;
            }

            HerbCombination combo = new HerbCombination(herb1.getItem(), herb2.getItem());
            if (!CenserEffectHandler.isValidCombination(herb1.getItem(), herb2.getItem())) {
                if (world.isClient()) {
                    player.sendMessage(Text.translatable("message.hexalia.invalid_herb_combination"), true);
                }
                return ActionResult.FAIL;
            }

            if (!world.isClient()) {
                // Send effect message before activating
                sendEffectActivationMessage(world, pos, combo, player);

                censer.setActiveCombination(combo);
                censer.clearInventory();
                world.setBlockState(pos, state.with(LIT, true));
                censer.setBurnTime(CenserBlockEntity.MAX_BURN_TIME);
                CenserEffectHandler.startEffect(world, pos, combo);
            }

            heldItem.damage(1, player, p -> p.sendToolBreakStatus(hand));
            world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, world.random.nextFloat() * 0.4F + 0.8F);
            return ActionResult.SUCCESS;
        }

        else if (heldItem.getItem() instanceof ShovelItem && state.get(LIT)) {
            if (!world.isClient()) {
                world.setBlockState(pos, state.with(LIT, false));
                censer.setBurnTime(0);
            }
            return ActionResult.SUCCESS;
        }

        if (!state.get(LIT)) {
            if (heldItem.isEmpty()) {
                // Retrieve items with empty hand
                for (int i = 0; i < censer.getItems().size(); i++) {
                    ItemStack stackInSlot = censer.getStack(i);
                    if (!stackInSlot.isEmpty()) {
                        ItemStack removedStack = censer.removeStack(i);
                        if (!player.getInventory().insertStack(removedStack)) {
                            player.dropItem(removedStack, false);
                        }
                        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5f, 1.0f);
                        return ActionResult.SUCCESS;
                    }
                }
            } else {
                // Only allow items with HERBS tag to be stored
                if (heldItem.isIn(ModTags.Items.HERBS)) {
                    for (int i = 0; i < censer.getItems().size(); i++) {
                        if (censer.getStack(i).isEmpty()) {
                            ItemStack stackToInsert = heldItem.copy();
                            stackToInsert.setCount(1);
                            censer.setStack(i, stackToInsert);

                            if (!player.isCreative()) {
                                heldItem.decrement(1);
                            }

                            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 0.5f, 1.0f);
                            return ActionResult.SUCCESS;
                        }
                    }
                } else {
                    if (world.isClient()) {
                        player.sendMessage(Text.translatable("message.hexalia.invalid_item"), true);
                    }
                    return ActionResult.FAIL;
                }
            }
        }

        return ActionResult.PASS;
    }

    private void sendEffectActivationMessage(World world, BlockPos pos, HerbCombination combo, PlayerEntity activatingPlayer) {
        String messageKey = CenserEffectHandler.getMessageKeyForCombination(combo);
        Box area = new Box(pos).expand(CenserEffectHandler.AREA_RADIUS);

        for (PlayerEntity player : world.getEntitiesByType(EntityType.PLAYER, area, EntityPredicates.VALID_ENTITY)) {
            if (!player.getUuid().equals(activatingPlayer.getUuid()) && player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.sendMessage(Text.translatable(messageKey), true);
            }
        }

        if (!world.isClient() && activatingPlayer instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(Text.translatable(messageKey), true);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.CENSER_BE, CenserBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CenserBlockEntity(pos, state);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT)) {
            if (random.nextInt(10) == 0) {
                world.playSound(
                        pos.getX() + 0.5D,
                        pos.getY() + 0.5D,
                        pos.getZ() + 0.5D,
                        SoundEvents.BLOCK_CAMPFIRE_CRACKLE,
                        SoundCategory.BLOCKS,
                        0.5F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.6F,
                        false
                );
            }

            world.addParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    true,
                    pos.getX() + 0.5D + random.nextDouble() / 3.0D * (random.nextBoolean() ? 1 : -1),
                    pos.getY() + random.nextDouble() + random.nextDouble(),
                    pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (random.nextBoolean() ? 1 : -1),
                    0.0D,
                    0.07D,
                    0.0D
            );

            world.addParticle(
                    ParticleTypes.SMOKE,
                    pos.getX() + 0.5D + random.nextDouble() / 4.0D * (random.nextBoolean() ? 1 : -1),
                    pos.getY() + 0.4D,
                    pos.getZ() + 0.5D + random.nextDouble() / 4.0D * (random.nextBoolean() ? 1 : -1),
                    0.0D,
                    0.005D,
                    0.0D
            );

            // Rare lava particles (less frequent than before)
            if (random.nextInt(25) == 0) {
                world.addParticle(
                        ParticleTypes.LAVA,
                        pos.getX() + 0.5D,
                        pos.getY() + 0.3D,
                        pos.getZ() + 0.5D,
                        random.nextFloat() / 8.0F,
                        0.0D,
                        random.nextFloat() / 8.0F
                );
            }
        }
    }
}
