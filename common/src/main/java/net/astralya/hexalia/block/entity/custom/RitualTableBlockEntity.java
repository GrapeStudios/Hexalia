package net.astralya.hexalia.block.entity.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.astralya.hexalia.block.custom.RitualBrazierBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.gameplay.naturesritual.NaturesRitual;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.astralya.hexalia.util.ItemInteractionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RitualTableBlockEntity extends BlockEntity
    implements Container, ItemInteractionHelper.SingleItemStorage {
  private static final int SLOT = 0;
  private static final int DEFAULT_DURATION = 8 * 20;

  private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

  private ItemStack cachedParticleItem = ItemStack.EMPTY;
  private List<RitualBrazierBlockEntity> activeBraziers = Collections.emptyList();
  private List<BlockPos> grownCrops = Collections.emptyList();
  private ItemStack pendingOutput = ItemStack.EMPTY;

  private int transformTicksRemaining;
  private int totalTransformTicks;
  private int nextBrazierIndex;
  private float rotation;

  public RitualTableBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.RITUAL_TABLE.get(), pos, state);
  }

  @Override
  public int getContainerSize() {
    return inventory.size();
  }

  @Override
  public boolean isEmpty() {
    return inventory.get(SLOT).isEmpty();
  }

  @Override
  public ItemStack getItem(int slot) {
    return inventory.get(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    ItemStack removed = ContainerHelper.removeItem(inventory, slot, amount);
    if (!removed.isEmpty()) {
      inventoryChanged();
    }
    return removed;
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return ContainerHelper.takeItem(inventory, slot);
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    inventory.set(slot, stack.copyWithCount(1));
    inventoryChanged();
  }

  @Override
  public int getMaxStackSize() {
    return 1;
  }

  @Override
  public boolean addItem(ItemStack stack) {
    if (!isEmpty() || stack.isEmpty() || !canPlaceItem(SLOT, stack)) {
      return false;
    }
    setItem(SLOT, stack.split(1));
    return true;
  }

  @Override
  public ItemStack removeItem() {
    if (isEmpty()) {
      return ItemStack.EMPTY;
    }
    return removeItem(SLOT, 1);
  }

  @Override
  public boolean canPlaceItem(int slot, ItemStack stack) {
    return slot == SLOT
        && transformTicksRemaining <= 0
        && isEmpty()
        && !stack.is(ModItems.HEX_FOCUS.get());
  }

  @Override
  public boolean canTakeItem(Container target, int slot, ItemStack stack) {
    return slot == SLOT && transformTicksRemaining <= 0;
  }

  @Override
  public boolean stillValid(Player player) {
    return Container.stillValidBlockEntity(this, player);
  }

  @Override
  public void clearContent() {
    inventory.clear();
    inventoryChanged();
  }

  public float getRenderingRotation() {
    rotation = (rotation + 0.5F) % 360F;
    return rotation;
  }

  public void startTransformation(
      ItemStack output, int durationTicks, List<RitualBrazierBlockEntity> braziers) {
    if (transformTicksRemaining > 0) {
      return;
    }
    transformTicksRemaining = Math.max(1, durationTicks);
    totalTransformTicks = transformTicksRemaining;
    pendingOutput = output.copy();
    activeBraziers = new ArrayList<>(braziers);
    nextBrazierIndex = 0;
    setChanged();
  }

  public void setGrownCropPositions(List<BlockPos> crops) {
    grownCrops = new ArrayList<>(crops);
  }

  public static void serverTick(
      Level level, BlockPos pos, BlockState state, RitualTableBlockEntity table) {
    if (table.transformTicksRemaining <= 0) {
      return;
    }

    if (table.isEmpty() || hasMissingBrazierItems(table)) {
      cancelRitual(level, pos, table);
      return;
    }

    int base = table.totalTransformTicks > 0 ? table.totalTransformTicks : DEFAULT_DURATION;
    int elapsed = base - table.transformTicksRemaining;

    handleActiveBraziers(level, pos, table, elapsed);
    table.transformTicksRemaining--;

    if (table.transformTicksRemaining == 0) {
      completeRitual(level, pos, table);
    }
  }

  private static boolean hasMissingBrazierItems(RitualTableBlockEntity table) {
    for (int index = table.nextBrazierIndex; index < table.activeBraziers.size(); index++) {
      RitualBrazierBlockEntity brazier = table.activeBraziers.get(index);
      if (index == table.nextBrazierIndex && !table.cachedParticleItem.isEmpty()) {
        continue;
      }
      if (brazier == null || brazier.isRemoved() || brazier.isEmpty()) {
        return true;
      }
    }
    return false;
  }

  private static void handleActiveBraziers(
      Level level, BlockPos pos, RitualTableBlockEntity table, int elapsed) {
    if (table.activeBraziers.isEmpty() || table.nextBrazierIndex >= table.activeBraziers.size()) {
      return;
    }

    int ticksPerBrazier = 40;
    int currentTime = elapsed - (table.nextBrazierIndex * ticksPerBrazier);
    RitualBrazierBlockEntity brazier = table.activeBraziers.get(table.nextBrazierIndex);
    if (brazier == null) {
      return;
    }

    if (currentTime == 0) {
      table.cachedParticleItem = brazier.getStoredItem().copy();
      brazier.removeItem();

      BlockState brazierState = level.getBlockState(brazier.getBlockPos());
      if (brazierState.getBlock() instanceof RitualBrazierBlock
          && brazierState.hasProperty(RitualBrazierBlock.SALTED)
          && brazierState.getValue(RitualBrazierBlock.SALTED)) {
        level.setBlock(
            brazier.getBlockPos(), brazierState.setValue(RitualBrazierBlock.SALTED, false), 3);
      }
    }

    if (currentTime >= 0 && currentTime < ticksPerBrazier && level instanceof ServerLevel server) {
      spawnItemParticles(
          server,
          table.cachedParticleItem,
          brazier.getBlockPos(),
          pos,
          currentTime,
          ticksPerBrazier);
    }

    if (currentTime == ticksPerBrazier - 1) {
      if (level instanceof ServerLevel server) {
        spawnAbsorbBurst(server, pos, table.cachedParticleItem);
      }
      table.nextBrazierIndex++;
      table.cachedParticleItem = ItemStack.EMPTY;
    }
  }

  private static void spawnItemParticles(
      ServerLevel server, ItemStack item, BlockPos from, BlockPos to, int time, int totalTime) {
    if (item.isEmpty()) {
      return;
    }
    ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, item);

    double startX = from.getX() + 0.5;
    double startY = from.getY() + 0.4;
    double startZ = from.getZ() + 0.5;
    double endX = to.getX() + 0.5;
    double endY = to.getY() + 1.15;
    double endZ = to.getZ() + 0.5;
    double progress = time / (double) totalTime;

    double particleX = startX + (endX - startX) * progress;
    double particleY = startY + (endY - startY) * progress;
    double particleZ = startZ + (endZ - startZ) * progress;

    for (int index = 0; index < 3; index++) {
      double speed = 0.008 + server.random.nextDouble() * 0.004;
      double motionX = (endX - startX) * speed;
      double motionY = (endY - startY) * speed + 0.003;
      double motionZ = (endZ - startZ) * speed;
      server.sendParticles(
          particle,
          particleX + (server.random.nextDouble() - 0.5) * 0.05,
          particleY + (server.random.nextDouble() - 0.5) * 0.05,
          particleZ + (server.random.nextDouble() - 0.5) * 0.05,
          1,
          motionX,
          motionY,
          motionZ,
          0.0);
    }
  }

  private static void spawnAbsorbBurst(ServerLevel server, BlockPos pos, ItemStack item) {
    double centerX = pos.getX() + 0.5;
    double centerY = pos.getY() + 1.1;
    double centerZ = pos.getZ() + 0.5;

    for (int index = 0; index < 12; index++) {
      double offsetX = (server.random.nextDouble() - 0.5) * 0.5;
      double offsetY = server.random.nextDouble() * 0.3;
      double offsetZ = (server.random.nextDouble() - 0.5) * 0.5;
      double motionX = (server.random.nextDouble() - 0.5) * 0.02;
      double motionY = 0.04 + server.random.nextDouble() * 0.02;
      double motionZ = (server.random.nextDouble() - 0.5) * 0.02;
      server.sendParticles(
          ParticleTypes.WITCH,
          centerX + offsetX,
          centerY + offsetY,
          centerZ + offsetZ,
          1,
          motionX,
          motionY,
          motionZ,
          0.0);
    }

    if (!item.isEmpty()) {
      ItemParticleOption itemParticle = new ItemParticleOption(ParticleTypes.ITEM, item);
      for (int index = 0; index < 8; index++) {
        double offsetX = (server.random.nextDouble() - 0.5) * 0.2;
        double offsetY = server.random.nextDouble() * 0.2;
        double offsetZ = (server.random.nextDouble() - 0.5) * 0.2;
        double motionX = (server.random.nextDouble() - 0.5) * 0.005;
        double motionY = 0.015 + server.random.nextDouble() * 0.005;
        double motionZ = (server.random.nextDouble() - 0.5) * 0.005;
        server.sendParticles(
            itemParticle,
            centerX + offsetX,
            centerY + offsetY,
            centerZ + offsetZ,
            1,
            motionX,
            motionY,
            motionZ,
            0.0);
      }
    }
    server.playSound(
        null,
        pos,
        SoundEvents.ENCHANTMENT_TABLE_USE,
        SoundSource.BLOCKS,
        0.4F,
        1.2F + server.random.nextFloat() * 0.2F);
  }

  private static void completeRitual(Level level, BlockPos pos, RitualTableBlockEntity table) {
    table.setItem(SLOT, table.pendingOutput);
    table.pendingOutput = ItemStack.EMPTY;

    for (BlockPos cropPos : table.grownCrops) {
      NaturesRitual.resetCrop(level, cropPos);
    }

    table.activeBraziers = Collections.emptyList();
    table.nextBrazierIndex = 0;

    level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.8F, 1.0F);
    if (level instanceof ServerLevel server) {
      server.sendParticles(
          ModParticleTypes.LEAVES.get(),
          pos.getX() + 0.5,
          pos.getY() + 1.0,
          pos.getZ() + 0.5,
          15,
          0.3,
          0.3,
          0.3,
          0.0);
    }
    table.setChanged();
  }

  private static void cancelRitual(Level level, BlockPos pos, RitualTableBlockEntity table) {
    table.transformTicksRemaining = 0;
    table.totalTransformTicks = 0;
    table.pendingOutput = ItemStack.EMPTY;
    table.activeBraziers = Collections.emptyList();
    table.nextBrazierIndex = 0;
    table.cachedParticleItem = ItemStack.EMPTY;

    if (level instanceof ServerLevel server) {
      server.sendParticles(
          ParticleTypes.SMOKE,
          pos.getX() + 0.5,
          pos.getY() + 1.0,
          pos.getZ() + 0.5,
          12,
          0.4,
          0.4,
          0.4,
          0.02);
      Player nearest = server.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, false);
      if (nearest != null) {
        nearest.displayClientMessage(
            Component.translatable("message.hexalia.natures_ritual.stopped_ritual"), true);
      }
    }
    level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 0.4F, 0.6F);
    table.setChanged();
  }

  private void inventoryChanged() {
    setChanged();
    if (level != null && !level.isClientSide()) {
      level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    ContainerHelper.saveAllItems(tag, inventory, registries);
    tag.putInt("TicksLeft", transformTicksRemaining);
    tag.putInt("TotalTicks", totalTransformTicks);
    if (!pendingOutput.isEmpty()) {
      tag.put("PendingOut", pendingOutput.save(registries));
    }
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    ContainerHelper.loadAllItems(tag, inventory, registries);
    transformTicksRemaining = tag.getInt("TicksLeft");
    totalTransformTicks = tag.getInt("TotalTicks");
    pendingOutput =
        tag.contains("PendingOut")
            ? ItemStack.parseOptional(registries, tag.getCompound("PendingOut"))
            : ItemStack.EMPTY;
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
