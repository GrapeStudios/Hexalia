package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.entity.ModBlockEntities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.astralya.hexalia.screen.SmallCauldronScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
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
    private PlayerEntity lastInteractedPlayer;
    private final int DEFAULT_MAX_PROGRESS = 175;

    // Define slot indices
    public static final int INPUT_SLOT_1 = 0;
    public static final int INPUT_SLOT_2 = 1;
    public static final int INPUT_SLOT_3 = 2;
    public static final int OUTPUT_SLOT = 6;
    public static final int BOTTLE_SLOT = 7;

    private int progress = 0;
    private int maxProgress = DEFAULT_MAX_PROGRESS;

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
        return Text.translatable("block.hexalia.small_cauldron");
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
    public boolean isValid(int slot, ItemStack stack) {
        return switch (slot) {
            case BOTTLE_SLOT -> stack.getItem() == ModItems.RUSTIC_BOTTLE;
            case OUTPUT_SLOT -> false;
            default -> stack.getItem() != ModItems.RUSTIC_BOTTLE;
        };
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("small_cauldron.progress", progress);
        nbt.putInt("small_cauldron.max_progress", maxProgress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("small_cauldron.progress");
        maxProgress = nbt.getInt("small_cauldron.max_progress");
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (isOutputSlotEmptyOrReceivable() && hasRecipe() && isHeated()) {
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

            if (lastInteractedPlayer != null && recipe.getExperience() > 0) {
                grantExperience(lastInteractedPlayer, recipe.getExperience());
            }
        });
    }

    private void grantExperience(PlayerEntity player, float experience) {
        if (experience > 0 && !player.getWorld().isClient()) {
            player.addExperience((int) experience);
        }
    }

    public void setLastInteractedPlayer(PlayerEntity player) {
        this.lastInteractedPlayer = player;
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = DEFAULT_MAX_PROGRESS;
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        this.progress++;
    }

    private boolean hasRecipe() {
        return getCurrentRecipe().map(recipe -> {
            this.maxProgress = recipe.getBrewTime();
            ItemStack output = recipe.getOutput(null);
            return canInsertAmountIntoOutputSlot(output.getCount())
                    && canInsertItemIntoOutputSlot(output.getItem())
                    && hasRequiredIngredients(recipe);
        }).orElse(false);
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

    private Optional<SmallCauldronRecipe> getCurrentRecipe() {
        SimpleInventory inventory = new SimpleInventory(this.size());
        for (int i = 0; i < this.size(); i++) {
            inventory.setStack(i, this.getStack(i));
        }
        return Objects.requireNonNull(this.getWorld()).getRecipeManager()
                .getFirstMatch(SmallCauldronRecipe.Type.INSTANCE, inventory, this.getWorld());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).isOf(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.getStack(OUTPUT_SLOT).getMaxCount() >=
                this.getStack(OUTPUT_SLOT).getCount() + count;
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.getStack(OUTPUT_SLOT).isEmpty() ||
                this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }

    public boolean isHeated() {
        return world != null && isHeated(world, pos);
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

    public static class RusticBottleSlot extends Slot {
        public RusticBottleSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() == ModItems.RUSTIC_BOTTLE;
        }

        @Override
        public int getMaxItemCount(ItemStack stack) {
            return 64;
        }
    }
}