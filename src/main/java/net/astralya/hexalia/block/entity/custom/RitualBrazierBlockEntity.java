package net.astralya.hexalia.block.entity.custom;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.RitualBrazierRecipe;
import net.astralya.hexalia.recipe.RitualBrazierRecipeInput;
import net.astralya.hexalia.util.SunlightCheck;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class RitualBrazierBlockEntity extends BlockEntity implements SidedInventory {

    public static final int CHANNEL_DURATION = 120;

    public enum RitualResult {
        SUCCESS,
        NO_CELESTIAL_BLOOMS,
        NO_SKY,
        INVALID_ITEM,
        ALREADY_CHANNELING
    }

    private static final int SLOT = 0;
    private static final String TAG_IS_ITEM_IMBUED = "IsItemImbued";
    private static final String TAG_INVENTORY = "Inventory";
    private static final String TAG_CHAN_LEFT = "ChanLeft";
    private static final String TAG_CHAN_TOTAL = "ChanTotal";
    private static final String TAG_PENDING_OUT = "PendingOut";
    private static final String TAG_BLOOM_A = "BloomA";
    private static final String TAG_BLOOM_B = "BloomB";
    private static final String TAG_BLOOM_C = "BloomC";

    private static final int[] INPUT_OUTPUT_SLOT = new int[]{SLOT};

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private boolean isRitualFocusItem;
    private float rotation;
    private int channelTicksRemaining;
    private int channelTotalTicks;
    private ItemStack pendingOutput = ItemStack.EMPTY;
    private long bloomPosA;
    private long bloomPosB;
    private long bloomPosC;

    public RitualBrazierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.RITUAL_BRAZIER, pos, state);
    }

    public boolean isChanneling() {
        return channelTicksRemaining > 0;
    }

    public float getChannelProgress(float partialTick) {
        if (!isChanneling() || channelTotalTicks <= 0) return 0.0F;
        float remaining = channelTicksRemaining - partialTick;
        float elapsed = channelTotalTicks - remaining;
        return MathHelper.clamp(elapsed / (float) channelTotalTicks, 0.0F, 1.0F);
    }

    public RitualResult tryStartCelestialInfusion() {
        if (!(world instanceof ServerWorld server)) return RitualResult.INVALID_ITEM;
        if (isRitualFocusItem) return RitualResult.INVALID_ITEM;
        if (isChanneling()) return RitualResult.ALREADY_CHANNELING;
        ItemStack in = getStoredItem();
        if (in.isEmpty()) return RitualResult.INVALID_ITEM;
        if (!SunlightCheck.hasOpenSky(server, pos.up())) return RitualResult.NO_SKY;
        if (!captureNearbyCelestialBlooms(3)) return RitualResult.NO_CELESTIAL_BLOOMS;
        Optional<RecipeEntry<RitualBrazierRecipe>> match = server.getRecipeManager()
                .getFirstMatch(ModRecipes.RITUAL_BRAZIER_TYPE, new RitualBrazierRecipeInput(in), server);
        if (match.isEmpty()) {
            clearCapturedBlooms();
            return RitualResult.INVALID_ITEM;
        }
        ItemStack out = match.get().value().getResult(server.getRegistryManager());
        if (out.isEmpty()) {
            clearCapturedBlooms();
            return RitualResult.INVALID_ITEM;
        }
        pendingOutput = out.copy();
        channelTotalTicks = CHANNEL_DURATION;
        channelTicksRemaining = CHANNEL_DURATION;
        sync(server, pos);
        return RitualResult.SUCCESS;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, RitualBrazierBlockEntity be) {
        if (!(world instanceof ServerWorld server)) return;
        if (!be.isChanneling()) return;
        if (be.isEmpty() || be.pendingOutput.isEmpty()) {
            be.cancelChannel(server, pos);
            return;
        }
        if (!SunlightCheck.canSeeSun(server, pos.up())) {
            be.cancelChannel(server, pos);
            return;
        }
        BlockPos a = BlockPos.fromLong(be.bloomPosA);
        BlockPos b = BlockPos.fromLong(be.bloomPosB);
        BlockPos c = BlockPos.fromLong(be.bloomPosC);
        if (!be.isValidBloomPos(a) || !be.isValidBloomPos(b) || !be.isValidBloomPos(c)) {
            be.cancelChannel(server, pos);
            return;
        }
        be.emitChannelParticles(server, pos, a, b, c);
        be.channelTicksRemaining--;
        if (be.channelTicksRemaining <= 0) {
            be.completeChannel(server, pos);
            return;
        }
        be.markDirty();
        server.updateListeners(pos, state, state, 3);
    }

    private boolean isValidBloomPos(BlockPos checkPos) {
        if (world == null) return false;
        BlockState bs = world.getBlockState(checkPos);
        return bs.isOf(ModBlocks.CELESTIAL_BLOOM) || bs.isOf(ModBlocks.WITHERED_CELESTIAL_BLOOM);
    }

    private void cancelChannel(ServerWorld world, BlockPos pos) {
        channelTicksRemaining = 0;
        channelTotalTicks = 0;
        pendingOutput = ItemStack.EMPTY;
        clearCapturedBlooms();
        world.playSound(null, pos, SoundEvents.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 0.35F, 0.7F);
        sync(world, pos);
    }

    private void completeChannel(ServerWorld world, BlockPos pos) {
        ItemStack resultStack = pendingOutput.copyWithCount(1);
        pendingOutput = ItemStack.EMPTY;
        BlockPos a = BlockPos.fromLong(bloomPosA);
        BlockPos b = BlockPos.fromLong(bloomPosB);
        BlockPos c = BlockPos.fromLong(bloomPosC);
        clearCapturedBlooms();
        inventory.set(SLOT, resultStack);
        onInventoryChanged();
        degradeCelestialBloom(world, a);
        degradeCelestialBloom(world, b);
        degradeCelestialBloom(world, c);
        world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.45F, 1.15F);
        world.spawnParticles(ModParticleType.SPARKLE, pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 18, 0.25, 0.2, 0.25, 0.0);
        channelTicksRemaining = 0;
        channelTotalTicks = 0;
        sync(world, pos);
    }

    private void emitChannelParticles(ServerWorld server, BlockPos brazierPos, BlockPos a, BlockPos b, BlockPos c) {
        double bx = brazierPos.getX() + 0.5;
        double by = brazierPos.getY() + 0.85;
        double bz = brazierPos.getZ() + 0.5;
        for (int i = 0; i < 2; i++) {
            double ox = (server.random.nextDouble() - 0.5) * 0.35;
            double oz = (server.random.nextDouble() - 0.5) * 0.35;
            double oy = server.random.nextDouble() * 0.20;
            server.spawnParticles(ModParticleType.SPARKLE, bx + ox, by + oy, bz + oz, 1, 0, 0, 0, 0.0);
        }
        emitBloomSparkles(server, a);
        emitBloomSparkles(server, b);
        emitBloomSparkles(server, c);
    }

    private void emitBloomSparkles(ServerWorld server, BlockPos bloomPos) {
        double x = bloomPos.getX() + 0.5 + (server.random.nextDouble() - 0.5) * 0.25;
        double y = bloomPos.getY() + 0.55 + server.random.nextDouble() * 0.35;
        double z = bloomPos.getZ() + 0.5 + (server.random.nextDouble() - 0.5) * 0.25;
        server.spawnParticles(ModParticleType.SPARKLE, x, y, z, 1, 0, 0, 0, 0.0);
    }

    private void degradeCelestialBloom(ServerWorld world, BlockPos bloomPos) {
        BlockState bs = world.getBlockState(bloomPos);
        BlockState next = null;
        if (bs.isOf(ModBlocks.CELESTIAL_BLOOM)) {
            next = ModBlocks.WITHERED_CELESTIAL_BLOOM.getDefaultState();
        } else if (bs.isOf(ModBlocks.WITHERED_CELESTIAL_BLOOM)) {
            next = Blocks.DEAD_BUSH.getDefaultState();
        }
        if (next == null) return;
        world.setBlockState(bloomPos, next, 3);
        world.spawnParticles(ModParticleType.LEAVES, bloomPos.getX() + 0.5, bloomPos.getY() + 0.6, bloomPos.getZ() + 0.5, 14, 0.2, 0.25, 0.2, 0.0);
    }

    private boolean captureNearbyCelestialBlooms(int radius) {
        if (world == null) return false;
        clearCapturedBlooms();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx == 0 && dz == 0) continue;
                BlockPos check = pos.add(dx, 0, dz);
                BlockState bs = world.getBlockState(check);
                if (!bs.isOf(ModBlocks.CELESTIAL_BLOOM) && !bs.isOf(ModBlocks.WITHERED_CELESTIAL_BLOOM)) continue;
                if (bloomPosA == 0L) {
                    bloomPosA = check.asLong();
                } else if (bloomPosB == 0L) {
                    bloomPosB = check.asLong();
                } else if (bloomPosC == 0L) {
                    bloomPosC = check.asLong();
                    return true;
                }
            }
        }
        clearCapturedBlooms();
        return false;
    }

    private void clearCapturedBlooms() {
        bloomPosA = 0L;
        bloomPosB = 0L;
        bloomPosC = 0L;
    }

    public boolean addItem(ItemStack itemStack) {
        if (isChanneling()) return false;
        if (isEmpty() && !itemStack.isEmpty()) {
            inventory.set(SLOT, itemStack.split(1));
            isRitualFocusItem = false;
            onInventoryChanged();
            return true;
        }
        return false;
    }

    public ItemStack removeItem() {
        if (isChanneling()) return ItemStack.EMPTY;
        if (!isEmpty()) {
            isRitualFocusItem = false;
            ItemStack item = getStoredItem().split(1);
            onInventoryChanged();
            return item;
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getStoredItem() {
        return inventory.get(SLOT);
    }

    public float getRenderingRotation() {
        if (world == null || !world.isClient) return rotation;
        rotation += isChanneling() ? 1.5f : 0.5f;
        if (rotation >= 360.0f) rotation = 0.0f;
        return rotation;
    }

    private void onInventoryChanged() {
        markDirty();
        if (world instanceof ServerWorld server) {
            server.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    private void sync(ServerWorld world, BlockPos pos) {
        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean(TAG_IS_ITEM_IMBUED, isRitualFocusItem);
        writeInventory(nbt, registries);
        nbt.putInt(TAG_CHAN_LEFT, channelTicksRemaining);
        nbt.putInt(TAG_CHAN_TOTAL, channelTotalTicks);
        if (!pendingOutput.isEmpty()) {
            nbt.put(TAG_PENDING_OUT, (NbtCompound) pendingOutput.encode(registries));
        }
        nbt.putLong(TAG_BLOOM_A, bloomPosA);
        nbt.putLong(TAG_BLOOM_B, bloomPosB);
        nbt.putLong(TAG_BLOOM_C, bloomPosC);
        return nbt;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        isRitualFocusItem = nbt.getBoolean(TAG_IS_ITEM_IMBUED);
        readInventory(nbt, registries);
        channelTicksRemaining = nbt.getInt(TAG_CHAN_LEFT);
        channelTotalTicks = nbt.getInt(TAG_CHAN_TOTAL);
        pendingOutput = nbt.contains(TAG_PENDING_OUT, NbtCompound.COMPOUND_TYPE)
                ? ItemStack.fromNbt(registries, nbt.getCompound(TAG_PENDING_OUT)).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
        bloomPosA = nbt.getLong(TAG_BLOOM_A);
        bloomPosB = nbt.getLong(TAG_BLOOM_B);
        bloomPosC = nbt.getLong(TAG_BLOOM_C);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putBoolean(TAG_IS_ITEM_IMBUED, isRitualFocusItem);
        writeInventory(nbt, registries);
        nbt.putInt(TAG_CHAN_LEFT, channelTicksRemaining);
        nbt.putInt(TAG_CHAN_TOTAL, channelTotalTicks);
        if (!pendingOutput.isEmpty()) {
            nbt.put(TAG_PENDING_OUT, (NbtCompound) pendingOutput.encode(registries));
        }
        nbt.putLong(TAG_BLOOM_A, bloomPosA);
        nbt.putLong(TAG_BLOOM_B, bloomPosB);
        nbt.putLong(TAG_BLOOM_C, bloomPosC);
    }

    private void writeInventory(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        ItemStack stack = inventory.get(SLOT);
        NbtCompound invTag = new NbtCompound();
        if (!stack.isEmpty()) {
            invTag.put("Slot0", (NbtCompound) stack.encode(registries));
        }
        nbt.put(TAG_INVENTORY, invTag);
    }

    private void readInventory(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        inventory.set(SLOT, ItemStack.EMPTY);
        NbtCompound invTag = nbt.getCompound(TAG_INVENTORY);
        if (invTag.contains("Slot0", NbtCompound.COMPOUND_TYPE)) {
            inventory.set(SLOT, ItemStack.fromNbt(registries, invTag.getCompound("Slot0")).orElse(ItemStack.EMPTY));
        }
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getStoredItem().isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot == SLOT ? inventory.get(SLOT) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot != SLOT || isChanneling()) {
            return ItemStack.EMPTY;
        }

        ItemStack removed = Inventories.splitStack(inventory, slot, amount);
        if (!removed.isEmpty()) {
            isRitualFocusItem = false;
            onInventoryChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot != SLOT || isChanneling()) {
            return ItemStack.EMPTY;
        }

        ItemStack removed = Inventories.removeStack(inventory, slot);
        if (!removed.isEmpty()) {
            isRitualFocusItem = false;
            onInventoryChanged();
        }
        return removed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot != SLOT || isChanneling()) {
            return;
        }

        if (stack.isEmpty()) {
            inventory.set(SLOT, ItemStack.EMPTY);
        } else {
            ItemStack one = stack.copy();
            one.setCount(1);
            inventory.set(SLOT, one);
        }

        isRitualFocusItem = false;
        onInventoryChanged();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return world != null
                && world.getBlockEntity(pos) == this
                && player.squaredDistanceTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clear() {
        inventory.set(SLOT, ItemStack.EMPTY);
        isRitualFocusItem = false;
        onInventoryChanged();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return INPUT_OUTPUT_SLOT;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (slot != SLOT) {
            return false;
        }
        if (isChanneling()) {
            return false;
        }
        if (stack.isEmpty()) {
            return false;
        }
        return inventory.get(SLOT).isEmpty();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (slot != SLOT) {
            return false;
        }
        if (isChanneling()) {
            return false;
        }
        return !stack.isEmpty();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot != SLOT) {
            return false;
        }
        if (isChanneling()) {
            return false;
        }
        if (stack.isEmpty()) {
            return false;
        }
        return inventory.get(SLOT).isEmpty();
    }
}