package net.astralya.hexalia.block.entity;

import net.astralya.hexalia.block.custom.RitualBrazierBlock;
import net.astralya.hexalia.recipe.TransmutationRecipe;
import net.astralya.hexalia.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class RitualTableBlockEntity extends BlockEntity implements WorldlyContainer {

    private int progress = 0;
    private static final int MAX_PROGRESS = 120;
    private boolean ritualInProgress = false;

    NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

    public RitualTableBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RITUAL_TABLE_BE.get(), pPos, pBlockState);
    }

    public boolean startRitual() {
        return performTransmutation(level, worldPosition);
    }

    public void cancelRitual() {
        ritualInProgress = false;
        progress = 0;
        setChanged();
    }

    public static void tick(ServerLevel pLevel, BlockPos pPos, BlockState pState, RitualTableBlockEntity ritualTableBlockEntity) {
        if (ritualTableBlockEntity.ritualInProgress) {
            ritualTableBlockEntity.progress++;

            if (ritualTableBlockEntity.progress >= MAX_PROGRESS) {
                ritualTableBlockEntity.completeRitual(pLevel, pPos);
            }

            ritualTableBlockEntity.setChanged();
        }
    }

    private void completeRitual(ServerLevel level, BlockPos pos) {
        performRitualEffect(level, pos);
        cancelRitual();
    }

    private void performRitualEffect(ServerLevel level, BlockPos pos) {
        level.sendParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                20, 0.5, 0.5, 0.5, 0.1);
        level.playSound(null, pos, ModSounds.RITUAL_SUCCESS.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
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

    public boolean canStartRitual(Level world, BlockPos tablePos) {
        if (!isRitualReady(world, tablePos)) {
            return false;
        }

        ItemStack inputStack = getItem(0);
        if (inputStack.isEmpty()) {
            sendMessageToPlayer(world, tablePos, "message.hexalia.ritual.missing_ingredients");
            spawnParticleEffect(world, tablePos, ParticleTypes.SMOKE, 10, 20);
            return false;
        }

        Optional<TransmutationRecipe> recipeOptional = world.getRecipeManager().getRecipeFor(
                TransmutationRecipe.Type.INSTANCE, this, world);

        if (recipeOptional.isEmpty()) {
            sendMessageToPlayer(world, tablePos, "message.hexalia.ritual.missing_ingredients");
            spawnParticleEffect(world, tablePos, ParticleTypes.SMOKE, 10, 20);
            return false;
        }

        TransmutationRecipe recipe = recipeOptional.get();
        boolean hasRequiredSalt = processSaltBlocks(world, tablePos, recipe, false);

        if (!hasRequiredSalt) {
            sendMessageToPlayer(world, tablePos, "message.hexalia.ritual.missing_ingredients");
            spawnParticleEffect(world, tablePos, ParticleTypes.SMOKE, 10, 20);
            return false;
        }

        return true;
    }

    private boolean isRitualReady(Level world, BlockPos tablePos) {
        BlockPos[] brazierPositions = {
                tablePos.offset(-2, 0, 0),
                tablePos.offset(2, 0, 0),
                tablePos.offset(0, 0, -2),
                tablePos.offset(0, 0, 2)
        };

        for (BlockPos pos : brazierPositions) {
            BlockState blockState = world.getBlockState(pos);
            if (!(blockState.getBlock() instanceof RitualBrazierBlock)) {
                sendMessageToPlayer(world, tablePos, "message.hexalia.ritual.missing_brazier");
                return false;
            }

            if (!blockState.getValue(RitualBrazierBlock.SALTED)) {
                sendMessageToPlayer(world, tablePos, "message.hexalia.ritual.missing_salt");
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

    public boolean processSaltBlocks(Level pLevel, BlockPos tablePos, TransmutationRecipe pRecipe, boolean consume) {
        NonNullList<ItemStack> requiredSaltItems = NonNullList.create();
        requiredSaltItems.addAll(pRecipe.getSaltItems());

        BlockPos[] brazierPositions = {
                tablePos.offset(-2, 0, 0),
                tablePos.offset(2, 0, 0),
                tablePos.offset(0, 0, -2),
                tablePos.offset(0, 0, 2)
        };

        for (BlockPos pos : brazierPositions) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pos);
            if (blockEntity instanceof RitualBrazierBlockEntity brazier) {
                Iterator<ItemStack> iterator = requiredSaltItems.iterator();
                while (iterator.hasNext()) {
                    ItemStack item = iterator.next();
                    if (ItemStack.isSameItemSameTags(brazier.getStoredItem(), item)) {
                        iterator.remove();
                        if (consume) {
                            brazier.removeItem();
                            brazier.setChanged();

                            BlockState brazierState = pLevel.getBlockState(pos);
                            if (brazierState.getValue(RitualBrazierBlock.SALTED)) {
                                pLevel.setBlock(pos, brazierState.setValue(RitualBrazierBlock.SALTED, false), Block.UPDATE_ALL);
                            }

                            pLevel.sendBlockUpdated(pos, pLevel.getBlockState(pos), pLevel.getBlockState(pos), Block.UPDATE_ALL);
                        }
                        break;
                    }
                }
            }
        }
        return requiredSaltItems.isEmpty();
    }

    private void sendMessageToPlayer(Level world, BlockPos pos, String messageKey) {
        if (!world.isClientSide) {
            Player nearestPlayer = world.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, false);
            if (nearestPlayer != null) {
                nearestPlayer.displayClientMessage(Component.translatable(messageKey), true);
            }
        }
    }

    public boolean performTransmutation(Level world, BlockPos pos) {
        SimpleContainer inventory = new SimpleContainer(this.getContainerSize());
        ItemStack inputStack = this.getItem(0);

        if (!isRitualReady(world, pos)) {
            return false;
        }

        if (inputStack.isEmpty()) {
            sendMessageToPlayer(world, pos, "message.hexalia.ritual.missing_ingredients");
            return false;
        }

        inventory.setItem(0, inputStack);

        Optional<TransmutationRecipe> recipeOptional = world.getRecipeManager().getRecipeFor(
                TransmutationRecipe.Type.INSTANCE, this, world
        );

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
        this.removeItem(0, 1);
        this.setItem(0, recipe.getResultItem(world.registryAccess()).copy());
        this.setChanged();

        world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), Block.UPDATE_ALL);
        validateAndResetCrops(world, pos, true);

        return true;
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return true;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return inventory.get(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= this.inventory.size()) {
            return ItemStack.EMPTY;
        }
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(inventory, pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(inventory, pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        inventory.set(pSlot, pStack);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return worldPosition.distSqr(pPlayer.blockPosition()) <= 16;
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.inventory = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, inventory);
        this.progress = pTag.getInt("Progress");
        this.ritualInProgress = pTag.getBoolean("RitualInProgress");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        ContainerHelper.saveAllItems(pTag, inventory);
        pTag.putInt("Progress", this.progress);
        pTag.putBoolean("RitualInProgress", this.ritualInProgress);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public boolean addStack(ItemStack itemStack) {
        if (isEmpty() && !itemStack.isEmpty()) {
            setItem(0, itemStack.split(1));
            setChanged();
            return true;
        }
        return false;
    }

    public ItemStack removeStack() {
        if (!isEmpty()) {
            ItemStack itemStack = getItem(0).split(1);
            setChanged();
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getRenderStack() {
        ItemStack stack = inventory.get(0);
        if (stack.isEmpty()) {
            stack = inventory.get(0);
        }
        return stack;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
