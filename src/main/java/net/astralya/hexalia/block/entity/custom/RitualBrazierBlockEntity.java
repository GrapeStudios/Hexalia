package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.RitualBrazierRecipe;
import net.astralya.hexalia.util.SunlightCheck;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
        if (!isChanneling() || channelTotalTicks <= 0) {
            return 0.0F;
        }

        float remaining = channelTicksRemaining - partialTick;
        float elapsed = channelTotalTicks - remaining;
        return MathHelper.clamp(elapsed / (float) channelTotalTicks, 0.0F, 1.0F);
    }

    public RitualResult tryStartCelestialInfusion() {
        if (!(world instanceof ServerWorld serverWorld)) {
            return RitualResult.INVALID_ITEM;
        }

        if (isRitualFocusItem) {
            return RitualResult.INVALID_ITEM;
        }

        if (isChanneling()) {
            return RitualResult.ALREADY_CHANNELING;
        }

        ItemStack input = getStoredItem();
        if (input.isEmpty()) {
            return RitualResult.INVALID_ITEM;
        }

        if (!SunlightCheck.hasOpenSky(serverWorld, pos.up())) {
            return RitualResult.NO_SKY;
        }

        if (!captureNearbyCelestialBlooms(3)) {
            return RitualResult.NO_CELESTIAL_BLOOMS;
        }

        Optional<RitualBrazierRecipe> match = serverWorld.getRecipeManager().getFirstMatch(
                ModRecipes.RITUAL_BRAZIER_TYPE,
                new SimpleInventory(input),
                serverWorld
        );

        if (match.isEmpty()) {
            clearCapturedBlooms();
            return RitualResult.INVALID_ITEM;
        }

        ItemStack output = match.get().getOutput(serverWorld.getRegistryManager()).copy();
        if (output.isEmpty()) {
            clearCapturedBlooms();
            return RitualResult.INVALID_ITEM;
        }

        pendingOutput = output;
        channelTotalTicks = CHANNEL_DURATION;
        channelTicksRemaining = CHANNEL_DURATION;
        sync(serverWorld);
        return RitualResult.SUCCESS;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, RitualBrazierBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        if (!blockEntity.isChanneling()) {
            return;
        }

        if (blockEntity.isEmpty() || blockEntity.pendingOutput.isEmpty()) {
            blockEntity.cancelChannel(serverWorld, pos);
            return;
        }

        if (!SunlightCheck.canSeeSun(serverWorld, pos.up())) {
            blockEntity.cancelChannel(serverWorld, pos);
            return;
        }

        BlockPos bloomA = BlockPos.fromLong(blockEntity.bloomPosA);
        BlockPos bloomB = BlockPos.fromLong(blockEntity.bloomPosB);
        BlockPos bloomC = BlockPos.fromLong(blockEntity.bloomPosC);

        if (!blockEntity.isValidBloomPos(bloomA) || !blockEntity.isValidBloomPos(bloomB) || !blockEntity.isValidBloomPos(bloomC)) {
            blockEntity.cancelChannel(serverWorld, pos);
            return;
        }

        blockEntity.emitChannelParticles(serverWorld, pos, bloomA, bloomB, bloomC);
        blockEntity.channelTicksRemaining--;

        if (blockEntity.channelTicksRemaining <= 0) {
            blockEntity.completeChannel(serverWorld, pos);
            return;
        }

        blockEntity.markDirty();
        serverWorld.updateListeners(pos, state, state, 3);
    }

    private boolean isValidBloomPos(BlockPos checkPos) {
        if (world == null) {
            return false;
        }

        BlockState state = world.getBlockState(checkPos);
        return state.isOf(ModBlocks.CELESTIAL_BLOOM) || state.isOf(ModBlocks.WITHERED_CELESTIAL_BLOOM);
    }

    private void cancelChannel(ServerWorld world, BlockPos pos) {
        channelTicksRemaining = 0;
        channelTotalTicks = 0;
        pendingOutput = ItemStack.EMPTY;
        clearCapturedBlooms();
        world.playSound(null, pos, SoundEvents.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 0.35F, 0.7F);
        sync(world);
    }

    private void completeChannel(ServerWorld world, BlockPos pos) {
        ItemStack resultStack = pendingOutput.copy();
        resultStack.setCount(1);
        pendingOutput = ItemStack.EMPTY;

        BlockPos bloomA = BlockPos.fromLong(bloomPosA);
        BlockPos bloomB = BlockPos.fromLong(bloomPosB);
        BlockPos bloomC = BlockPos.fromLong(bloomPosC);

        clearCapturedBlooms();
        inventory.set(SLOT, resultStack);
        onInventoryChanged();

        degradeCelestialBloom(world, bloomA);
        degradeCelestialBloom(world, bloomB);
        degradeCelestialBloom(world, bloomC);

        world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.45F, 1.15F);
        world.spawnParticles(ModParticleType.SPARKLE, pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 18, 0.25, 0.2, 0.25, 0.0);

        channelTicksRemaining = 0;
        channelTotalTicks = 0;
        sync(world);
    }

    private void emitChannelParticles(ServerWorld serverWorld, BlockPos brazierPos, BlockPos bloomA, BlockPos bloomB, BlockPos bloomC) {
        double centerX = brazierPos.getX() + 0.5;
        double centerY = brazierPos.getY() + 0.85;
        double centerZ = brazierPos.getZ() + 0.5;

        for (int i = 0; i < 2; i++) {
            double offsetX = (serverWorld.random.nextDouble() - 0.5) * 0.35;
            double offsetZ = (serverWorld.random.nextDouble() - 0.5) * 0.35;
            double offsetY = serverWorld.random.nextDouble() * 0.20;
            serverWorld.spawnParticles(ModParticleType.SPARKLE, centerX + offsetX, centerY + offsetY, centerZ + offsetZ, 1, 0.0, 0.0, 0.0, 0.0);
        }

        emitBloomSparkles(serverWorld, bloomA);
        emitBloomSparkles(serverWorld, bloomB);
        emitBloomSparkles(serverWorld, bloomC);
    }

    private void emitBloomSparkles(ServerWorld serverWorld, BlockPos bloomPos) {
        double x = bloomPos.getX() + 0.5 + (serverWorld.random.nextDouble() - 0.5) * 0.25;
        double y = bloomPos.getY() + 0.55 + serverWorld.random.nextDouble() * 0.35;
        double z = bloomPos.getZ() + 0.5 + (serverWorld.random.nextDouble() - 0.5) * 0.25;
        serverWorld.spawnParticles(ModParticleType.SPARKLE, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
    }

    private void degradeCelestialBloom(ServerWorld world, BlockPos bloomPos) {
        BlockState state = world.getBlockState(bloomPos);
        BlockState nextState = null;

        if (state.isOf(ModBlocks.CELESTIAL_BLOOM)) {
            nextState = ModBlocks.WITHERED_CELESTIAL_BLOOM.getDefaultState();
        } else if (state.isOf(ModBlocks.WITHERED_CELESTIAL_BLOOM)) {
            nextState = Blocks.DEAD_BUSH.getDefaultState();
        }

        if (nextState == null) {
            return;
        }

        world.setBlockState(bloomPos, nextState, 3);
        world.spawnParticles(ModParticleType.LEAVES, bloomPos.getX() + 0.5, bloomPos.getY() + 0.6, bloomPos.getZ() + 0.5, 14, 0.2, 0.25, 0.2, 0.0);
    }

    private boolean captureNearbyCelestialBlooms(int radius) {
        if (world == null) {
            return false;
        }

        clearCapturedBlooms();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                BlockPos checkPos = pos.add(dx, 0, dz);
                BlockState state = world.getBlockState(checkPos);
                if (!state.isOf(ModBlocks.CELESTIAL_BLOOM) && !state.isOf(ModBlocks.WITHERED_CELESTIAL_BLOOM)) {
                    continue;
                }

                if (bloomPosA == 0L) {
                    bloomPosA = checkPos.asLong();
                } else if (bloomPosB == 0L) {
                    bloomPosB = checkPos.asLong();
                } else if (bloomPosC == 0L) {
                    bloomPosC = checkPos.asLong();
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
        if (isChanneling()) {
            return false;
        }

        if (isEmpty() && !itemStack.isEmpty()) {
            inventory.set(SLOT, itemStack.split(1));
            isRitualFocusItem = false;
            onInventoryChanged();
            return true;
        }

        return false;
    }

    public ItemStack removeItem() {
        if (isChanneling()) {
            return ItemStack.EMPTY;
        }

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
        if (world == null || !world.isClient) {
            return rotation;
        }

        rotation += isChanneling() ? 1.5F : 0.5F;
        if (rotation >= 360.0F) {
            rotation = 0.0F;
        }
        return rotation;
    }

    private void onInventoryChanged() {
        markDirty();
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    private void sync(ServerWorld world) {
        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        isRitualFocusItem = nbt.getBoolean(TAG_IS_ITEM_IMBUED);
        readInventory(nbt);
        channelTicksRemaining = nbt.getInt(TAG_CHAN_LEFT);
        channelTotalTicks = nbt.getInt(TAG_CHAN_TOTAL);
        pendingOutput = nbt.contains(TAG_PENDING_OUT, NbtCompound.COMPOUND_TYPE)
                ? ItemStack.fromNbt(nbt.getCompound(TAG_PENDING_OUT))
                : ItemStack.EMPTY;
        bloomPosA = nbt.getLong(TAG_BLOOM_A);
        bloomPosB = nbt.getLong(TAG_BLOOM_B);
        bloomPosC = nbt.getLong(TAG_BLOOM_C);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean(TAG_IS_ITEM_IMBUED, isRitualFocusItem);
        writeInventory(nbt);
        nbt.putInt(TAG_CHAN_LEFT, channelTicksRemaining);
        nbt.putInt(TAG_CHAN_TOTAL, channelTotalTicks);
        if (!pendingOutput.isEmpty()) {
            nbt.put(TAG_PENDING_OUT, pendingOutput.writeNbt(new NbtCompound()));
        }
        nbt.putLong(TAG_BLOOM_A, bloomPosA);
        nbt.putLong(TAG_BLOOM_B, bloomPosB);
        nbt.putLong(TAG_BLOOM_C, bloomPosC);
    }

    private void writeInventory(NbtCompound nbt) {
        ItemStack stack = inventory.get(SLOT);
        NbtCompound inventoryTag = new NbtCompound();
        if (!stack.isEmpty()) {
            inventoryTag.put("Slot0", stack.writeNbt(new NbtCompound()));
        }
        nbt.put(TAG_INVENTORY, inventoryTag);
    }

    private void readInventory(NbtCompound nbt) {
        inventory.set(SLOT, ItemStack.EMPTY);
        NbtCompound inventoryTag = nbt.getCompound(TAG_INVENTORY);
        if (inventoryTag.contains("Slot0", NbtCompound.COMPOUND_TYPE)) {
            inventory.set(SLOT, ItemStack.fromNbt(inventoryTag.getCompound("Slot0")));
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
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction direction) {
        if (slot != SLOT || isChanneling() || stack.isEmpty()) {
            return false;
        }

        return inventory.get(SLOT).isEmpty();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction direction) {
        if (slot != SLOT || isChanneling()) {
            return false;
        }

        return !stack.isEmpty();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot != SLOT || isChanneling() || stack.isEmpty()) {
            return false;
        }

        return inventory.get(SLOT).isEmpty();
    }
}