package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.entity.CenserBlockEntity;
import net.astralya.hexalia.block.entity.ModBlockEntities;
import net.astralya.hexalia.censer.CenserEffectHandler;
import net.astralya.hexalia.censer.HerbCombination;
import net.astralya.hexalia.util.ModTags;
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
        Vec3d offset = state.getModelOffset(world, pos);
        return SHAPE.offset(offset.x, offset.y, offset.z);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            world.scheduleBlockTick(pos, this, 1);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof CenserBlockEntity censer) {
            CenserBlockEntity.tick(world, pos, state, censer);
        }
        world.scheduleBlockTick(pos, this, 1);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CenserBlockEntity censer) {
                for (int i = 0; i < censer.getItems().size(); i++) {
                    ItemStack stack = censer.getStack(i);
                    if (!stack.isEmpty()) {
                        Block.dropStack(world, pos, stack);
                    }
                }
                world.updateComparators(pos, this);
            }
        } else {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CenserBlockEntity censer) {
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
        Direction face = ctx.getSide();
        if (face.getAxis() == Direction.Axis.Y) {
            return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        }
        return this.getDefaultState().with(FACING, face);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack held = player.getStackInHand(hand);
        BlockEntity be = world.getBlockEntity(pos);

        if (!(be instanceof CenserBlockEntity censer)) return ActionResult.PASS;

        if (held.getItem() instanceof FlintAndSteelItem && !state.get(LIT)) {
            ItemStack herb1 = censer.getStack(0);
            ItemStack herb2 = censer.getStack(1);

            if (herb1.isEmpty() || herb2.isEmpty()) {
                if (world.isClient()) player.sendMessage(Text.translatable("message.hexalia.censer_not_full"), true);
                return ActionResult.FAIL;
            }

            HerbCombination combo = new HerbCombination(herb1.getItem(), herb2.getItem());
            if (!CenserEffectHandler.isValidCombination(herb1.getItem(), herb2.getItem())) {
                if (world.isClient()) player.sendMessage(Text.translatable("message.hexalia.invalid_herb_combination"), true);
                return ActionResult.FAIL;
            }

            if (!world.isClient()) {
                sendEffectActivationMessage(world, pos, combo, player);

                censer.setActiveCombination(combo);
                censer.clearInventory();

                world.setBlockState(pos, state.with(LIT, true));

                int burn = Math.max(1, Configuration.get().censerEffectDuration);
                censer.setBurnTime(burn);                 // burn time from config
                CenserEffectHandler.startEffect(world, pos, combo);
            }

            held.damage(1, player, p -> p.sendToolBreakStatus(hand));
            world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, world.random.nextFloat() * 0.4F + 0.8F);
            return ActionResult.SUCCESS;
        }

        if (held.getItem() instanceof ShovelItem && state.get(LIT)) {
            if (!world.isClient()) {
                world.setBlockState(pos, state.with(LIT, false));
                censer.setBurnTime(0);
                CenserEffectHandler.removeActiveEffect(pos);
                CenserEffectHandler.clearPlayerEffectsInRange(world, pos);
            }
            return ActionResult.SUCCESS;
        }

        if (!state.get(LIT)) {
            if (held.isEmpty()) {
                for (int i = 0; i < censer.getItems().size(); i++) {
                    ItemStack inSlot = censer.getStack(i);
                    if (!inSlot.isEmpty()) {
                        ItemStack removed = censer.removeStack(i);
                        if (!player.getInventory().insertStack(removed)) {
                            player.dropItem(removed, false);
                        }
                        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5f, 1.0f);
                        return ActionResult.SUCCESS;
                    }
                }
            } else {
                if (held.isIn(ModTags.Items.HERBS)) {
                    for (int i = 0; i < censer.getItems().size(); i++) {
                        if (censer.getStack(i).isEmpty()) {
                            ItemStack toInsert = held.copy();
                            toInsert.setCount(1);
                            censer.setStack(i, toInsert);

                            if (!player.getAbilities().creativeMode) {
                                held.decrement(1);
                            }

                            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 0.5f, 1.0f);
                            return ActionResult.SUCCESS;
                        }
                    }
                } else {
                    if (world.isClient()) player.sendMessage(Text.translatable("message.hexalia.invalid_item"), true);
                    return ActionResult.FAIL;
                }
            }
        }

        return ActionResult.PASS;
    }

    private void sendEffectActivationMessage(World world, BlockPos pos, HerbCombination combo, PlayerEntity activatingPlayer) {
        String key = CenserEffectHandler.getMessageKeyForCombination(combo);
        int radius = Math.max(1, Configuration.get().censerEffectRadius);
        Box area = new Box(pos).expand(radius);

        for (PlayerEntity p : world.getEntitiesByType(EntityType.PLAYER, area, EntityPredicates.VALID_ENTITY)) {
            if (!p.getUuid().equals(activatingPlayer.getUuid()) && p instanceof ServerPlayerEntity sp) {
                sp.sendMessage(Text.translatable(key), true);
            }
        }
        if (!world.isClient() && activatingPlayer instanceof ServerPlayerEntity sp) {
            sp.sendMessage(Text.translatable(key), true);
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
        if (!state.get(LIT)) return;

        if (random.nextInt(10) == 0) {
            world.playSound(
                    pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS,
                    0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false
            );
        }

        world.addParticle(
                ParticleTypes.CAMPFIRE_COSY_SMOKE, true,
                pos.getX() + 0.5D + random.nextDouble() / 3.0D * (random.nextBoolean() ? 1 : -1),
                pos.getY() + random.nextDouble() + random.nextDouble(),
                pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (random.nextBoolean() ? 1 : -1),
                0.0D, 0.07D, 0.0D
        );

        world.addParticle(
                ParticleTypes.SMOKE,
                pos.getX() + 0.5D + random.nextDouble() / 4.0D * (random.nextBoolean() ? 1 : -1),
                pos.getY() + 0.4D,
                pos.getZ() + 0.5D + random.nextDouble() / 4.0D * (random.nextBoolean() ? 1 : -1),
                0.0D, 0.005D, 0.0D
        );

        if (random.nextInt(25) == 0) {
            world.addParticle(
                    ParticleTypes.LAVA,
                    pos.getX() + 0.5D, pos.getY() + 0.3D, pos.getZ() + 0.5D,
                    random.nextFloat() / 8.0F, 0.0D, random.nextFloat() / 8.0F
            );
        }
    }
}