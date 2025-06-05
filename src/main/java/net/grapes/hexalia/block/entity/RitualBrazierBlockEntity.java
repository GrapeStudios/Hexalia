package net.grapes.hexalia.block.entity;

import net.grapes.hexalia.recipe.RitualBrazierRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RitualBrazierBlockEntity extends BlockEntity implements WorldlyContainer {

    private static final int MOONLIGHT_DURATION = 400;
    private static final BlockPos.MutableBlockPos mutableWorldPosition = new BlockPos.MutableBlockPos();
    private int timer = 0;
    private boolean active = false;
    private NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

    public RitualBrazierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RITUAL_BRAZIER_BE.get(), pos, state);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, RitualBrazierBlockEntity pBlockEntity) {
        if (pLevel.isClientSide) return;

        if (pBlockEntity.active && !pBlockEntity.isEmpty()) {
            ItemStack itemStack = pBlockEntity.getItem(0);
            Optional<RitualBrazierRecipe> recipe = pLevel.getRecipeManager()
                    .getRecipeFor(RitualBrazierRecipe.Type.INSTANCE, new SimpleContainer(itemStack), pLevel);

            if (recipe.isPresent()) {
                if (canPerformMoonlightRitual(pLevel, pPos)) {
                    pBlockEntity.timer++;

                    if (pBlockEntity.timer >= MOONLIGHT_DURATION) {
                        pBlockEntity.setItem(0, ItemStack.EMPTY);
                        pBlockEntity.timer = 0;
                        pBlockEntity.setActive(false);
                        pBlockEntity.setChanged();

                        ItemStack resultStack = recipe.get().getResultItem(pLevel.registryAccess()).copy();
                        Containers.dropItemStack(pLevel, pPos.getX() + 0.5, pPos.getY() + 1.0, pPos.getZ() + 0.5, resultStack);

                        spawnPoofParticles(pLevel, pPos);
                        pLevel.playSound(null, pPos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
                    }
                } else {
                    pBlockEntity.cancelRitual();
                }
            } else {
                pBlockEntity.cancelRitual();
            }
        }
    }

    public boolean startMoonRitual(Player player) {
        if (isEmpty()) {
            player.displayClientMessage(Component.translatable("message.hexalia.moonlight_ritual.invalid_item"), true);
            return false;
        }

        ItemStack itemStack = getItem(0);
        Optional<RitualBrazierRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(RitualBrazierRecipe.Type.INSTANCE, new SimpleContainer(itemStack), level);

        if (recipe.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.hexalia.moonlight_ritual.invalid_item"), true);
            return false;
        }

        if (!canPerformMoonlightRitual(level, worldPosition)) {
            player.displayClientMessage(Component.translatable("message.hexalia.moonlight_ritual.not_night"), true);
            return false;
        }

        this.timer = 1;
        this.setActive(true);
        player.displayClientMessage(Component.translatable("message.hexalia.moonlight_ritual.started"), true);
        return true;
    }

    public void cancelRitual() {
        this.timer = 0;
        this.setActive(false);
        this.setChanged();
    }

    private static void spawnPoofParticles(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;

            serverLevel.sendParticles(ParticleTypes.POOF, x, y, z, 10, 0.2, 0.2, 0.2, 0.02);
        }
    }

    public void setActive(boolean active) {
        if (this.active != active) {
            this.active = active;
            this.setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(),
                        Block.UPDATE_ALL);
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    private static boolean canPerformMoonlightRitual(Level level, BlockPos pos) {
        int moonPhase = level.getMoonPhase();
        return (moonPhase == 0 || moonPhase == 1 || moonPhase == 7) &&
                level.canSeeSky(pos.above());
    }

    @Override
    public int[] getSlotsForFace(@NotNull Direction direction) {
        return new int[]{0};
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
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.active = tag.getBoolean("Active");
        ContainerHelper.loadAllItems(tag, this.inventory);
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= this.inventory.size()) {
            return ItemStack.EMPTY;
        }
        return this.inventory.get(slot);
    }

    public ItemStack getRenderStack() {
        return getItem(0);
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
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
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
        this.timer = pTag.getInt("Timer");
        this.active = pTag.getBoolean("Active");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        ContainerHelper.saveAllItems(pTag, inventory);
        pTag.putInt("Timer", timer);
        pTag.putBoolean("Active", active);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putBoolean("Active", this.active);
        ContainerHelper.saveAllItems(tag, inventory);
        return tag;
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
            if (this.active) {
                this.cancelRitual();
            }
            ItemStack itemStack = getItem(0).copy();
            setItem(0, ItemStack.EMPTY);
            setChanged();
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        if (level != null && level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(),
                    Block.UPDATE_ALL);
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}