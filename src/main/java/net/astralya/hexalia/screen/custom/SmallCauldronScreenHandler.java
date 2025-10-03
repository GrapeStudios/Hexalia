package net.astralya.hexalia.screen.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.custom.SmallCauldronBlockEntity;
import net.astralya.hexalia.screen.ModScreenHandlers;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

public class SmallCauldronScreenHandler extends ScreenHandler {

    private static final int TE_SIZE = 5;

    private final PropertyDelegate properties;
    private final ScreenHandlerContext context;
    public final SmallCauldronBlockEntity blockEntity;

    public SmallCauldronScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(
                syncId,
                playerInventory,
                playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(2)
        );
    }

    public SmallCauldronScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity be, PropertyDelegate properties) {
        super(ModScreenHandlers.SMALL_CAULDRON_SCREEN, syncId);
        Inventory inventory = (Inventory) be;
        this.blockEntity = (SmallCauldronBlockEntity) be;
        this.properties = properties;
        this.context = ScreenHandlerContext.create(playerInventory.player.getWorld(), blockEntity.getPos());

        checkSize(inventory, TE_SIZE);

        addSlot(new Slot(inventory, 0, 30, 27) {
            @Override public boolean canInsert(ItemStack stack) {
                return !stack.isIn(ModTags.Items.BREWS) && super.canInsert(stack);
            }

        });
        addSlot(new Slot(inventory, 1, 48, 27) {
            @Override public boolean canInsert(ItemStack stack) {
                return !stack.isIn(ModTags.Items.BREWS) && super.canInsert(stack);
            }
        });

        addSlot(new Slot(inventory, 2, 66, 27) {
            @Override public boolean canInsert(ItemStack stack) {
                return !stack.isIn(ModTags.Items.BREWS) && super.canInsert(stack);
            }
        });

        addSlot(new Slot(inventory, 3, 124, 28) {
            @Override public boolean canInsert(ItemStack stack) { return false; }
        });

        addSlot(new Slot(inventory, 4, 48, 48) {
            @Override public boolean canInsert(ItemStack stack) {
                return stack.isIn(ModTags.Items.BREWS);
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(properties);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(context, player, ModBlocks.SMALL_CAULDRON);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        Slot slot = slots.get(invSlot);
        if (!slot.hasStack()) return ItemStack.EMPTY;

        ItemStack source = slot.getStack();
        ItemStack newStack = source.copy();

        int teStart = 0;
        int playerStart = teStart + TE_SIZE;
        int hotbarStart = playerStart + 27;
        int hotbarEnd = hotbarStart + 9;

        if (invSlot < playerStart) {
            if (!insertItem(source, playerStart, hotbarEnd, true)) return ItemStack.EMPTY;
        } else {
            if (source.isIn(ModTags.Items.BREWS)) {
                if (!insertItem(source, 4, 5, false)) return ItemStack.EMPTY;
            } else {
                if (!insertItem(source, 0, 3, false)) return ItemStack.EMPTY;
            }
        }

        if (source.isEmpty()) slot.setStack(ItemStack.EMPTY);
        else slot.markDirty();

        return newStack;
    }

    public boolean isCrafting() {
        return properties.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = properties.get(0);
        int max = properties.get(1);
        int px = 26;
        return max != 0 && progress != 0 ? progress * px / max : 0;
    }

    public boolean isHeated() {
        return blockEntity.isHeated();
    }

    private void addPlayerInventory(PlayerInventory inv) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                addSlot(new Slot(inv, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory inv) {
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(inv, i, 8 + i * 18, 142));
        }
    }
}