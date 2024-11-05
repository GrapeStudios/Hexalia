package net.grapes.hexalia.screen;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;

public class BrewShelfMenu extends AbstractContainerMenu {

    private final NonNullList<ItemStack> inventory;

    public BrewShelfMenu(int syncId, Inventory playerInventory, NonNullList<ItemStack> inventory) {
        super(MenuType.GENERIC_9x3, syncId);
        this.inventory = inventory;

        for (int i = 0; i < inventory.size(); i++) {
            this.addSlot(new BrewSlot(inventory, i, 8 + (i % 9) * 18, 18 + (i / 9) * 18));
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemstack = itemStack1.copy();

            if (index < this.inventory.size()) {
                if (!this.moveItemStackTo(itemStack1, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack1, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    public static class BrewSlot extends Slot {
        public BrewSlot(NonNullList<ItemStack> inventory, int index, int x, int y) {
            super(new net.minecraft.world.SimpleContainer(inventory.size()), index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(Tags.Items.DYES);
        }
    }
}
