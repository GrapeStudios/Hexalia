package net.astralya.hexalia.menu;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class NestingBlockMenu extends ScreenHandler {

    public static final int COLUMNS = 9;
    public static final int ROWS = 1;
    public static final int CONTAINER_SLOTS = COLUMNS * ROWS;
    private static final int PLAYER_INV_SLOTS = 27;
    private static final int HOTBAR_SLOTS = 9;

    private final Inventory container;

    public NestingBlockMenu(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, buf.readBlockPos());
    }

    public NestingBlockMenu(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, resolveContainer(playerInventory, pos));
    }

    public NestingBlockMenu(int syncId, PlayerInventory playerInventory, Inventory container) {
        super(ModMenuTypes.NESTING_BLOCK_MENU, syncId);
        this.container = container;
        checkSize(container, CONTAINER_SLOTS);
        container.onOpen(playerInventory.player);

        int slotIndex = 0;
        int containerX = 8;
        int containerY = 18;

        for (int col = 0; col < COLUMNS; col++) {
            this.addSlot(new OutputOnlySlot(container, slotIndex++, containerX + col * 18, containerY));
        }

        int playerInvX = 8;
        int playerInvY = 64;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = col + row * 9 + 9;
                this.addSlot(new Slot(playerInventory, index, playerInvX + col * 18, playerInvY + row * 18));
            }
        }

        int hotbarY = playerInvY + 58;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, playerInvX + col * 18, hotbarY));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.container.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = slot.getStack();
        ItemStack copy = stackInSlot.copy();

        int containerEnd = CONTAINER_SLOTS;
        int playerInvStart = containerEnd;
        int playerInvEnd = playerInvStart + PLAYER_INV_SLOTS;
        int hotbarStart = playerInvEnd;
        int hotbarEnd = hotbarStart + HOTBAR_SLOTS;

        if (index < containerEnd) {
            if (!this.insertItem(stackInSlot, playerInvStart, hotbarEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (stackInSlot.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        return copy;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.container.onClose(player);
    }

    public Inventory getContainer() {
        return this.container;
    }

    private static Inventory resolveContainer(PlayerInventory playerInventory, BlockPos pos) {
        if (playerInventory.player.getWorld().getBlockEntity(pos) instanceof Inventory inventory) {
            return inventory;
        }
        return new SimpleInventory(CONTAINER_SLOTS);
    }

    private static final class OutputOnlySlot extends Slot {

        public OutputOnlySlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }
    }
}