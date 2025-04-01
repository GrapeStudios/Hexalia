package net.grapes.hexalia.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.grapes.hexalia.block.entity.RitualBrazierBlockEntity;
import net.grapes.hexalia.block.entity.RitualTableBlockEntity;
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
        // Read data from packet
        DefaultedList<ItemStack> stacks = readStacksFromBuffer(buf);
        BlockPos pos = buf.readBlockPos();

        // Update block entities
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
            handleTableUpdate(table, stacks);
        } else if (blockEntity instanceof RitualBrazierBlockEntity brazier) {
            handleBrazierUpdate(brazier, stacks);
        }
    }

    private static void handleTableUpdate(RitualTableBlockEntity table, DefaultedList<ItemStack> stacks) {
        table.setInventory(stacks);
    }

    private static void handleBrazierUpdate(RitualBrazierBlockEntity brazier, DefaultedList<ItemStack> stacks) {
        for (int i = 0; i < stacks.size(); i++) {
            brazier.setStack(i, stacks.get(i));
        }
        brazier.markDirty();
    }
}