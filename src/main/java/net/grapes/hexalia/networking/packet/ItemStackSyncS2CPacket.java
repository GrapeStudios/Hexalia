package net.grapes.hexalia.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.grapes.hexalia.block.entity.BrewShelfBlockEntity;
import net.grapes.hexalia.block.entity.RitualTableBlockEntity;
import net.grapes.hexalia.block.entity.SaltBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class ItemStackSyncS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        int size = buf.readInt();
        DefaultedList<ItemStack> list = DefaultedList.ofSize(size, ItemStack.EMPTY);

        for (int i = 0; i < size; i++) {
            list.set(i, buf.readItemStack());
        }

        BlockPos position = buf.readBlockPos();

        // Ensure the client world and block entity are valid
        if (client.world != null && client.world.getBlockEntity(position) instanceof RitualTableBlockEntity blockEntity) {
            blockEntity.setInventory(list);
        }
        if (client.world != null && client.world.getBlockEntity(position) instanceof SaltBlockEntity blockEntity) {
            blockEntity.setInventory(list);
        }
    }
}
