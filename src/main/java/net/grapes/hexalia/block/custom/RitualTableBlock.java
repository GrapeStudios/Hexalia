package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.RitualTableBlockEntity;
import net.grapes.hexalia.block.entity.SaltBlockEntity;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.particle.ModParticles;
import net.grapes.hexalia.recipe.TransmutationRecipe;
import net.grapes.hexalia.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class RitualTableBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final VoxelShape SHAPE = createShape();

    public RitualTableBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    private static VoxelShape createShape() {
        return Shapes.or(
                Shapes.box(0.1875, 0, 0.1875, 0.8125, 0.125, 0.8125),
                Shapes.box(0.25, 0.125, 0.25, 0.75, 0.625, 0.75),
                Shapes.box(0.1875, 0.625, 0.1875, 0.8125, 0.6875, 0.8125),
                Shapes.box(0.125, 0.6875, 0.125, 0.875, 0.8125, 0.875)
        );
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RitualTableBlockEntity(pPos, pState);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof RitualTableBlockEntity ritualTable) {
                ItemStack stack = ritualTable.getItem(0);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(pLevel, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5, stack);
                }
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack heldItem = pPlayer.getItemInHand(pHand);
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);

        if (!(blockEntity instanceof RitualTableBlockEntity ritualTableBlockEntity)) {
            return InteractionResult.PASS;
        }

        if (heldItem.getItem().equals(ModItems.HEX_FOCUS.get())) {
            if (performTransmutation(ritualTableBlockEntity, pLevel, pPos)) {
                spawnParticleEffect(pLevel, pPos, ParticleTypes.ENCHANT, 15, 20);
                spawnParticleEffect(pLevel, pPos, ModParticles.LEAVES_PARTICLE.get(), 15, 20);
                playRitualSound(pLevel, ritualTableBlockEntity.getBlockPos());
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            } else {
                spawnParticleEffect(pLevel, pPos, ParticleTypes.SMOKE, 15, 20);
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }
        } else {
            return handleItemInteraction(pLevel, ritualTableBlockEntity, pPlayer, pHand);
        }
    }

    private InteractionResult handleItemInteraction(Level pLevel, RitualTableBlockEntity ritualTableBlockEntity, Player player, InteractionHand pHand) {
        if (ritualTableBlockEntity.isEmpty()) {
            InteractionResult result = addItemFromHand(pLevel, ritualTableBlockEntity, player, pHand);
            if (result.consumesAction()) {
                spawnParticleEffect(pLevel, ritualTableBlockEntity.getBlockPos(), ParticleTypes.POOF, 5, 10);
            }
            return result;
        } else if (pHand.equals(InteractionHand.MAIN_HAND)) {
            removeItemFromBlock(pLevel, ritualTableBlockEntity, player);
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult addItemFromHand(Level pLevel, RitualTableBlockEntity ritualTableBlockEntity, Player player, InteractionHand pHand) {
        ItemStack heldItem = player.getItemInHand(pHand);
        ItemStack offHandItem = player.getOffhandItem();

        if (!offHandItem.isEmpty() && pHand.equals(InteractionHand.OFF_HAND)) {
            return InteractionResult.PASS;
        }
        if (heldItem.isEmpty()) {
            return InteractionResult.PASS;
        } else if (ritualTableBlockEntity.addStack(player.getAbilities().instabuild ? heldItem.copy() : heldItem)) {
            playItemSound(pLevel, ritualTableBlockEntity.getBlockPos());
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        return InteractionResult.PASS;
    }

    private void removeItemFromBlock(Level pLevel, RitualTableBlockEntity ritualTableBlockEntity, Player player) {
        BlockPos pos = ritualTableBlockEntity.getBlockPos();
        if (player.getAbilities().instabuild) {
            ritualTableBlockEntity.removeStack();
        } else {
            ItemStack stack = ritualTableBlockEntity.removeStack();
            if (!player.getInventory().add(stack)) {
                Containers.dropItemStack(pLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            }
        }
        playItemSound(pLevel, pos);
    }

    private boolean performTransmutation(RitualTableBlockEntity ritualTable, Level world, BlockPos pos) {
        SimpleContainer inventory = new SimpleContainer(ritualTable.getContainerSize());
        ItemStack inputStack = ritualTable.getItem(0);

        if (!isRitualReady(world, pos)) {
            return false;
        }

        if (inputStack.isEmpty()) {
            sendMessageToPlayer(world, pos, "message.hexalia.ritual.missing_ingredients");
            return false;
        }

        inventory.setItem(0, inputStack);
        Optional<TransmutationRecipe> recipeOptional = world.getRecipeManager().getRecipeFor(
                TransmutationRecipe.Type.INSTANCE, ritualTable, world);

        if (recipeOptional.isEmpty()) {
            sendMessageToPlayer(world, pos, "message.hexalia.ritual.missing_ingredients");
            return false;
        }

        TransmutationRecipe recipe = recipeOptional.get();
        boolean hasRequiredSalt = processSaltBlocks(world, pos, recipe, false);

        if (!hasRequiredSalt) {
            sendMessageToPlayer(world, pos, "message.hexalia.ritual.missing_ingredients");
            return false;
        }

        processSaltBlocks(world, pos, recipe, true);
        ritualTable.removeItem(0, 1);
        ritualTable.setItem(0, recipe.getResultItem(world.registryAccess()).copy());
        ritualTable.setChanged();
        world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), Block.UPDATE_ALL);

        validateAndResetCrops(world, pos, true);
        return true;
    }

    private boolean processSaltBlocks(Level pLevel, BlockPos tablePos, TransmutationRecipe pRecipe, boolean consume) {
        NonNullList<ItemStack> requiredSaltItems = NonNullList.create();
        requiredSaltItems.addAll(pRecipe.getSaltItems());

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos saltPos = tablePos.relative(direction, 2);
            BlockEntity saltEntity = pLevel.getBlockEntity(saltPos);

            if (saltEntity instanceof SaltBlockEntity saltBlock) {
                Iterator<ItemStack> iterator = requiredSaltItems.iterator();
                while (iterator.hasNext()) {
                    ItemStack item = iterator.next();
                    if (ItemStack.isSameItemSameTags(saltBlock.getItem(0), item)) {
                        iterator.remove();
                        if (consume) {
                            saltBlock.removeStack();
                            saltBlock.setChanged();
                            pLevel.sendBlockUpdated(saltPos, pLevel.getBlockState(saltPos), pLevel.getBlockState(saltPos), Block.UPDATE_ALL);
                        }
                        break;
                    }
                }
            }
        }
        return requiredSaltItems.isEmpty();
    }

    private boolean isRitualReady(Level world, BlockPos tablePos) {

        BlockPos[] saltPositions = {
                tablePos.offset(-2, 0, 0),
                tablePos.offset(2, 0, 0),
                tablePos.offset(0, 0, -2),
                tablePos.offset(0, 0, 2)
        };

        for (BlockPos pos : saltPositions) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof SaltBlockEntity)) {
                sendMessageToPlayer(world, tablePos, "message.hexalia.ritual.missing_ingredients");
                return false;
            }
        }

        boolean cropsValid = validateAndResetCrops(world, tablePos, false);
        if (!cropsValid) {
            sendMessageToPlayer(world, tablePos, "message.hexalia.ritual.invalid_crops");
        }

        return cropsValid;
    }

    private boolean validateAndResetCrops(Level world, BlockPos tablePos, boolean resetCrops) {
        BlockPos[] cropPositions = {
                tablePos.offset(-2, 0, -2), tablePos.offset(-1, 0, -2), tablePos.offset(1, 0, -2), tablePos.offset(2, 0, -2),
                tablePos.offset(-2, 0, -1), tablePos.offset(-1, 0, -1), tablePos.offset(1, 0, -1), tablePos.offset(2, 0, -1),
                tablePos.offset(-2, 0,  1), tablePos.offset(-1, 0,  1), tablePos.offset(1, 0,  1), tablePos.offset(2, 0,  1),
                tablePos.offset(-2, 0,  2), tablePos.offset(-1, 0,  2), tablePos.offset(1, 0,  2), tablePos.offset(2, 0,  2)
        };

        boolean allCropsValid = true;

        for (BlockPos pos : cropPositions) {
            BlockState state = world.getBlockState(pos);

            if (state.getBlock() instanceof CropBlock crop) {
                if (crop.getAge(state) < crop.getMaxAge()) {
                    allCropsValid = false;

                } else if (resetCrops) {
                    BlockState resetState = crop.getStateForAge(0);
                    world.setBlockAndUpdate(pos, resetState);
                }
            } else {
                allCropsValid = false;
            }
        }
        return allCropsValid;
    }

    private void sendMessageToPlayer(Level world, BlockPos pos, String messageKey) {
        if (!world.isClientSide) {
            Player nearestPlayer = world.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, false);
            if (nearestPlayer != null) {
                nearestPlayer.displayClientMessage(Component.translatable(messageKey), true);
            }
        }
    }

    private void spawnParticleEffect(Level pLevel, BlockPos pPos, SimpleParticleType particleType, int minParticles, int maxParticles) {
        int particleCount = ThreadLocalRandom.current().nextInt(minParticles, maxParticles);
        for (int i = 0; i < particleCount; i++) {
            double offsetX = ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
            double offsetY = ThreadLocalRandom.current().nextDouble(0, 0.5);
            double offsetZ = ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
            pLevel.addParticle(particleType, pPos.getX() + 0.5 + offsetX, pPos.getY() + 1.0 + offsetY, pPos.getZ() + 0.5 + offsetZ, 0, 0, 0);
        }
    }

    private void playItemSound(Level pLevel, BlockPos pos) {
        pLevel.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8f, 0.5f);
        pLevel.playSound(null, pos, SoundEvents.CHISELED_BOOKSHELF_PICKUP_ENCHANTED, SoundSource.BLOCKS, 0.8f, 0.5f);
    }

    private void playRitualSound(Level pLevel, BlockPos pos) {
        pLevel.playSound(null, pos, ModSounds.RITUAL_SUCCESS.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
    }
}
