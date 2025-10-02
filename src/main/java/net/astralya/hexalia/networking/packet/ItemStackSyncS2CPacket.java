package net.astralya.hexalia.networking.packet;

import net.astralya.hexalia.block.entity.custom.RitualBrazierBlockEntity;
import net.astralya.hexalia.block.entity.custom.RitualTableBlockEntity;
import net.astralya.hexalia.block.entity.custom.ShelfBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ItemStackSyncS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        DefaultedList<ItemStack> stacks = readStacksFromBuffer(buf);
        BlockPos pos = buf.readBlockPos();

        client.execute(() -> handleBlockEntityUpdate(client, pos, stacks));
    }

    private static DefaultedList<ItemStack> readStacksFromBuffer(PacketByteBuf buf) {
        int size = buf.readInt();
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
        for (int i = 0; i < size; i++) {
            stacks.set(i, buf.readItemStack());
        }
        return stacks;
    }

    private static void handleBlockEntityUpdate(MinecraftClient client, BlockPos pos, DefaultedList<ItemStack> stacks) {
        if (client.world == null) return;

        @Nullable BlockEntity blockEntity = client.world.getBlockEntity(pos);
        if (blockEntity == null) return;

        if (blockEntity instanceof RitualTableBlockEntity table) {
            table.setInventory(stacks);
        } else if (blockEntity instanceof RitualBrazierBlockEntity brazier) {
            for (int i = 0; i < stacks.size(); i++) {
                brazier.setStack(i, stacks.get(i));
            }
            brazier.markDirty();
        } else if (blockEntity instanceof ShelfBlockEntity shelf) {
            for (int i = 0; i < stacks.size(); i++) {
                shelf.setStack(i, stacks.get(i));
            }
            shelf.markDirty();
        }
    }

    private final DefaultedList<ItemStack> stacks;
    private final BlockPos pos;

    public ItemStackSyncS2CPacket(DefaultedList<ItemStack> stacks, BlockPos pos) {
        this.stacks = stacks;
        this.pos = pos;
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(stacks.size());
        for (ItemStack stack : stacks) {
            buf.writeItemStack(stack);
        }
        buf.writeBlockPos(pos);
    }
}