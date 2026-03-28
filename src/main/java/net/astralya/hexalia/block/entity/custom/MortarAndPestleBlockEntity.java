package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.MortarAndPestleRecipe;
import net.astralya.hexalia.recipe.MortarAndPestleRecipeInput;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class MortarAndPestleBlockEntity extends BlockEntity implements SidedInventory {

    public static final int SPIN_TICKS = 20;
    public static final int REQUIRED_SPINS = 3;
    public static final int INPUT_0 = 0;
    public static final int INPUT_1 = 1;
    public static final int INPUT_2 = 2;
    public static final int OUTPUT = 3;
    private static final int SIZE = 4;

    private static final int[] INPUT_SLOTS = new int[]{INPUT_0, INPUT_1, INPUT_2};
    private static final int[] OUTPUT_SLOTS = new int[]{OUTPUT};

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);
    private ItemStack pendingResult = ItemStack.EMPTY;
    private int pestleTick;
    private int pestleCount;
    private boolean pestling;

    public MortarAndPestleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.MORTAR_AND_PESTLE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MortarAndPestleBlockEntity be) {
        if (!world.isClient) {
            be.recomputePestlingState();
            if (world.isReceivingRedstonePower(pos) && be.canStartSpin()) {
                be.startSpin();
            }
        }

        if (be.pestleTick > 0) {
            be.pestleTick--;
            be.markDirty();
            if (!world.isClient && be.pestleTick == 0) {
                be.tryFinishOnSpinEnd();
                be.onInventoryChanged();
            }
        }
    }

    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public boolean hasAnyInputs() {
        return !items.get(INPUT_0).isEmpty()
                || !items.get(INPUT_1).isEmpty()
                || !items.get(INPUT_2).isEmpty();
    }

    public boolean hasOutput() {
        return !items.get(OUTPUT).isEmpty();
    }

    public int getPestleTick() {
        return pestleTick;
    }

    public boolean canInsertOne(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (hasOutput()) return false;
        return items.get(INPUT_0).isEmpty()
                || items.get(INPUT_1).isEmpty()
                || items.get(INPUT_2).isEmpty();
    }

    public boolean insertOneIntoNextEmpty(ItemStack held) {
        if (!canInsertOne(held)) return false;
        ItemStack one = held.copy();
        one.setCount(1);
        for (int slot = INPUT_0; slot <= INPUT_2; slot++) {
            if (items.get(slot).isEmpty()) {
                items.set(slot, one);
                onInventoryChanged();
                return true;
            }
        }
        return false;
    }

    public ItemStack extractOneInput() {
        for (int slot = INPUT_2; slot >= INPUT_0; slot--) {
            ItemStack s = items.get(slot);
            if (!s.isEmpty()) {
                ItemStack out = s.copy();
                out.setCount(1);
                items.set(slot, ItemStack.EMPTY);
                if (world != null && !world.isClient) {
                    recomputePestlingState();
                    onInventoryChanged();
                } else {
                    markDirty();
                }
                return out;
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack takeOutputOne() {
        ItemStack out = items.get(OUTPUT);
        if (out.isEmpty()) return ItemStack.EMPTY;
        ItemStack give = out.copy();
        give.setCount(1);
        out.decrement(1);
        items.set(OUTPUT, out.isEmpty() ? ItemStack.EMPTY : out);
        if (world != null && !world.isClient) onInventoryChanged();
        else markDirty();
        return give;
    }

    public void recomputePestlingState() {
        if (world == null || world.isClient) return;
        if (hasOutput() || !hasAnyInputs()) {
            pendingResult = ItemStack.EMPTY;
            pestling = false;
            pestleTick = 0;
            pestleCount = 0;
            return;
        }
        ItemStack newResult = computeRecipeResult();
        boolean valid = !newResult.isEmpty();
        if (!ItemStack.areItemsAndComponentsEqual(pendingResult, newResult) || pendingResult.getCount() != newResult.getCount()) {
            pestleTick = 0;
            pestleCount = 0;
        }
        pendingResult = newResult;
        pestling = valid;
        if (!valid) {
            pestleTick = 0;
            pestleCount = 0;
        }
    }

    private ItemStack computeRecipeResult() {
        if (world == null) return ItemStack.EMPTY;
        MortarAndPestleRecipeInput input = new MortarAndPestleRecipeInput(
                items.get(INPUT_0),
                items.get(INPUT_1),
                items.get(INPUT_2)
        );
        Optional<RecipeEntry<MortarAndPestleRecipe>> match =
                world.getRecipeManager().getFirstMatch(ModRecipes.MORTAR_AND_PESTLE_TYPE, input, world);
        return match.map(entry -> entry.value().getResult(world.getRegistryManager()).copy()).orElse(ItemStack.EMPTY);
    }

    public boolean canStartSpin() {
        return !hasOutput() && !pendingResult.isEmpty() && pestleTick <= 0 && pestleCount < REQUIRED_SPINS;
    }

    public boolean startSpin() {
        if (world == null) return false;
        if (!world.isClient) {
            recomputePestlingState();
        }
        if (!canStartSpin()) return false;
        pestling = true;
        pestleTick = SPIN_TICKS;
        pestleCount++;
        if (world instanceof ServerWorld server) {
            spawnCrushParticles(server);
        }
        if (!world.isClient) onInventoryChanged();
        else markDirty();
        return true;
    }

    private void tryFinishOnSpinEnd() {
        if (world == null || world.isClient) return;
        if (!pestling) return;
        if (pestleCount < REQUIRED_SPINS) return;
        if (pendingResult.isEmpty()) return;
        if (hasOutput()) return;
        items.set(OUTPUT, pendingResult.copy());
        items.set(INPUT_0, ItemStack.EMPTY);
        items.set(INPUT_1, ItemStack.EMPTY);
        items.set(INPUT_2, ItemStack.EMPTY);
        pendingResult = ItemStack.EMPTY;
        pestling = false;
        pestleTick = 0;
        pestleCount = 0;
    }

    private void spawnCrushParticles(ServerWorld server) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.20;
        double z = pos.getZ() + 0.5;
        for (int slot = INPUT_0; slot <= INPUT_2; slot++) {
            ItemStack stack = items.get(slot);
            if (stack.isEmpty()) continue;
            ItemStackParticleEffect particle = new ItemStackParticleEffect(ParticleTypes.ITEM, stack);
            for (int i = 0; i < 3; i++) {
                double ox = (server.random.nextDouble() - 0.5) * 0.12;
                double oy = server.random.nextDouble() * 0.06;
                double oz = (server.random.nextDouble() - 0.5) * 0.12;
                double vx = (server.random.nextDouble() - 0.5) * 0.03;
                double vy = 0.02 + server.random.nextDouble() * 0.02;
                double vz = (server.random.nextDouble() - 0.5) * 0.03;
                server.spawnParticles(particle, x + ox, y + oy, z + oz, 1, vx, vy, vz, 0.0);
            }
        }
    }

    public void drops() {
        if (world == null) return;
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copy());
                world.spawnEntity(entity);
            }
        }
        for (int i = 0; i < SIZE; i++) {
            items.set(i, ItemStack.EMPTY);
        }
    }

    private void onInventoryChanged() {
        if (world != null && !world.isClient) {
            recomputePestlingState();
        }
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        NbtCompound itemsTag = new NbtCompound();
        for (int i = 0; i < SIZE; i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) continue;
            itemsTag.put("Slot" + i, (NbtCompound) stack.encode(registries));
        }
        nbt.put("Items", itemsTag);

        nbt.putInt("PestleTick", pestleTick);
        nbt.putInt("PestleCount", pestleCount);
        nbt.putBoolean("Pestling", pestling);

        if (!pendingResult.isEmpty()) {
            nbt.put("PendingResult", (NbtCompound) pendingResult.encode(registries));
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        for (int i = 0; i < SIZE; i++) {
            items.set(i, ItemStack.EMPTY);
        }

        if (nbt.contains("Items", NbtElement.COMPOUND_TYPE)) {
            NbtCompound itemsTag = nbt.getCompound("Items");
            for (int i = 0; i < SIZE; i++) {
                String key = "Slot" + i;
                if (!itemsTag.contains(key, NbtElement.COMPOUND_TYPE)) continue;

                NbtCompound stackTag = itemsTag.getCompound(key);
                if (stackTag.isEmpty()) continue;

                items.set(i, ItemStack.fromNbt(registries, stackTag).orElse(ItemStack.EMPTY));
            }
        }

        pestleTick = nbt.getInt("PestleTick");
        pestleCount = nbt.getInt("PestleCount");
        pestling = nbt.getBoolean("Pestling");

        if (nbt.contains("PendingResult", NbtElement.COMPOUND_TYPE)) {
            NbtCompound resultTag = nbt.getCompound("PendingResult");
            pendingResult = resultTag.isEmpty() ? ItemStack.EMPTY : ItemStack.fromNbt(registries, resultTag).orElse(ItemStack.EMPTY);
        } else {
            pendingResult = ItemStack.EMPTY;
        }
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) {
            return INPUT_SLOTS;
        }
        if (side == Direction.DOWN) {
            return OUTPUT_SLOTS;
        }
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir != Direction.UP) {
            return false;
        }
        if (slot < INPUT_0 || slot > INPUT_2) {
            return false;
        }
        if (stack.isEmpty()) {
            return false;
        }
        if (hasOutput()) {
            return false;
        }
        return items.get(slot).isEmpty();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot < INPUT_0 || slot > INPUT_2) {
            return false;
        }
        if (stack.isEmpty()) {
            return false;
        }
        if (hasOutput()) {
            return false;
        }
        return items.get(slot).isEmpty();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot == OUTPUT && !stack.isEmpty();
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot >= 0 && slot < SIZE ? items.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack removed = Inventories.splitStack(items, slot, amount);
        if (!removed.isEmpty()) {
            if (world != null && !world.isClient) {
                recomputePestlingState();
                onInventoryChanged();
            } else {
                markDirty();
            }
        }
        return removed;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack removed = Inventories.removeStack(items, slot);
        if (!removed.isEmpty()) {
            if (world != null && !world.isClient) {
                recomputePestlingState();
                onInventoryChanged();
            } else {
                markDirty();
            }
        }
        return removed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SIZE) {
            return;
        }

        if (slot <= INPUT_2) {
            ItemStack one = stack.copy();
            if (!one.isEmpty()) {
                one.setCount(1);
            }
            items.set(slot, one);
        } else {
            items.set(slot, stack);
        }

        if (world != null && !world.isClient) {
            recomputePestlingState();
            onInventoryChanged();
        } else {
            markDirty();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return world != null
                && world.getBlockEntity(pos) == this
                && player.squaredDistanceTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clear() {
        for (int i = 0; i < SIZE; i++) {
            items.set(i, ItemStack.EMPTY);
        }
        if (world != null && !world.isClient) {
            recomputePestlingState();
            onInventoryChanged();
        } else {
            markDirty();
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt, registries);
        return nbt;
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }
}