package net.astralya.hexalia.block.entity.custom;

import java.util.Optional;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.astralya.hexalia.recipe.CelestialInfusionRecipe;
import net.astralya.hexalia.recipe.CelestialInfusionRecipeInput;
import net.astralya.hexalia.recipe.ModRecipeTypes;
import net.astralya.hexalia.util.ItemInteractionHelper;
import net.astralya.hexalia.util.SunlightCheck;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RitualBrazierBlockEntity extends BlockEntity
    implements Container, Clearable, ItemInteractionHelper.SingleItemStorage {
  public static final int CHANNEL_DURATION = 120;

  private static final String TAG_ITEM = "Item";
  private static final String TAG_CHAN_LEFT = "ChanLeft";
  private static final String TAG_CHAN_TOTAL = "ChanTotal";
  private static final String TAG_PENDING_OUT = "PendingOut";
  private static final String TAG_BLOOM_A = "BloomA";
  private static final String TAG_BLOOM_B = "BloomB";
  private static final String TAG_BLOOM_C = "BloomC";

  private ItemStack item = ItemStack.EMPTY;
  private float rotation;
  private int channelTicksRemaining;
  private int channelTotalTicks;
  private ItemStack pendingOutput = ItemStack.EMPTY;
  private long bloomPosA;
  private long bloomPosB;
  private long bloomPosC;

  public RitualBrazierBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.RITUAL_BRAZIER.get(), pos, state);
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
    if (isChanneling()) {
      return RitualResult.ALREADY_CHANNELING;
    }
    if (item.isEmpty()) {
      return RitualResult.INVALID_ITEM;
    }
    if (!SunlightCheck.hasOpenSky(server, worldPosition.above())) {
      return RitualResult.NO_SKY;
    }
    if (!captureNearbyCelestialBlooms(3)) {
      return RitualResult.NO_CELESTIAL_BLOOMS;
    }

    Optional<RecipeHolder<CelestialInfusionRecipe>> match =
        server
            .getRecipeManager()
            .getRecipeFor(
                ModRecipeTypes.CELESTIAL_INFUSION.get(),
                new CelestialInfusionRecipeInput(item),
                server);

    if (match.isEmpty()) {
      clearCapturedBlooms();
      return RitualResult.INVALID_ITEM;
    }

    ItemStack output = match.get().value().getResultItem(server.registryAccess());
    if (output.isEmpty()) {
      clearCapturedBlooms();
      return RitualResult.INVALID_ITEM;
    }

    pendingOutput = output.copy();
    channelTotalTicks = CHANNEL_DURATION;
    channelTicksRemaining = CHANNEL_DURATION;
    sync(server, worldPosition);
    return RitualResult.SUCCESS;
  }

  public static void serverTick(
      Level level, BlockPos pos, BlockState state, RitualBrazierBlockEntity brazier) {
    if (!(level instanceof ServerLevel server) || !brazier.isChanneling()) {
      return;
    }
    if (brazier.isEmpty() || brazier.pendingOutput.isEmpty()) {
      brazier.cancelChannel(server, pos);
      return;
    }
    if (!SunlightCheck.canSeeSun(server, pos.above())) {
      brazier.cancelChannel(server, pos);
      return;
    }

    BlockPos a = BlockPos.of(brazier.bloomPosA);
    BlockPos b = BlockPos.of(brazier.bloomPosB);
    BlockPos c = BlockPos.of(brazier.bloomPosC);

    if (!brazier.isValidBloomPos(a) || !brazier.isValidBloomPos(b) || !brazier.isValidBloomPos(c)) {
      brazier.cancelChannel(server, pos);
      return;
    }

    brazier.emitChannelParticles(server, pos, a, b, c);
    brazier.channelTicksRemaining--;

    if (brazier.channelTicksRemaining <= 0) {
      brazier.completeChannel(server, pos);
      return;
    }

    brazier.setChanged();
    server.sendBlockUpdated(pos, state, state, 3);
  }

  private boolean isValidBloomPos(BlockPos pos) {
    if (level == null) {
      return false;
    }
    BlockState state = level.getBlockState(pos);
    return state.is(ModBlocks.CELESTIAL_BLOOM.get())
        || state.is(ModBlocks.WITHERED_CELESTIAL_BLOOM.get());
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
    item = pendingOutput.copyWithCount(1);
    pendingOutput = ItemStack.EMPTY;

    BlockPos a = BlockPos.of(bloomPosA);
    BlockPos b = BlockPos.of(bloomPosB);
    BlockPos c = BlockPos.of(bloomPosC);
    clearCapturedBlooms();

    degradeCelestialBloom(level, a);
    degradeCelestialBloom(level, b);
    degradeCelestialBloom(level, c);

    level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.45F, 1.15F);
    level.sendParticles(
        ModParticleTypes.SPARKLE.get(),
        pos.getX() + 0.5,
        pos.getY() + 0.9,
        pos.getZ() + 0.5,
        18,
        0.25,
        0.2,
        0.25,
        0.0);

    channelTicksRemaining = 0;
    channelTotalTicks = 0;
    sync(level, pos);
  }

  private void emitChannelParticles(
      ServerLevel server, BlockPos brazierPos, BlockPos a, BlockPos b, BlockPos c) {
    double x = brazierPos.getX() + 0.5;
    double y = brazierPos.getY() + 0.85;
    double z = brazierPos.getZ() + 0.5;
    server.sendParticles(ModParticleTypes.SPARKLE.get(), x, y, z, 2, 0.2, 0.1, 0.2, 0.0);
    emitBloomSparkles(server, a);
    emitBloomSparkles(server, b);
    emitBloomSparkles(server, c);
  }

  private void emitBloomSparkles(ServerLevel server, BlockPos bloomPos) {
    server.sendParticles(
        ModParticleTypes.SPARKLE.get(),
        bloomPos.getX() + 0.5,
        bloomPos.getY() + 0.75,
        bloomPos.getZ() + 0.5,
        1,
        0.15,
        0.15,
        0.15,
        0.0);
  }

  private void degradeCelestialBloom(ServerLevel level, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    BlockState next = null;

    if (state.is(ModBlocks.CELESTIAL_BLOOM.get())) {
      next = ModBlocks.WITHERED_CELESTIAL_BLOOM.get().defaultBlockState();
    } else if (state.is(ModBlocks.WITHERED_CELESTIAL_BLOOM.get())) {
      next = Blocks.DEAD_BUSH.defaultBlockState();
    }

    if (next != null) {
      level.setBlock(pos, next, 3);
      level.sendParticles(
          ParticleTypes.HAPPY_VILLAGER,
          pos.getX() + 0.5,
          pos.getY() + 0.6,
          pos.getZ() + 0.5,
          14,
          0.2,
          0.25,
          0.2,
          0.0);
    }
  }

  private boolean captureNearbyCelestialBlooms(int radius) {
    if (level == null) {
      return false;
    }
    clearCapturedBlooms();

    for (int dx = -radius; dx <= radius; dx++) {
      for (int dz = -radius; dz <= radius; dz++) {
        if (dx == 0 && dz == 0) {
          continue;
        }

        BlockPos check = worldPosition.offset(dx, 0, dz);
        BlockState state = level.getBlockState(check);
        if (!state.is(ModBlocks.CELESTIAL_BLOOM.get())
            && !state.is(ModBlocks.WITHERED_CELESTIAL_BLOOM.get())) {
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

  public boolean addItem(ItemStack stack) {
    if (isChanneling() || !item.isEmpty() || stack.isEmpty()) {
      return false;
    }
    item = stack.split(1);
    inventoryChanged();
    return true;
  }

  public ItemStack removeItem() {
    if (isChanneling() || item.isEmpty()) {
      return ItemStack.EMPTY;
    }
    ItemStack removed = item.split(1);
    inventoryChanged();
    return removed;
  }

  @Override
  public int getContainerSize() {
    return 1;
  }

  @Override
  public boolean isEmpty() {
    return item.isEmpty();
  }

  @Override
  public ItemStack getItem(int slot) {
    return slot == 0 ? item : ItemStack.EMPTY;
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    if (slot != 0 || amount <= 0 || isChanneling()) {
      return ItemStack.EMPTY;
    }
    ItemStack removed = item.split(amount);
    if (!removed.isEmpty()) {
      inventoryChanged();
    }
    return removed;
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    if (slot != 0 || isChanneling()) {
      return ItemStack.EMPTY;
    }
    ItemStack removed = item;
    item = ItemStack.EMPTY;
    return removed;
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    if (slot != 0) {
      return;
    }
    item = stack.copyWithCount(Math.min(stack.getCount(), getMaxStackSize()));
    inventoryChanged();
  }

  @Override
  public int getMaxStackSize() {
    return 1;
  }

  @Override
  public boolean canPlaceItem(int slot, ItemStack stack) {
    return slot == 0 && !isChanneling() && item.isEmpty() && !stack.isEmpty();
  }

  @Override
  public boolean canTakeItem(Container target, int slot, ItemStack stack) {
    return slot == 0 && !isChanneling();
  }

  @Override
  public boolean stillValid(net.minecraft.world.entity.player.Player player) {
    return Container.stillValidBlockEntity(this, player);
  }

  @Override
  public void clearContent() {
    item = ItemStack.EMPTY;
    inventoryChanged();
  }

  public ItemStack getStoredItem() {
    return item;
  }

  public float getRenderingRotation() {
    if (level == null || !level.isClientSide) {
      return rotation;
    }
    rotation += isChanneling() ? 1.5F : 0.5F;
    if (rotation >= 360.0F) {
      rotation = 0.0F;
    }
    return rotation;
  }

  private void sync(ServerLevel level, BlockPos pos) {
    setChanged();
    level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
  }

  private void inventoryChanged() {
    setChanged();
    if (level != null && !level.isClientSide()) {
      level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
    CompoundTag tag = new CompoundTag();
    saveAdditional(tag, provider);
    return tag;
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
    super.loadAdditional(tag, provider);
    item =
        tag.contains(TAG_ITEM)
            ? ItemStack.parseOptional(provider, tag.getCompound(TAG_ITEM))
            : ItemStack.EMPTY;
    channelTicksRemaining = tag.getInt(TAG_CHAN_LEFT);
    channelTotalTicks = tag.getInt(TAG_CHAN_TOTAL);
    pendingOutput =
        tag.contains(TAG_PENDING_OUT)
            ? ItemStack.parseOptional(provider, tag.getCompound(TAG_PENDING_OUT))
            : ItemStack.EMPTY;
    bloomPosA = tag.getLong(TAG_BLOOM_A);
    bloomPosB = tag.getLong(TAG_BLOOM_B);
    bloomPosC = tag.getLong(TAG_BLOOM_C);
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
    super.saveAdditional(tag, provider);
    if (!item.isEmpty()) {
      tag.put(TAG_ITEM, item.save(provider));
    }
    tag.putInt(TAG_CHAN_LEFT, channelTicksRemaining);
    tag.putInt(TAG_CHAN_TOTAL, channelTotalTicks);
    if (!pendingOutput.isEmpty()) {
      tag.put(TAG_PENDING_OUT, pendingOutput.save(provider));
    }
    tag.putLong(TAG_BLOOM_A, bloomPosA);
    tag.putLong(TAG_BLOOM_B, bloomPosB);
    tag.putLong(TAG_BLOOM_C, bloomPosC);
  }

  public enum RitualResult {
    SUCCESS,
    NO_CELESTIAL_BLOOMS,
    NO_SKY,
    INVALID_ITEM,
    ALREADY_CHANNELING
  }
}
