package net.grapes.hexalia.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.networking.packet.ItemStackSyncS2CPacket;
import net.minecraft.util.Identifier;

public class ModMessages {
    public static final Identifier SYNC_ITEM = new Identifier(HexaliaMod.MOD_ID, "item_sync");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SYNC_ITEM, ItemStackSyncS2CPacket::receive);
    }
}
