package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.RitualTableBlockEntity;
import net.grapes.hexalia.block.entity.SaltBlockEntity;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.recipe.TransmutationRecipe;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class RitualTableBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE = createShape();

    public static final int REQUIRED_DISTANCE = 2;
    private static final double PARTICLE_OFFSET = 0.5;
    private static final int PARTICLE_COUNT = 8;

    public RitualTableBlock(Settings settings) {
        super(settings);
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
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
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
        ItemStack heldItem = player.getStackInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof RitualTableBlockEntity ritualTableBlockEntity)) {
            return ActionResult.PASS;
        }

        if (heldItem.getItem().equals(ModItems.HEX_FOCUS)) {
            if (performTransmutation(ritualTableBlockEntity, world, pos)) {
                spawnParticleEffect(world, pos, ParticleTypes.GLOW_SQUID_INK);
                return ActionResult.SUCCESS;
            } else {
                spawnParticleEffect(world, pos, ParticleTypes.SMOKE);
                return ActionResult.SUCCESS;
            }
        } else {
            return handleItemInteraction(world, ritualTableBlockEntity, player, hand);
        }
    }

    private ActionResult handleItemInteraction(World world, RitualTableBlockEntity ritualTableBlockEntity, PlayerEntity player, Hand hand) {
        if (ritualTableBlockEntity.isEmpty()) {
            return addItemFromHand(world, ritualTableBlockEntity, player, hand);
        } else if (hand.equals(Hand.MAIN_HAND)) {
            removeItemFromBlock(world, ritualTableBlockEntity, player);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private ActionResult addItemFromHand(World world, RitualTableBlockEntity ritualTableBlockEntity, PlayerEntity player, Hand hand) {
        ItemStack heldItem = player.getStackInHand(hand);
        ItemStack offHandItem = player.getOffHandStack();

        if (!offHandItem.isEmpty()) {
            if (hand.equals(Hand.MAIN_HAND) && !(heldItem.getItem() instanceof BlockItem)) {
                return ActionResult.PASS;
            }
            if (hand.equals(Hand.OFF_HAND)) {
                return ActionResult.PASS;
            }
        }
        if (heldItem.isEmpty()) {
            return ActionResult.PASS;
        } else if (ritualTableBlockEntity.addItem(player.getAbilities().creativeMode ? heldItem.copy() : heldItem)) {
            playPlaceSound(world, ritualTableBlockEntity.getPos());
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private void removeItemFromBlock(World world, RitualTableBlockEntity ritualTableBlockEntity, PlayerEntity player) {
        BlockPos pos = ritualTableBlockEntity.getPos();
        ItemStack removedItem = ritualTableBlockEntity.removeItem();

        if (player.isCreative()) {
            // Item is removed in creative mode without being added to the player's inventory
            ritualTableBlockEntity.removeItem();
        } else if (!player.getInventory().insertStack(removedItem)) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), removedItem);
        }
        playRemoveSound(world, pos);
    }

    private void playPlaceSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1f, 1f);
    }

    private void playRemoveSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
    }

    // Methods related to performing a ritual

    private boolean performTransmutation(RitualTableBlockEntity ritualTable, World world, BlockPos pos) {
        ItemStack inputStack = ritualTable.getStack(0);
        System.out.println("Performing transmutation with input: " + inputStack.getItem().getName().getString());

        Optional<TransmutationRecipe> recipeOptional = world.getRecipeManager().getFirstMatch(TransmutationRecipe.Type.INSTANCE, ritualTable, world);

        if (recipeOptional.isPresent()) {
            TransmutationRecipe recipe = recipeOptional.get();
            System.out.println("Found transmutation recipe with output: " + recipe.getOutput(world.getRegistryManager()).getItem().getName().getString());

            if (checkSaltBlocks(world, pos, recipe)) {
                System.out.println("Salt blocks have required items. Performing transmutation.");
                consumeSaltBlocks(world, pos, recipe);
                ritualTable.removeStack(0);
                ritualTable.setStack(0, recipe.getOutput(world.getRegistryManager()).copy());
                ritualTable.markDirty();
                world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_ALL);
                return true;
            } else {
                System.out.println("Salt blocks do not have the required items.");
            }
        } else {
            System.out.println("No matching transmutation recipe found.");
        }
        return false;
    }

    private boolean checkSaltBlocks(World world, BlockPos tablePos, TransmutationRecipe recipe) {
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        List<ItemStack> requiredSaltItems = new ArrayList<>(recipe.getSaltItems());

        for (Direction direction : directions) {
            BlockPos saltPos = tablePos.offset(direction, REQUIRED_DISTANCE);
            BlockEntity blockEntity = world.getBlockEntity(saltPos);

            if (!(blockEntity instanceof SaltBlockEntity saltBlockEntity)) {
                return false;
            }

            ItemStack saltStack = saltBlockEntity.getStack(0);
            boolean found = false;

            for (ItemStack requiredSaltItem : requiredSaltItems) {
                if (ItemStack.areItemsEqual(saltStack, requiredSaltItem)) {
                    requiredSaltItems.remove(requiredSaltItem);
                    found = true;
                    break;
                }
            }

            if (!found) {
                return false;
            }
        }

        return requiredSaltItems.isEmpty();
    }

    private void consumeSaltBlocks(World world, BlockPos tablePos, TransmutationRecipe recipe) {
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        List<ItemStack> requiredSaltItems = new ArrayList<>(recipe.getSaltItems());

        for (Direction direction : directions) {
            BlockPos saltPos = tablePos.offset(direction, REQUIRED_DISTANCE);
            BlockEntity blockEntity = world.getBlockEntity(saltPos);

            if (blockEntity instanceof SaltBlockEntity saltBlockEntity) {
                ItemStack saltStack = saltBlockEntity.getStack(0);

                for (ItemStack requiredSaltItem : requiredSaltItems) {
                    if (ItemStack.areItemsEqual(saltStack, requiredSaltItem)) {
                        saltBlockEntity.removeStack(0);
                        requiredSaltItems.remove(requiredSaltItem);
                        break;
                    }
                }
            }
        }
    }

    private void spawnParticleEffect(World world, BlockPos pos, ParticleEffect particleType) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double x = pos.getX() + 0.5 + random.nextDouble(-PARTICLE_OFFSET, PARTICLE_OFFSET);
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5 + random.nextDouble(-PARTICLE_OFFSET, PARTICLE_OFFSET);
            world.addParticle(particleType, x, y, z, 0.0D, 0.05D, 0.0D);
        }
    }
}
