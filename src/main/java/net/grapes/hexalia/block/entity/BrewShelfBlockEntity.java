package net.grapes.hexalia.block.entity;

import net.grapes.hexalia.item.custom.brews.HomesteadBrewItem;
import net.grapes.hexalia.item.custom.brews.SlimeyStepBrewItem;
import net.grapes.hexalia.item.custom.brews.VigorBrewItem;
import net.grapes.hexalia.item.custom.brews.WardingBrewItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class BrewShelfBlockEntity extends BlockEntity implements Inventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(16, ItemStack.EMPTY);

    public BrewShelfBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BREW_SHELF_BE, pos, state);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : items) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(items, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(items, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        }
        return player.squaredDistanceTo(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);
    }

    public boolean addItem(ItemStack stack) {
        if (!isAllowedItem(stack)) {
            return false;
        }

        for (int i = 0; i < this.size(); i++) {
            ItemStack itemStack = this.getStack(i);
            if (itemStack.isEmpty()) {
                this.setStack(i, stack.copy());
                markDirty();
                return true;
            } else if (ItemStack.areItemsEqual(itemStack, stack) && itemStack.getCount() < itemStack.getMaxCount()) {
                int newCount = Math.min(itemStack.getCount() + stack.getCount(), itemStack.getMaxCount());
                itemStack.setCount(newCount);
                markDirty();
                return true;
            }
        }
        return false;
    }

    private boolean isAllowedItem(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof HomesteadBrewItem
                || item instanceof SlimeyStepBrewItem || item instanceof VigorBrewItem
                || item instanceof WardingBrewItem;
    }

    public ItemStack removeItem() {
        for (int i = this.size() - 1; i >= 0; i--) {
            ItemStack itemStack = this.getStack(i);
            if (!itemStack.isEmpty()) {
                ItemStack toRemove = itemStack.copy();
                this.setStack(i, ItemStack.EMPTY);
                markDirty();
                return toRemove;
            }
        }
        return ItemStack.EMPTY;
    }

    public DefaultedList<ItemStack> getItems() {
        return items;
    }
}
