package net.astralya.hexalia.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

@SuppressWarnings("UnstableApiUsage")
@ApiStatus.Experimental
public final class SidedItemHandlers {

    private SidedItemHandlers() {
    }

    public static Storage<ItemVariant> view(Inventory inventory, int[] slots, boolean allowInsert, boolean allowExtract) {
        return new SlotsView(inventory, slots, allowInsert, allowExtract);
    }

    public static Storage<ItemVariant> upDown(@Nullable Direction side, Storage<ItemVariant> upInput, Storage<ItemVariant> downOutput) {
        if (side == Direction.UP) {
            return upInput;
        }
        if (side == Direction.DOWN) {
            return downOutput;
        }
        return Storage.empty();
    }

    public static Storage<ItemVariant> southDown(@Nullable Direction side, Storage<ItemVariant> southInput, Storage<ItemVariant> downOutput) {
        if (side == Direction.SOUTH) {
            return southInput;
        }
        if (side == Direction.DOWN) {
            return downOutput;
        }
        return Storage.empty();
    }

    public static Storage<ItemVariant> upOnly(@Nullable Direction side, Storage<ItemVariant> upInput) {
        return side == Direction.UP ? upInput : Storage.empty();
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
            if (!this.allowInsert || maxAmount <= 0) {
                return 0;
            }

            long inserted = 0;

            for (int slot : this.slots) {
                ItemStack current = this.inventory.getStack(slot);
                if (!current.isEmpty() && !ItemVariant.of(current).equals(resource)) {
                    continue;
                }

                int limit = Math.min(resource.getItem().getMaxCount(), this.inventory.getMaxCountPerStack());
                int currentCount = current.isEmpty() ? 0 : current.getCount();
                int space = limit - currentCount;
                if (space <= 0) {
                    continue;
                }

                int toInsert = (int) Math.min(maxAmount - inserted, space);
                if (toInsert <= 0) {
                    continue;
                }

                transaction.addCloseCallback((tx, result) -> {
                    if (result.wasCommitted()) {
                        ItemStack stackInSlot = this.inventory.getStack(slot);
                        if (stackInSlot.isEmpty()) {
                            this.inventory.setStack(slot, resource.toStack(toInsert));
                        } else {
                            stackInSlot.increment(toInsert);
                        }
                        this.inventory.markDirty();
                    }
                });

                inserted += toInsert;
                if (inserted >= maxAmount) {
                    break;
                }
            }

            return inserted;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            if (!this.allowExtract || maxAmount <= 0) {
                return 0;
            }

            long extracted = 0;

            for (int slot : this.slots) {
                ItemStack current = this.inventory.getStack(slot);
                if (current.isEmpty() || !ItemVariant.of(current).equals(resource)) {
                    continue;
                }

                int toExtract = (int) Math.min(current.getCount(), maxAmount - extracted);
                if (toExtract <= 0) {
                    continue;
                }

                transaction.addCloseCallback((tx, result) -> {
                    if (result.wasCommitted()) {
                        ItemStack stackInSlot = this.inventory.getStack(slot);
                        stackInSlot.decrement(toExtract);
                        if (stackInSlot.isEmpty()) {
                            this.inventory.setStack(slot, ItemStack.EMPTY);
                        }
                        this.inventory.markDirty();
                    }
                });

                extracted += toExtract;
                if (extracted >= maxAmount) {
                    break;
                }
            }

            return extracted;
        }

        @Override
        public @NotNull Iterator<StorageView<ItemVariant>> iterator() {
            return new Iterator<>() {
                private int index;

                @Override
                public boolean hasNext() {
                    return this.index < SlotsView.this.slots.length;
                }

                @Override
                public StorageView<ItemVariant> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }

                    int slot = SlotsView.this.slots[this.index++];
                    return new SingleSlotStorage<>() {
                        @Override
                        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                            if (!SlotsView.this.allowInsert || maxAmount <= 0) {
                                return 0;
                            }

                            ItemStack current = SlotsView.this.inventory.getStack(slot);
                            if (!current.isEmpty() && !ItemVariant.of(current).equals(resource)) {
                                return 0;
                            }

                            int limit = Math.min(resource.getItem().getMaxCount(), SlotsView.this.inventory.getMaxCountPerStack());
                            int currentCount = current.isEmpty() ? 0 : current.getCount();
                            int space = limit - currentCount;
                            if (space <= 0) {
                                return 0;
                            }

                            int toInsert = (int) Math.min(maxAmount, space);
                            transaction.addCloseCallback((tx, result) -> {
                                if (result.wasCommitted()) {
                                    ItemStack stackInSlot = SlotsView.this.inventory.getStack(slot);
                                    if (stackInSlot.isEmpty()) {
                                        SlotsView.this.inventory.setStack(slot, resource.toStack(toInsert));
                                    } else {
                                        stackInSlot.increment(toInsert);
                                    }
                                    SlotsView.this.inventory.markDirty();
                                }
                            });
                            return toInsert;
                        }

                        @Override
                        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                            if (!SlotsView.this.allowExtract || maxAmount <= 0) {
                                return 0;
                            }

                            ItemStack current = SlotsView.this.inventory.getStack(slot);
                            if (current.isEmpty() || !ItemVariant.of(current).equals(resource)) {
                                return 0;
                            }

                            int toExtract = (int) Math.min(current.getCount(), maxAmount);
                            transaction.addCloseCallback((tx, result) -> {
                                if (result.wasCommitted()) {
                                    ItemStack stackInSlot = SlotsView.this.inventory.getStack(slot);
                                    stackInSlot.decrement(toExtract);
                                    if (stackInSlot.isEmpty()) {
                                        SlotsView.this.inventory.setStack(slot, ItemStack.EMPTY);
                                    }
                                    SlotsView.this.inventory.markDirty();
                                }
                            });
                            return toExtract;
                        }

                        @Override
                        public boolean isResourceBlank() {
                            return SlotsView.this.inventory.getStack(slot).isEmpty();
                        }

                        @Override
                        public ItemVariant getResource() {
                            return ItemVariant.of(SlotsView.this.inventory.getStack(slot));
                        }

                        @Override
                        public long getAmount() {
                            return SlotsView.this.inventory.getStack(slot).getCount();
                        }

                        @Override
                        public long getCapacity() {
                            ItemStack stack = SlotsView.this.inventory.getStack(slot);
                            return stack.isEmpty()
                                    ? SlotsView.this.inventory.getMaxCountPerStack()
                                    : Math.min(stack.getMaxCount(), SlotsView.this.inventory.getMaxCountPerStack());
                        }
                    };
                }
            };
        }
    }
}