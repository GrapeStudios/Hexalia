package net.grapes.hexalia.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.grapes.hexalia.block.custom.SmallCauldronBlock;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.screen.SmallCauldronScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SmallCauldronBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, HeatingBlockEntity {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    public static int INPUT_SLOTS = 0;
    public static int OUTPUT_SLOT = 1;
    public static int BOTTLE_SLOT = 2;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 72;

    public SmallCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMALL_CAULDRON_BE, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SmallCauldronBlockEntity.this.progress;
                    case 1 -> SmallCauldronBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: SmallCauldronBlockEntity.this.progress = value;
                    case 1: SmallCauldronBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Small Cauldron");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SmallCauldronScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }


    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("small_cauldron.progress", progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("small_cauldron.progress");
        super.readNbt(nbt);
    }

    public void brewingTick(World world, BlockPos pos, BlockState state) {
        boolean isHeated = isHeated(world, pos);
        if (canInsertOutputSlot() && hasRecipe() && hasRusticBottle() && isHeated) {
            increaseCraftingProgress();
            markDirty(world, pos, state);
            if(hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void craftItem() {
        this.removeStack(INPUT_SLOTS, 1);
        this.removeStack(BOTTLE_SLOT, 1);
        this.setStack(OUTPUT_SLOT, new ItemStack(ModItems.BREW_OF_SLIMEY_STEP,
                this.getStack(OUTPUT_SLOT).getCount() + 1));
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        this.progress++;
    }

    private boolean hasRecipe() {
        return this.getStack(INPUT_SLOTS).getItem() == ModItems.SIREN_KELP_PASTE;
    }

    private boolean hasRusticBottle() {
        return this.getStack(BOTTLE_SLOT).getItem() == ModItems.RUSTIC_BOTTLE;
    }

    public boolean isHeated() {
        if (world == null) {
            return false;
        }
        return isHeated(world, pos);
    }

    private boolean canInsertOutputSlot() {
        return this.getStack(OUTPUT_SLOT).isEmpty() ||
                this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }

}
