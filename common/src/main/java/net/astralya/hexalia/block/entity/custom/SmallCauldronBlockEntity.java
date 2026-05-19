package net.astralya.hexalia.block.entity.custom;

import java.util.List;
import net.astralya.hexalia.block.custom.SmallCauldronBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.gameplay.smallcauldron.SmallCauldronContents;
import net.astralya.hexalia.util.ItemInteractionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class SmallCauldronBlockEntity extends BlockEntity
    implements Container, Clearable, ItemInteractionHelper.ItemStorage {
  public static final int STIR_ANIM_TICKS = 20;
  private static final String TAG_STIR_ANIM_TICK = "StirAnimTick";
  private static final int SPOILED_AURA_INTERVAL_TICKS = 20;

  private final SmallCauldronContents contents = new SmallCauldronContents();
  private int stirAnimTick;
  private long clientStirStartGameTime;
  private int clientStirStartTick;
  private boolean stirAnimDirty;

  public SmallCauldronBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.SMALL_CAULDRON.get(), pos, state);
  }

  public void tick(Level level, BlockPos pos, BlockState state) {
    if (!(level instanceof ServerLevel server)) {
      return;
    }
    if (stirAnimTick > 0) {
      stirAnimTick--;
      stirAnimDirty = true;
    }

    contents.tickServer(server, state.getValue(SmallCauldronBlock.LIT));
    if (contents.isSpoiled()) {
      applySpoiledAura(server, pos);
    }
    syncIfNeeded();
  }

  public boolean canStir(BlockState state, Player player) {
    return contents.canStir(state.getValue(SmallCauldronBlock.LIT));
  }

  public boolean tryStir(BlockState state, Player player) {
    if (!(level instanceof ServerLevel server)
        || !contents.canStir(state.getValue(SmallCauldronBlock.LIT))) {
      return false;
    }
    contents.stir(server);
    stirAnimTick = STIR_ANIM_TICKS;
    stirAnimDirty = true;
    syncIfNeeded();
    return true;
  }

  public int getStirAnimTick() {
    return stirAnimTick;
  }

  public float getStirProgress(float partialTick) {
    Level level = getLevel();
    if (level == null) {
      return 0.0F;
    }
    if (level.isClientSide()) {
      if (clientStirStartTick <= 0) {
        return 0.0F;
      }
      float elapsed = (level.getGameTime() - clientStirStartGameTime) + partialTick;
      float progress = elapsed / (float) clientStirStartTick;
      return progress >= 1.0F ? 0.0F : Mth.clamp(progress, 0.0F, 1.0F);
    }
    if (stirAnimTick <= 0) {
      return 0.0F;
    }
    float progress = (STIR_ANIM_TICKS - (stirAnimTick - partialTick)) / (float) STIR_ANIM_TICKS;
    return Mth.clamp(progress, 0.0F, 1.0F);
  }

  public List<ItemStack> getIngredientsForRender() {
    return contents.getIngredientsForRender();
  }

  @Override
  public int getContainerSize() {
    return SmallCauldronContents.MAX_INGREDIENTS;
  }

  @Override
  public boolean isEmpty() {
    for (int slot = 0; slot < getContainerSize(); slot++) {
      if (!contents.getIngredient(slot).isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack getItem(int slot) {
    return contents.getIngredient(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    if (slot < 0
        || slot >= getContainerSize()
        || amount <= 0
        || !contents.canExtractOneIngredient(getBlockState().getValue(SmallCauldronBlock.LIT))) {
      return ItemStack.EMPTY;
    }
    ItemStack existing = contents.getIngredient(slot);
    if (existing.isEmpty()) {
      return ItemStack.EMPTY;
    }
    contents.setIngredient(slot, ItemStack.EMPTY);
    syncIfNeeded();
    return existing.copyWithCount(1);
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    if (slot < 0 || slot >= getContainerSize()) {
      return ItemStack.EMPTY;
    }
    ItemStack existing = contents.getIngredient(slot);
    contents.setIngredient(slot, ItemStack.EMPTY);
    return existing;
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    if (slot < 0 || slot >= getContainerSize()) {
      return;
    }
    contents.setIngredient(slot, stack);
    syncIfNeeded();
  }

  @Override
  public int getMaxStackSize() {
    return 1;
  }

  @Override
  public boolean canPlaceItem(int slot, ItemStack stack) {
    return slot >= 0
        && slot < getContainerSize()
        && contents.getIngredient(slot).isEmpty()
        && contents.canInsertOne(stack);
  }

  @Override
  public boolean canTakeItem(Container target, int slot, ItemStack stack) {
    return slot >= 0
        && slot < getContainerSize()
        && contents.canExtractOneIngredient(getBlockState().getValue(SmallCauldronBlock.LIT));
  }

  @Override
  public boolean stillValid(Player player) {
    return Container.stillValidBlockEntity(this, player);
  }

  @Override
  public void clearContent() {
    contents.resetToEmpty();
    contents.clearDirty();
    syncIfNeeded();
  }

  public float getLiquidFill01() {
    return contents.getLiquidFill01();
  }

  public float getVisualLiquidFill01() {
    return contents.getVisualLiquidFill01();
  }

  public int getVisualLiquidColor() {
    return contents.getVisualLiquidColor();
  }

  public boolean isSpoiled() {
    return contents.isSpoiled();
  }

  public boolean isCooking() {
    return contents.isCooking();
  }

  public boolean hasMixture() {
    return contents.hasMixture();
  }

  public boolean isOvercooked() {
    return contents.isOvercooked();
  }

  public int getMixtureBaseColor() {
    return contents.getMixtureBaseColor();
  }

  public boolean canExtractOneIngredient() {
    return contents.canExtractOneIngredient(getBlockState().getValue(SmallCauldronBlock.LIT));
  }

  @Override
  public boolean canExtractItem() {
    return canExtractOneIngredient();
  }

  public ItemStack extractOneIngredient() {
    if (!(level instanceof ServerLevel)) {
      return ItemStack.EMPTY;
    }
    ItemStack out = contents.extractOneIngredient();
    syncIfNeeded();
    return out;
  }

  @Override
  public ItemStack removeItem() {
    return extractOneIngredient();
  }

  public boolean canInsertOne(ItemStack stack) {
    return contents.canInsertOne(stack);
  }

  @Override
  public boolean canInsertItem(ItemStack stack) {
    return canInsertOne(stack);
  }

  @Override
  public boolean addItem(ItemStack stack) {
    if (stack.isEmpty() || !canInsertOne(stack)) {
      return false;
    }
    return insertOneIntoCauldron(stack.split(1));
  }

  public boolean insertOneIntoCauldron(ItemStack held) {
    if (!(level instanceof ServerLevel)) {
      return false;
    }
    boolean inserted = contents.insertOne(held);
    syncIfNeeded();
    return inserted;
  }

  public boolean canScoopMixtureWithRusticBottle() {
    return contents.canScoopMixtureWithRusticBottle();
  }

  public boolean tryScoopBottlePublic(Player player, InteractionHand hand, ItemStack held) {
    if (!(level instanceof ServerLevel server)) {
      return false;
    }
    boolean scooped =
        contents.tryScoopBottle(server, centerX(), topY(), centerZ(), player, hand, held);
    syncIfNeeded();
    return scooped;
  }

  public boolean isRusticBottle(ItemStack stack) {
    return contents.isRusticBottle(stack);
  }

  public boolean isLotusBlossom(ItemStack stack) {
    return contents.isLotusBlossom(stack);
  }

  public boolean isWaterContainer(ItemStack stack) {
    return contents.isWaterContainer(stack);
  }

  public boolean canCleanseSpoiledWithLotus() {
    return contents.canCleanseSpoiledWithLotus();
  }

  public boolean tryCleanseSpoiledPublic(Player player, InteractionHand hand, ItemStack held) {
    if (!(level instanceof ServerLevel)) {
      return false;
    }
    boolean cleansed = contents.tryCleanseSpoiled(player, hand, held);
    syncIfNeeded();
    return cleansed;
  }

  public boolean canUseWaterContainer(ItemStack stack) {
    return contents.canUseWaterContainer(stack);
  }

  public boolean tryFillWithWaterPublic(Player player, InteractionHand hand, ItemStack held) {
    if (!(level instanceof ServerLevel server)) {
      return false;
    }
    boolean filled =
        contents.tryUseWaterContainer(server, centerX(), centerY(), centerZ(), player, hand, held);
    syncIfNeeded();
    return filled;
  }

  public void dropAll(Level level) {
    if (level == null || level.isClientSide()) {
      return;
    }
    contents.dropAll(level, centerX(), centerY(), centerZ());
    contents.clearDirty();
    stirAnimDirty = false;
    setChanged();
  }

  private void applySpoiledAura(ServerLevel server, BlockPos pos) {
    if ((server.getGameTime() % SPOILED_AURA_INTERVAL_TICKS) != 0) {
      return;
    }
    for (Player player : server.getEntitiesOfClass(Player.class, new AABB(pos).inflate(1.0))) {
      player.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0, false, true, true));
    }
  }

  private void syncIfNeeded() {
    if (!stirAnimDirty && !contents.isDirty()) {
      return;
    }
    contents.clearDirty();
    stirAnimDirty = false;
    inventoryChanged();
  }

  private void inventoryChanged() {
    setChanged();
    if (level != null && !level.isClientSide()) {
      level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
  }

  private double centerX() {
    return worldPosition.getX() + 0.5;
  }

  private double centerY() {
    return worldPosition.getY() + 0.5;
  }

  private double centerZ() {
    return worldPosition.getZ() + 0.5;
  }

  private double topY() {
    return worldPosition.getY() + 1.0;
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putInt(TAG_STIR_ANIM_TICK, stirAnimTick);
    contents.save(tag, registries);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    int previousStirTick = stirAnimTick;
    stirAnimTick = Mth.clamp(tag.getInt(TAG_STIR_ANIM_TICK), 0, STIR_ANIM_TICKS);
    contents.load(tag, registries);

    Level level = getLevel();
    if (level != null && level.isClientSide()) {
      if (stirAnimTick > previousStirTick) {
        clientStirStartGameTime = level.getGameTime();
        clientStirStartTick = stirAnimTick;
      } else if (stirAnimTick <= 0) {
        clientStirStartTick = 0;
      }
    }
    stirAnimDirty = false;
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
