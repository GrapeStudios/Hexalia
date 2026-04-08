package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.util.ModUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class ShelfBlockEntity extends BlockEntity implements SidedInventory {
    private static final int SIZE = 6;
    private static final int[] TOP_SLOTS = new int[]{0, 1, 2, 3, 4, 5};
    private static final int[] NO_SLOTS = new int[0];

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);

    public ShelfBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SHELF, pos, state);
    }

    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public ItemStack removeStack(int slot) {
        if (slot < 0 || slot >= SIZE) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = items.get(slot).copy();
        items.set(slot, ItemStack.EMPTY);
        markDirty();
        sendUpdate();
        return stack;
    }

    private void sendUpdate() {
        if (world != null && !world.isClient) {
            Packet<ClientPlayPacketListener> packet = toUpdatePacket();
            if (packet != null) {
                for (ServerPlayerEntity player : ModUtil.tracking((ServerWorld) world, pos)) {
                    player.networkHandler.sendPacket(packet);
                }
            }
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        for (int i = 0; i < SIZE; i++) {
            items.set(i, ItemStack.EMPTY);
            String key = "Slot" + i;
            if (nbt.contains(key, 10)) {
                ItemStack stack = ItemStack.fromNbt(nbt.getCompound(key));
                if (!stack.isEmpty() && (stack.getCount() <= 0 || stack.getCount() > stack.getMaxCount())) {
                    stack.setCount(1);
                }
                items.set(i, stack);
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        for (int i = 0; i < SIZE; i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                nbt.put("Slot" + i, stack.writeNbt(new NbtCompound()));
            }
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return side == Direction.UP ? TOP_SLOTS : NO_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction direction) {
        return direction == Direction.UP
                && slot >= 0
                && slot < SIZE
                && !stack.isEmpty()
                && items.get(slot).isEmpty();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot < 0 || slot >= SIZE) {
            return ItemStack.EMPTY;
        }
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack removed = Inventories.splitStack(items, slot, amount);
        if (!removed.isEmpty()) {
            markDirty();
            sendUpdate();
        }
        return removed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SIZE) {
            return;
        }

        items.set(slot, stack);
        markDirty();
        sendUpdate();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return slot >= 0 && slot < SIZE && !stack.isEmpty() && items.get(slot).isEmpty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return world != null
                && world.getBlockEntity(pos) == this
                && player.squaredDistanceTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clear() {
        items.clear();
        for (int i = 0; i < SIZE; i++) {
            items.set(i, ItemStack.EMPTY);
        }
        markDirty();
        sendUpdate();
    }
}