package net.grapes.hexalia.block.entity;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.grapes.hexalia.networking.ModMessages;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class RitualTableBlockEntity extends BlockEntity implements ImplementedInventory {

    private final DefaultedList<ItemStack> inventory;

    public RitualTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RITUAL_TABLE_BE, pos, state);
        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        super.readNbt(nbt);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (!world.isClient()) {
            sync();
        }
    }

    private void sync() {
        if (world instanceof ServerWorld serverWorld) {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeInt(inventory.size());
            for (ItemStack itemStack : inventory) {
                data.writeItemStack(itemStack);
            }
            data.writeBlockPos(getPos());

            for (ServerPlayerEntity player : PlayerLookup.tracking(serverWorld, getPos())) {
                ServerPlayNetworking.send(player, ModMessages.SYNC_ITEM, data);
            }
        }
    }

    public ItemStack getRenderStack() {
        return this.getStack(0);
    }

    public void setInventory(DefaultedList<ItemStack> list) {
        for (int i = 0; i < list.size(); i++) {
            this.inventory.set(i, list.get(i));
        }
        markDirty();
    }

    public boolean addItem(ItemStack itemStack) {
        if (isEmpty() && !itemStack.isEmpty()) {
            setStack(0, itemStack.split(1));
            markDirty();
            return true;
        }
        return false;
    }

    public ItemStack removeItem() {
        if (!isEmpty()) {
            ItemStack itemStack = getStoredItem().split(1);
            markDirty();
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    public boolean isEmpty() {
        return getStack(0).isEmpty();
    }

    public ItemStack getStoredItem() {
        return getStack(0);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        Inventories.writeNbt(nbtCompound, inventory, true);
        return nbtCompound;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
