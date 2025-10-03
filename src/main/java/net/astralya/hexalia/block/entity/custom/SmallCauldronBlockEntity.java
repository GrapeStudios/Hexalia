package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.astralya.hexalia.screen.custom.SmallCauldronScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;

public class SmallCauldronBlockEntity extends SyncBlockEntity
        implements ExtendedScreenHandlerFactory, HeatingBlock, Inventory {

    private static final int SIZE = 5;
    private static final int INPUT_SLOT_1 = 0;
    private static final int INPUT_SLOT_2 = 1;
    private static final int INPUT_SLOT_3 = 2;
    private static final int OUTPUT_SLOT   = 3;
    private static final int BOTTLE_SLOT   = 4;
    private static final int DEFAULT_MAX_PROGRESS = 175;

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);

    private int progress = 0;
    private int maxProgress = DEFAULT_MAX_PROGRESS;

    @Nullable
    private PlayerEntity lastInteractedPlayer;

    private final PropertyDelegate properties = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> maxProgress = value;
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public SmallCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SMALL_CAULDRON, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;

        if (hasRecipe() && isOutputSlotEmptyOrReceivable() && isHeated()) {
            increaseCraftingProgress();
            markDirtyAndSync();
            if (hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.hexalia.small_cauldron");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new SmallCauldronScreenHandler(syncId, inv, this, properties);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("small_cauldron.progress", progress);
        nbt.putInt("small_cauldron.max_progress", maxProgress);
        Inventories.writeNbt(nbt, items);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        progress = nbt.getInt("small_cauldron.progress");
        maxProgress = nbt.getInt("small_cauldron.max_progress");
        Collections.fill(items, ItemStack.EMPTY);
        Inventories.readNbt(nbt, items);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : items) if (!s.isEmpty()) return false;
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot >= 0 && slot < SIZE ? items.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot < 0 || slot >= SIZE || amount <= 0) return ItemStack.EMPTY;
        ItemStack current = items.get(slot);
        if (current.isEmpty()) return ItemStack.EMPTY;
        ItemStack taken = current.split(amount);
        if (current.isEmpty()) items.set(slot, ItemStack.EMPTY);
        markDirtyAndSync();
        return taken;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot < 0 || slot >= SIZE) return ItemStack.EMPTY;
        ItemStack out = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        markDirtyAndSync();
        return out;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SIZE) return;
        items.set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        if (!items.get(slot).isEmpty() && items.get(slot).getCount() > items.get(slot).getMaxCount()) {
            items.set(slot, items.get(slot).copyWithCount(items.get(slot).getMaxCount()));
        }
        markDirtyAndSync();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (world == null) return false;
        if (world.getBlockEntity(pos) != this) return false;
        return player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < SIZE; i++) items.set(i, ItemStack.EMPTY);
        markDirtyAndSync();
    }

    public boolean isHeated() {
        World w = getWorld();
        return w != null && isHeated(w, getPos());
    }

    public void setLastInteractedPlayer(PlayerEntity player) {
        this.lastInteractedPlayer = player;
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = DEFAULT_MAX_PROGRESS;
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        ItemStack out = items.get(OUTPUT_SLOT);
        return out.isEmpty() || out.getCount() < out.getMaxCount();
    }

    private boolean hasRecipe() {
        World w = getWorld();
        if (w == null) return false;

        SimpleInventory inv = new SimpleInventory(4);
        inv.setStack(0, items.get(INPUT_SLOT_1));
        inv.setStack(1, items.get(INPUT_SLOT_2));
        inv.setStack(2, items.get(INPUT_SLOT_3));
        inv.setStack(3, items.get(BOTTLE_SLOT));

        Optional<SmallCauldronRecipe> match =
                w.getRecipeManager().getFirstMatch(ModRecipes.SMALL_CAULDRON_TYPE, inv, w);

        if (!match.isPresent()) return false;

        SmallCauldronRecipe recipe = match.get();
        ItemStack result = recipe.getOutput(w.getRegistryManager());
        maxProgress = recipe.getBrewTime();

        return canInsertAmountIntoOutput(result.getCount()) && canInsertItemIntoOutput(result);
    }

    private void craftItem() {
        World w = getWorld();
        if (w == null) return;

        SimpleInventory inv = new SimpleInventory(4);
        inv.setStack(0, items.get(INPUT_SLOT_1));
        inv.setStack(1, items.get(INPUT_SLOT_2));
        inv.setStack(2, items.get(INPUT_SLOT_3));
        inv.setStack(3, items.get(BOTTLE_SLOT));

        Optional<SmallCauldronRecipe> match =
                w.getRecipeManager().getFirstMatch(ModRecipes.SMALL_CAULDRON_TYPE, inv, w);
        if (!match.isPresent()) return;

        SmallCauldronRecipe recipe = match.get();
        ItemStack result = recipe.getOutput(w.getRegistryManager());
        ItemStack current = items.get(OUTPUT_SLOT);

        items.set(OUTPUT_SLOT, new ItemStack(result.getItem(), current.getCount() + result.getCount()));

        for (int i = 0; i < 3; i++) decrementSlot(i);
        if (!recipe.getBottleSlot().isEmpty()) decrementSlot(BOTTLE_SLOT);

        if (lastInteractedPlayer != null) grantExperience(lastInteractedPlayer, recipe.getExperience());

        markDirtyAndSync();
    }

    private void grantExperience(PlayerEntity player, float experience) {
        if (experience > 0 && !player.getWorld().isClient) {
            player.addExperience((int) experience);
        }
    }

    private boolean canInsertItemIntoOutput(ItemStack output) {
        ItemStack out = items.get(OUTPUT_SLOT);
        return out.isEmpty() || out.isOf(output.getItem());
    }

    private boolean canInsertAmountIntoOutput(int count) {
        ItemStack out = items.get(OUTPUT_SLOT);
        int max = out.isEmpty() ? 64 : out.getMaxCount();
        int current = out.getCount();
        return max >= current + count;
    }

    private void decrementSlot(int slot) {
        ItemStack s = items.get(slot);
        if (s.isEmpty()) return;
        int remain = s.getCount() - 1;
        items.set(slot, remain > 0 ? s.copyWithCount(remain) : ItemStack.EMPTY);
    }

    private void markDirtyAndSync() {
        markDirty();
        World w = getWorld();
        if (w instanceof ServerWorld sw) {
            sw.getChunkManager().markForUpdate(getPos());
            w.updateListeners(getPos(), getCachedState(), getCachedState(), 3);
        }
    }
}