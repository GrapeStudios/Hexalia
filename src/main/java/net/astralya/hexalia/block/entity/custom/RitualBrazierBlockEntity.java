package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.RitualBrazierRecipe;
import net.astralya.hexalia.recipe.RitualBrazierRecipeInput;
import net.astralya.hexalia.util.SunlightCheck;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = HexaliaMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RitualBrazierBlockEntity extends SyncBlockEntity {

    public static final int CHANNEL_DURATION = 120;

    public enum RitualResult { SUCCESS, NO_CELESTIAL_BLOOMS, NO_SKY, INVALID_ITEM, ALREADY_CHANNELING }

    private final ItemStackHandler inventory;
    private final RecipeManager.CachedCheck<RitualBrazierRecipeInput, RitualBrazierRecipe> quickCheck;

    private boolean isRitualFocusItem;
    private float rotation;

    private int channelTicksRemaining;
    private int channelTotalTicks;
    private ItemStack pendingOutput;
    private long bloomPosA;
    private long bloomPosB;
    private long bloomPosC;

    public RitualBrazierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.RITUAL_BRAZIER.get(), pos, state);
        this.inventory = createHandler();
        this.isRitualFocusItem = false;
        this.quickCheck = RecipeManager.createCheck(ModRecipes.RITUAL_BRAZIER_TYPE.get());
        this.channelTicksRemaining = 0;
        this.channelTotalTicks = 0;
        this.pendingOutput = ItemStack.EMPTY;
        this.bloomPosA = 0L;
        this.bloomPosB = 0L;
        this.bloomPosC = 0L;
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
        if (level == null || isRitualFocusItem) {
            return RitualResult.INVALID_ITEM;
        }

        if (isChanneling()) {
            return RitualResult.ALREADY_CHANNELING;
        }

        if (isEmpty()) {
            return RitualResult.INVALID_ITEM;
        }

        if (!SunlightCheck.hasOpenSky(level, worldPosition.above())) {
            return RitualResult.NO_SKY;
        }

        List<BlockPos> blooms = findNearbyCelestialBlooms(3);
        if (blooms.size() < 3) {
            return RitualResult.NO_CELESTIAL_BLOOMS;
        }

        Optional<RecipeHolder<RitualBrazierRecipe>> match = level.getRecipeManager()
                .getRecipeFor(ModRecipes.RITUAL_BRAZIER_TYPE.get(), new RitualBrazierRecipeInput(getStoredItem()), level);

        if (match.isEmpty()) {
            return RitualResult.INVALID_ITEM;
        }

        ItemStack out = match.get().value().getResultItem(level.registryAccess());
        if (out.isEmpty()) {
            return RitualResult.INVALID_ITEM;
        }

        this.pendingOutput = out.copy();
        this.bloomPosA = blooms.get(0).asLong();
        this.bloomPosB = blooms.get(1).asLong();
        this.bloomPosC = blooms.get(2).asLong();

        this.channelTotalTicks = CHANNEL_DURATION;
        this.channelTicksRemaining = CHANNEL_DURATION;

        setChanged();
        if (!level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }

        return RitualResult.SUCCESS;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RitualBrazierBlockEntity be) {
        if (be.channelTicksRemaining <= 0) {
            return;
        }

        if (be.isEmpty() || be.pendingOutput.isEmpty()) {
            be.cancelChannel(level, pos);
            return;
        }

        if (!SunlightCheck.canSeeSun(level, pos.above())) {
            be.cancelChannel(level, pos);
            return;
        }

        BlockPos a = BlockPos.of(be.bloomPosA);
        BlockPos b = BlockPos.of(be.bloomPosB);
        BlockPos c = BlockPos.of(be.bloomPosC);

        if (!be.isValidBloomPos(a) || !be.isValidBloomPos(b) || !be.isValidBloomPos(c)) {
            be.cancelChannel(level, pos);
            return;
        }

        if (level instanceof ServerLevel server) {
            be.emitChannelParticles(server, pos, a, b, c);
        }

        be.channelTicksRemaining--;

        if (be.channelTicksRemaining == 0) {
            be.completeChannel(level, pos);
        }

        be.setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
    }

    private boolean isValidBloomPos(BlockPos pos) {
        if (level == null) {
            return false;
        }
        BlockState bs = level.getBlockState(pos);
        return bs.is(ModBlocks.CELESTIAL_BLOOM.get()) || bs.is(ModBlocks.WITHERED_CELESTIAL_BLOOM.get());
    }

    private void cancelChannel(Level level, BlockPos pos) {
        this.channelTicksRemaining = 0;
        this.channelTotalTicks = 0;
        this.pendingOutput = ItemStack.EMPTY;
        this.bloomPosA = 0L;
        this.bloomPosB = 0L;
        this.bloomPosC = 0L;

        if (level instanceof ServerLevel server) {
            server.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 0.35F, 0.7F);
        }
    }

    private void completeChannel(Level level, BlockPos pos) {
        ItemStack resultStack = this.pendingOutput.copy();
        this.pendingOutput = ItemStack.EMPTY;

        BlockPos a = BlockPos.of(this.bloomPosA);
        BlockPos b = BlockPos.of(this.bloomPosB);
        BlockPos c = BlockPos.of(this.bloomPosC);

        this.bloomPosA = 0L;
        this.bloomPosB = 0L;
        this.bloomPosC = 0L;

        inventory.setStackInSlot(0, resultStack.copyWithCount(1));
        inventoryChanged();

        degradeCelestialBloom(a);
        degradeCelestialBloom(b);
        degradeCelestialBloom(c);

        if (level instanceof ServerLevel server) {
            server.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.45F, 1.15F);
            server.sendParticles(ModParticleType.SPARKLE.get(), pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 18, 0.25, 0.2, 0.25, 0.0);
        }

        this.channelTicksRemaining = 0;
        this.channelTotalTicks = 0;

        setChanged();
        level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
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

    private void degradeCelestialBloom(BlockPos pos) {
        if (level == null) {
            return;
        }

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

        if (level instanceof ServerLevel server) {
            server.sendParticles(
                    ModParticleType.LEAVES.get(),
                    pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5,
                    14, 0.2, 0.25, 0.2, 0.0
            );
        }
    }

    private List<BlockPos> findNearbyCelestialBlooms(int radius) {
        List<BlockPos> result = new ArrayList<>();
        if (level == null) {
            return result;
        }

        BlockPos origin = getBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                BlockPos check = origin.offset(dx, 0, dz);
                BlockState bs = level.getBlockState(check);

                if (bs.is(ModBlocks.CELESTIAL_BLOOM.get()) || bs.is(ModBlocks.WITHERED_CELESTIAL_BLOOM.get())) {
                    result.add(check);
                    if (result.size() >= 3) {
                        return result;
                    }
                }
            }
        }

        return result;
    }

    public boolean addItem(ItemStack itemStack) {
        if (isChanneling()) {
            return false;
        }

        if (isEmpty() && !itemStack.isEmpty()) {
            inventory.setStackInSlot(0, itemStack.split(1));
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
        return inventory.getStackInSlot(0);
    }

    public boolean isEmpty() {
        return inventory.getStackInSlot(0).isEmpty();
    }

    public float getRenderingRotation() {
        rotation += isChanneling() ? 1.5f : 0.5f;
        if (rotation >= 360) rotation = 0;
        return rotation;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("IsItemImbued", this.isRitualFocusItem);
        tag.put("Inventory", this.inventory.serializeNBT(provider));
        tag.putInt("ChanLeft", this.channelTicksRemaining);
        tag.putInt("ChanTotal", this.channelTotalTicks);
        if (!this.pendingOutput.isEmpty()) {
            tag.put("PendingOut", this.pendingOutput.save(provider));
        }
        tag.putLong("BloomA", this.bloomPosA);
        tag.putLong("BloomB", this.bloomPosB);
        tag.putLong("BloomC", this.bloomPosC);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        isRitualFocusItem = tag.getBoolean("IsItemImbued");
        inventory.deserializeNBT(provider, tag.getCompound("Inventory"));
        this.channelTicksRemaining = tag.getInt("ChanLeft");
        this.channelTotalTicks = tag.getInt("ChanTotal");
        this.pendingOutput = tag.contains("PendingOut") ? ItemStack.parseOptional(provider, tag.getCompound("PendingOut")) : ItemStack.EMPTY;
        this.bloomPosA = tag.getLong("BloomA");
        this.bloomPosB = tag.getLong("BloomB");
        this.bloomPosC = tag.getLong("BloomC");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("IsItemImbued", isRitualFocusItem);
        tag.put("Inventory", inventory.serializeNBT(provider));
        tag.putInt("ChanLeft", this.channelTicksRemaining);
        tag.putInt("ChanTotal", this.channelTotalTicks);
        if (!this.pendingOutput.isEmpty()) {
            tag.put("PendingOut", this.pendingOutput.save(provider));
        }
        tag.putLong("BloomA", this.bloomPosA);
        tag.putLong("BloomB", this.bloomPosB);
        tag.putLong("BloomC", this.bloomPosC);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.RITUAL_BRAZIER.get(),
                (be, ctx) -> be.getInventory()
        );
    }
}