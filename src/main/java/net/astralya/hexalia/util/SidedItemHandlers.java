package net.astralya.hexalia.util;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public final class SidedItemHandlers {
    private SidedItemHandlers() {
    }

    public static IItemHandler view(IItemHandler backing, int[] slots, boolean allowInsert, boolean allowExtract) {
        return new SlotsView(backing, slots, allowInsert, allowExtract);
    }

    public static IItemHandler upDown(@Nullable Direction side, IItemHandler upInput, IItemHandler downOutput) {
        if (side == null) return EmptyHandler.INSTANCE;
        if (side == Direction.UP) return upInput;
        if (side == Direction.DOWN) return downOutput;
        return EmptyHandler.INSTANCE;
    }

    public static IItemHandler southDown(@Nullable Direction side, IItemHandler southInput, IItemHandler downOutput) {
        if (side == null) return EmptyHandler.INSTANCE;
        if (side == Direction.SOUTH) return southInput;
        if (side == Direction.DOWN) return downOutput;
        return EmptyHandler.INSTANCE;
    }

    public static IItemHandler upOnly(@Nullable Direction side, IItemHandler upInput) {
        if (side == null) return EmptyHandler.INSTANCE;
        if (side == Direction.UP) return upInput;
        return EmptyHandler.INSTANCE;
    }

    public static IItemHandler blocked() {
        return EmptyHandler.INSTANCE;
    }

    private static final class SlotsView implements IItemHandler {
        private final IItemHandler backing;
        private final int[] slots;
        private final boolean allowInsert;
        private final boolean allowExtract;

        private SlotsView(IItemHandler backing, int[] slots, boolean allowInsert, boolean allowExtract) {
            this.backing = backing;
            this.slots = slots;
            this.allowInsert = allowInsert;
            this.allowExtract = allowExtract;
        }

        @Override
        public int getSlots() {
            return slots.length;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return backing.getStackInSlot(map(slot));
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!allowInsert) return stack;
            return backing.insertItem(map(slot), stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!allowExtract) return ItemStack.EMPTY;
            return backing.extractItem(map(slot), amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return backing.getSlotLimit(map(slot));
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (!allowInsert) return false;
            return backing.isItemValid(map(slot), stack);
        }

        private int map(int localSlot) {
            return slots[localSlot];
        }
    }

    private enum EmptyHandler implements IItemHandler {
        INSTANCE;

        @Override public int getSlots() { return 0; }
        @Override public ItemStack getStackInSlot(int slot) { return ItemStack.EMPTY; }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return stack; }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
        @Override public int getSlotLimit(int slot) { return 0; }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return false; }
    }
}