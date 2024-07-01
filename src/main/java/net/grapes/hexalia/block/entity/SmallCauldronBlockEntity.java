package net.grapes.hexalia.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.recipe.SmallCauldronRecipe;
import net.grapes.hexalia.screen.SmallCauldronScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class SmallCauldronBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, HeatingBlockEntity {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(8, ItemStack.EMPTY);

    public static final int INPUT_SLOT_1 = 0;
    public static final int INPUT_SLOT_2 = 1;
    public static final int INPUT_SLOT_3 = 2;
    public static final int OUTPUT_SLOT = 6;
    public static final int BOTTLE_SLOT = 7;

    private int progress = 0;
    private int maxProgress = 72;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
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
                case 0 -> SmallCauldronBlockEntity.this.progress = value;
                case 1 -> SmallCauldronBlockEntity.this.maxProgress = value;
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public SmallCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMALL_CAULDRON_BE, pos, state);
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
        if (isHeated() && canInsertOutputSlot() && hasRecipe()) {
            increaseCraftingProgress();
            markDirty(world, pos, state);
            if (hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void craftItem() {
        getCurrentRecipe().ifPresent(recipe -> {
            removeStack(INPUT_SLOT_1, 1);
            removeStack(INPUT_SLOT_2, 1);
            removeStack(INPUT_SLOT_3, 1);
            removeStack(BOTTLE_SLOT, 1);

            ItemStack output = recipe.getOutput(null);
            setStack(OUTPUT_SLOT, new ItemStack(output.getItem(), getStack(OUTPUT_SLOT).getCount() + output.getCount()));
        });
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
        return getCurrentRecipe().map(recipe -> {
            ItemStack output = recipe.getOutput(null);
            return canInsertAmountIntoOutputSlot(output.getCount())
                    && canInsertItemIntoOutputSlot(output)
                    && hasRequiredIngredients(recipe);
        }).orElse(false);
    }


    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.getStack(OUTPUT_SLOT).getMaxCount() >= this.getStack(OUTPUT_SLOT).getCount() + count;
    }

    private Optional<SmallCauldronRecipe> getCurrentRecipe() {
        SimpleInventory inventory = new SimpleInventory(this.size());
        for (int i = 0; i < this.size(); i++) {
            inventory.setStack(i, this.getStack(i));
        }
        return Objects.requireNonNull(this.getWorld()).getRecipeManager().getFirstMatch(SmallCauldronRecipe.Type.INSTANCE, inventory, this.getWorld());
    }

    private boolean hasRequiredIngredients(SmallCauldronRecipe recipe) {
        boolean hasRusticBottle = recipe.getBottleSlot().test(this.getStack(BOTTLE_SLOT));
        if (!hasRusticBottle) {
            return false;
        }

        for (Ingredient ingredient : recipe.getIngredients()) {
            boolean foundIngredient = false;
            for (int i = INPUT_SLOT_1; i <= INPUT_SLOT_3; i++) {
                if (ingredient.test(this.getStack(i))) {
                    foundIngredient = true;
                    break;
                }
            }
            if (!foundIngredient) {
                return false;
            }
        }
        return true;
    }

    public boolean isHeated() {
        return world != null && isHeated(world, pos);
    }

    private boolean canInsertOutputSlot() {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }

    public static class RusticBottleSlot extends Slot {
        public RusticBottleSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public int getMaxItemCount(ItemStack stack) {
            return 64;
        }
    }

    public static class IngredientSlot extends Slot {
        public IngredientSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() != ModItems.RUSTIC_BOTTLE;
        }
    }
}
