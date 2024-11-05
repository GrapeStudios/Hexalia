package net.grapes.hexalia.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SaltBlockEntity extends BlockEntity implements WorldlyContainer {

    NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

    public SaltBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SALT_BE.get(), pPos, pBlockState);
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
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        ContainerHelper.saveAllItems(pTag, inventory);
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
            ItemStack itemStack = getItem(0).split(1); // Changed 1 to 0
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
