package net.astralya.hexalia.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class SidedItemHandlers {

    private SidedItemHandlers() {
    }

    public static Storage<ItemVariant> view(Inventory inventory, int[] slots, boolean allowInsert, boolean allowExtract) {
        return new SlotsView(inventory, slots, allowInsert, allowExtract);
    }

    public static Storage<ItemVariant> upDown(@Nullable Direction side, Storage<ItemVariant> upInput, Storage<ItemVariant> downOutput) {
        if (side == Direction.UP) return upInput;
        if (side == Direction.DOWN) return downOutput;
        return Storage.empty();
    }

    public static Storage<ItemVariant> southDown(@Nullable Direction side, Storage<ItemVariant> southInput, Storage<ItemVariant> downOutput) {
        if (side == Direction.SOUTH) return southInput;
        if (side == Direction.DOWN) return downOutput;
        return Storage.empty();
    }

    public static Storage<ItemVariant> upOnly(@Nullable Direction side, Storage<ItemVariant> upInput) {
        if (side == Direction.UP) return upInput;
        return Storage.empty();
    }

    public static Storage<ItemVariant> blocked() {
        return Storage.empty();
    }

    private static final class SlotsView implements Storage<ItemVariant> {

        private final Inventory inventory;
        private final int[] slots;
        private final boolean allowInsert;
        private final boolean allowExtract;

        private SlotsView(Inventory inventory, int[] slots, boolean allowInsert, boolean allowExtract) {
            this.inventory = inventory;
            this.slots = slots;
            this.allowInsert = allowInsert;
            this.allowExtract = allowExtract;
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            if (!allowInsert || maxAmount <= 0) return 0;

            long inserted = 0;

            for (int slot : slots) {
                ItemStack current = inventory.getStack(slot);
                if (!current.isEmpty() && !ItemVariant.of(current).equals(resource)) continue;

                int limit = current.isEmpty()
                        ? 1
                        : Math.min(1, inventory.getMaxCountPerStack());
                int space = limit - current.getCount();
                if (space <= 0) continue;

                int toInsert = (int) Math.min(space, maxAmount - inserted);
                if (toInsert <= 0) continue;

                transaction.addCloseCallback((t, result) -> {
                    if (result.wasCommitted()) {
                        if (current.isEmpty()) {
                            ItemStack insertedStack = resource.toStack(toInsert);
                            insertedStack.setCount(1);
                            inventory.setStack(slot, insertedStack);
                        } else {
                            current.increment(toInsert);
                            if (current.getCount() > 1) {
                                current.setCount(1);
                            }
                        }
                        inventory.markDirty();
                    }
                });

                inserted += toInsert;
                if (inserted >= maxAmount) break;
            }

            return inserted;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            if (!allowExtract) return 0;
            long extracted = 0;
            for (int slot : slots) {
                ItemStack current = inventory.getStack(slot);
                if (current.isEmpty() || !ItemVariant.of(current).equals(resource)) continue;
                int toExtract = (int) Math.min(current.getCount(), maxAmount - extracted);
                if (toExtract <= 0) continue;
                transaction.addCloseCallback((t, result) -> {
                    if (result.wasCommitted()) {
                        current.decrement(toExtract);
                        if (current.isEmpty()) inventory.setStack(slot, ItemStack.EMPTY);
                        inventory.markDirty();
                    }
                });
                extracted += toExtract;
                if (extracted >= maxAmount) break;
            }
            return extracted;
        }

        @Override
        public @NotNull Iterator<StorageView<ItemVariant>> iterator() {
            return new Iterator<>() {
                private int index = 0;

                @Override
                public boolean hasNext() {
                    return index < slots.length;
                }

                @Override
                public StorageView<ItemVariant> next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    int slot = slots[index++];
                    return new SingleSlotStorage<>() {
                        @Override
                        public long insert(ItemVariant resource, long maxAmount, TransactionContext tx) {
                            return allowInsert ? SlotsView.this.insert(resource, maxAmount, tx) : 0;
                        }

                        @Override
                        public long extract(ItemVariant resource, long maxAmount, TransactionContext tx) {
                            return allowExtract ? SlotsView.this.extract(resource, maxAmount, tx) : 0;
                        }

                        @Override
                        public boolean isResourceBlank() {
                            return inventory.getStack(slot).isEmpty();
                        }

                        @Override
                        public ItemVariant getResource() {
                            return ItemVariant.of(inventory.getStack(slot));
                        }

                        @Override
                        public long getAmount() {
                            return inventory.getStack(slot).getCount();
                        }

                        @Override
                        public long getCapacity() {
                            return inventory.getMaxCountPerStack();
                        }
                    };
                }
            };
        }
    }
}