package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.RitualBrazierRecipe;
import net.astralya.hexalia.recipe.RitualBrazierRecipeInput;
import net.astralya.hexalia.util.SidedItemHandlers;
import net.astralya.hexalia.util.SunlightCheck;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.Optional;

public class RitualBrazierBlockEntity extends SyncBlockEntity implements Clearable {

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

    private final ItemStackHandler inventory;
    private final IItemHandler upInputHandler;
    private final IItemHandler downOutputHandler;

    private boolean isRitualFocusItem;
    private float rotation;

    private int channelTicksRemaining;
    private int channelTotalTicks;
    private ItemStack pendingOutput = ItemStack.EMPTY;
    private long bloomPosA;
    private long bloomPosB;
    private long bloomPosC;

    public RitualBrazierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.RITUAL_BRAZIER.get(), pos, state);

        this.inventory = createHandler();

        this.upInputHandler = SidedItemHandlers.view(inventory, new int[]{SLOT}, true, false);
        this.downOutputHandler = SidedItemHandlers.view(inventory, new int[]{SLOT}, false, true);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                inventoryChanged();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    public IItemHandler getItemHandler(Direction side) {
        if (isChanneling()) {
            return SidedItemHandlers.blocked();
        }

        return SidedItemHandlers.upDown(side, upInputHandler, downOutputHandler);
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
        return Mth.clamp(elapsed / (float) channelTotalTicks, 0.0F, 1.0F);
    }

    public RitualResult tryStartCelestialInfusion() {
        if (!(level instanceof ServerLevel server)) {
            return RitualResult.INVALID_ITEM;
        }

        if (isRitualFocusItem) {
            return RitualResult.INVALID_ITEM;
        }

        if (isChanneling()) {
            return RitualResult.ALREADY_CHANNELING;
        }

        ItemStack in = getStoredItem();
        if (in.isEmpty()) {
            return RitualResult.INVALID_ITEM;
        }

        if (!SunlightCheck.hasOpenSky(server, worldPosition.above())) {
            return RitualResult.NO_SKY;
        }

        if (!captureNearbyCelestialBlooms(3)) {
            return RitualResult.NO_CELESTIAL_BLOOMS;
        }

        Optional<RecipeHolder<RitualBrazierRecipe>> match = server.getRecipeManager()
                .getRecipeFor(ModRecipes.RITUAL_BRAZIER_TYPE.get(), new RitualBrazierRecipeInput(in), server);

        if (match.isEmpty()) {
            clearCapturedBlooms();
            return RitualResult.INVALID_ITEM;
        }

        ItemStack out = match.get().value().getResultItem(server.registryAccess());
        if (out.isEmpty()) {
            clearCapturedBlooms();
            return RitualResult.INVALID_ITEM;
        }

        pendingOutput = out.copy();
        channelTotalTicks = CHANNEL_DURATION;
        channelTicksRemaining = CHANNEL_DURATION;

        sync(server, worldPosition);
        return RitualResult.SUCCESS;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RitualBrazierBlockEntity be) {
        if (!(level instanceof ServerLevel server)) {
            return;
        }

        if (!be.isChanneling()) {
            return;
        }

        if (be.isEmpty() || be.pendingOutput.isEmpty()) {
            be.cancelChannel(server, pos);
            return;
        }

        if (!SunlightCheck.canSeeSun(server, pos.above())) {
            be.cancelChannel(server, pos);
            return;
        }

        BlockPos a = BlockPos.of(be.bloomPosA);
        BlockPos b = BlockPos.of(be.bloomPosB);
        BlockPos c = BlockPos.of(be.bloomPosC);

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

        be.setChanged();
        server.sendBlockUpdated(pos, state, state, 3);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    private boolean isValidBloomPos(BlockPos pos) {
        if (level == null) {
            return false;
        }
        BlockState bs = level.getBlockState(pos);
        return bs.is(ModBlocks.CELESTIAL_BLOOM.get()) || bs.is(ModBlocks.WITHERED_CELESTIAL_BLOOM.get());
    }

    private void cancelChannel(ServerLevel level, BlockPos pos) {
        channelTicksRemaining = 0;
        channelTotalTicks = 0;
        pendingOutput = ItemStack.EMPTY;
        clearCapturedBlooms();

        level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 0.35F, 0.7F);
        sync(level, pos);
    }

    private void completeChannel(ServerLevel level, BlockPos pos) {
        ItemStack resultStack = pendingOutput.copyWithCount(1);
        pendingOutput = ItemStack.EMPTY;

        BlockPos a = BlockPos.of(bloomPosA);
        BlockPos b = BlockPos.of(bloomPosB);
        BlockPos c = BlockPos.of(bloomPosC);
        clearCapturedBlooms();

        inventory.setStackInSlot(SLOT, resultStack);
        inventoryChanged();

        degradeCelestialBloom(level, a);
        degradeCelestialBloom(level, b);
        degradeCelestialBloom(level, c);

        level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.45F, 1.15F);
        level.sendParticles(ModParticleType.SPARKLE.get(), pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 18, 0.25, 0.2, 0.25, 0.0);

        channelTicksRemaining = 0;
        channelTotalTicks = 0;

        sync(level, pos);
    }

    private void emitChannelParticles(ServerLevel server, BlockPos brazierPos, BlockPos a, BlockPos b, BlockPos c) {
        double bx = brazierPos.getX() + 0.5;
        double by = brazierPos.getY() + 0.85;
        double bz = brazierPos.getZ() + 0.5;

        for (int i = 0; i < 2; i++) {
            double ox = (server.random.nextDouble() - 0.5) * 0.35;
            double oz = (server.random.nextDouble() - 0.5) * 0.35;
            double oy = (server.random.nextDouble() * 0.20);
            server.sendParticles(ModParticleType.SPARKLE.get(), bx + ox, by + oy, bz + oz, 1, 0, 0, 0, 0.0);
        }

        emitBloomSparkles(server, a);
        emitBloomSparkles(server, b);
        emitBloomSparkles(server, c);
    }

    private void emitBloomSparkles(ServerLevel server, BlockPos bloomPos) {
        double x = bloomPos.getX() + 0.5 + (server.random.nextDouble() - 0.5) * 0.25;
        double y = bloomPos.getY() + 0.55 + server.random.nextDouble() * 0.35;
        double z = bloomPos.getZ() + 0.5 + (server.random.nextDouble() - 0.5) * 0.25;
        server.sendParticles(ModParticleType.SPARKLE.get(), x, y, z, 1, 0, 0, 0, 0.0);
    }

    private void degradeCelestialBloom(ServerLevel level, BlockPos pos) {
        BlockState bs = level.getBlockState(pos);
        BlockState next = null;

        if (bs.is(ModBlocks.CELESTIAL_BLOOM.get())) {
            next = ModBlocks.WITHERED_CELESTIAL_BLOOM.get().defaultBlockState();
        } else if (bs.is(ModBlocks.WITHERED_CELESTIAL_BLOOM.get())) {
            next = Blocks.DEAD_BUSH.defaultBlockState();
        }

        if (next == null) {
            return;
        }

        level.setBlock(pos, next, 3);
        level.sendParticles(ModParticleType.LEAVES.get(), pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 14, 0.2, 0.25, 0.2, 0.0);
    }

    private boolean captureNearbyCelestialBlooms(int radius) {
        if (level == null) {
            return false;
        }

        clearCapturedBlooms();

        BlockPos origin = worldPosition;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                BlockPos check = origin.offset(dx, 0, dz);
                BlockState bs = level.getBlockState(check);

                if (!bs.is(ModBlocks.CELESTIAL_BLOOM.get()) && !bs.is(ModBlocks.WITHERED_CELESTIAL_BLOOM.get())) {
                    continue;
                }

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
        if (isChanneling()) {
            return false;
        }

        if (isEmpty() && !itemStack.isEmpty()) {
            inventory.setStackInSlot(SLOT, itemStack.split(1));
            isRitualFocusItem = false;
            inventoryChanged();
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
            inventoryChanged();
            return item;
        }

        return ItemStack.EMPTY;
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    public ItemStack getStoredItem() {
        return inventory.getStackInSlot(SLOT);
    }

    public boolean isEmpty() {
        return getStoredItem().isEmpty();
    }

    public float getRenderingRotation() {
        if (level == null || !level.isClientSide) {
            return rotation;
        }

        rotation += isChanneling() ? 1.5f : 0.5f;
        if (rotation >= 360.0f) {
            rotation = 0.0f;
        }

        return rotation;
    }

    private void sync(ServerLevel level, BlockPos pos) {
        setChanged();
        level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(TAG_IS_ITEM_IMBUED, isRitualFocusItem);
        tag.put(TAG_INVENTORY, inventory.serializeNBT(provider));
        tag.putInt(TAG_CHAN_LEFT, channelTicksRemaining);
        tag.putInt(TAG_CHAN_TOTAL, channelTotalTicks);
        if (!pendingOutput.isEmpty()) {
            tag.put(TAG_PENDING_OUT, pendingOutput.save(provider));
        }
        tag.putLong(TAG_BLOOM_A, bloomPosA);
        tag.putLong(TAG_BLOOM_B, bloomPosB);
        tag.putLong(TAG_BLOOM_C, bloomPosC);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        isRitualFocusItem = tag.getBoolean(TAG_IS_ITEM_IMBUED);
        inventory.deserializeNBT(provider, tag.getCompound(TAG_INVENTORY));

        channelTicksRemaining = tag.getInt(TAG_CHAN_LEFT);
        channelTotalTicks = tag.getInt(TAG_CHAN_TOTAL);

        pendingOutput = tag.contains(TAG_PENDING_OUT)
                ? ItemStack.parseOptional(provider, tag.getCompound(TAG_PENDING_OUT))
                : ItemStack.EMPTY;

        bloomPosA = tag.getLong(TAG_BLOOM_A);
        bloomPosB = tag.getLong(TAG_BLOOM_B);
        bloomPosC = tag.getLong(TAG_BLOOM_C);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.putBoolean(TAG_IS_ITEM_IMBUED, isRitualFocusItem);
        tag.put(TAG_INVENTORY, inventory.serializeNBT(provider));
        tag.putInt(TAG_CHAN_LEFT, channelTicksRemaining);
        tag.putInt(TAG_CHAN_TOTAL, channelTotalTicks);

        if (!pendingOutput.isEmpty()) {
            tag.put(TAG_PENDING_OUT, pendingOutput.save(provider));
        }

        tag.putLong(TAG_BLOOM_A, bloomPosA);
        tag.putLong(TAG_BLOOM_B, bloomPosB);
        tag.putLong(TAG_BLOOM_C, bloomPosC);
    }
}