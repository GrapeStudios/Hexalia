package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.astralya.hexalia.menu.SmallCauldronMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SmallCauldronBlockEntity extends SyncBlockEntity implements MenuProvider, HeatingBlock {

    public final ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private static final int INPUT_SLOT_1 = 0;
    private static final int INPUT_SLOT_2 = 1;
    private static final int INPUT_SLOT_3 = 2;
    private static final int OUTPUT_SLOT = 3;
    private static final int BOTTLE_SLOT = 4;

    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = 175;
    private final int DEFAULT_MAX_PROGRESS = 175;
    @Nullable private Player lastInteractedPlayer;

    public SmallCauldronBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.SMALL_CAULDRON.get(), pos, blockState);
        this.data = new ContainerData() {
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
            public int getCount() {
                return 2;
            }
        };
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public void tick (Level level, BlockPos pos, BlockState state) {
        if(hasRecipe() && isOutputSlotEmptyOrReceivable() && isHeated()) {
            increaseCraftingProgress();
            setChanged(level, pos, state);

            if (hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = DEFAULT_MAX_PROGRESS;
    }

    private void craftItem() {
        Level level = this.getLevel();
        if (level == null) return;

        RecipeWrapper wrapper = new RecipeWrapper(inventory);

        Optional<RecipeHolder<SmallCauldronRecipe>> match = level.getRecipeManager()
                .getRecipeFor(ModRecipes.SMALL_CAULDRON_TYPE.get(), wrapper, level);

        if (match.isPresent()) {
            SmallCauldronRecipe recipe = match.get().value();
            ItemStack result = recipe.getResultItem(level.registryAccess());
            ItemStack currentOutput = inventory.getStackInSlot(OUTPUT_SLOT);

            inventory.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                    currentOutput.getCount() + result.getCount()));

            for (int i = 0; i < 3; i++) {
                inventory.extractItem(i, 1, false);
            }

            if (!recipe.getBottleSlot().isEmpty()) {
                inventory.extractItem(BOTTLE_SLOT, 1, false);
            }

            if (lastInteractedPlayer != null) {
                grantExperience(lastInteractedPlayer, recipe.getExperience());
            }

            setChanged(level, worldPosition, getBlockState());
        }
    }

    private void grantExperience(Player player, float experience) {
        if (experience > 0 && !player.level().isClientSide) {
            player.giveExperiencePoints((int) experience);
        }
    }

    public void setLastInteractedPlayer(Player player) {
        this.lastInteractedPlayer = player;
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.inventory.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                this.inventory.getStackInSlot(OUTPUT_SLOT).getCount() < this.inventory.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean hasRecipe() {
        Level level = this.getLevel();
        if (level == null) return false;

        RecipeWrapper wrapper = new RecipeWrapper(inventory);
        Optional<RecipeHolder<SmallCauldronRecipe>> match = level.getRecipeManager()
                .getRecipeFor(ModRecipes.SMALL_CAULDRON_TYPE.get(), wrapper, level);

        if (match.isEmpty()) return false;

        SmallCauldronRecipe recipe = match.get().value();
        ItemStack result = recipe.getResultItem(level.registryAccess());

        this.maxProgress = recipe.getBrewTime();

        return canInsertAmountIntoOutputSlot(result.getCount()) &&
                canInsertItemIntoOutputSlot(result);
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return inventory.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                inventory.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = inventory.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : inventory.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = inventory.getStackInSlot(OUTPUT_SLOT).getCount();

        return maxCount >= currentCount + count;
    }

    public boolean isHeated() {
        return level != null && isHeated(level, worldPosition);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.hexalia.small_cauldron");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new SmallCauldronMenu(i, inventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("small_cauldron.progress", progress);
        tag.putInt("small_cauldron.max_progress", maxProgress);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("small_cauldron.progress");
        maxProgress = tag.getInt("small_cauldron.max_progress");
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
