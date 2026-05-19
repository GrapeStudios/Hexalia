package net.astralya.hexalia.gameplay.smallcauldron;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.astralya.hexalia.HexaliaConfig;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.recipe.ModRecipeTypes;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.astralya.hexalia.recipe.SmallCauldronRecipeInput;
import net.astralya.hexalia.util.FireStarterHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class SmallCauldronContents {
  public static final int MAX_INGREDIENTS = 4;
  public static final int SERVINGS_PER_MIXTURE = 2;
  public static final int DEFAULT_REQUIRED_WATER_MB = 1000;
  public static final int DEFAULT_COOK_TIME_TICKS = 20 * 20;
  public static final int DEFAULT_OVERCOOK_AFTER_TICKS = 2 * 60 * 20;
  public static final int DEFAULT_CAPACITY_MB = 1000;
  public static final int BUCKET_MB = 1000;
  public static final int BOTTLE_MB = 250;

  private static final int WATER_RGB = 0x3F76E4;
  private static final int SPOILED_RGB = 0x3E5A3A;

  private static final String TAG_WATER_MB = "WaterMb";
  private static final String TAG_COOK_PROGRESS = "CookProgress";
  private static final String TAG_STIRS_DONE = "StirsDone";
  private static final String TAG_INGREDIENTS = "Ingredients";
  private static final String TAG_CONTENTS_KIND = "ContentsKind";
  private static final String TAG_SERVINGS = "Servings";
  private static final String TAG_MIX_COLOR = "MixtureColor";
  private static final String TAG_LOCKED_RECIPE = "LockedRecipe";
  private static final String TAG_MIXTURE_AGE = "MixtureAge";
  private static final String TAG_OVERCOOKED = "Overcooked";
  private static final String TAG_MIXTURE_RESULT = "MixtureResult";

  private final NonNullList<ItemStack> ingredients =
      NonNullList.withSize(MAX_INGREDIENTS, ItemStack.EMPTY);
  private final List<ItemStack> ingredientsView = Collections.unmodifiableList(ingredients);

  private int waterMb;
  private int ingredientCount;
  private int stirsDone;
  private int cookProgress;
  private int mixtureAgeTicks;
  private boolean overcooked;
  private ContentsKind kind = ContentsKind.EMPTY;
  private int servings;
  private int mixtureColor;
  private ItemStack mixtureResult = ItemStack.EMPTY;
  private @Nullable String lockedRecipeId;
  private boolean dirty;

  public enum ContentsKind {
    EMPTY,
    WATER,
    COOKING,
    MIXTURE,
    SPOILED
  }

  public boolean isDirty() {
    return dirty;
  }

  public void clearDirty() {
    dirty = false;
  }

  public boolean isSpoiled() {
    return kind == ContentsKind.SPOILED;
  }

  public boolean isCooking() {
    return kind == ContentsKind.COOKING;
  }

  public boolean hasMixture() {
    return kind == ContentsKind.MIXTURE;
  }

  public boolean isOvercooked() {
    return kind == ContentsKind.MIXTURE && overcooked;
  }

  public int getMixtureBaseColor() {
    return mixtureColor != 0 ? mixtureColor : WATER_RGB;
  }

  public int getVisualLiquidColor() {
    if (kind == ContentsKind.SPOILED) {
      return SPOILED_RGB;
    }
    if (kind == ContentsKind.MIXTURE) {
      return overcooked ? darkenRgb(getMixtureBaseColor(), 0.55F) : getMixtureBaseColor();
    }
    if (kind == ContentsKind.WATER || kind == ContentsKind.COOKING) {
      return WATER_RGB;
    }
    return 0;
  }

  public float getLiquidFill01() {
    return waterMb / (float) DEFAULT_CAPACITY_MB;
  }

  public float getVisualLiquidFill01() {
    if (kind == ContentsKind.MIXTURE) {
      return overcooked || servings <= 1 ? 0.5F : 1.0F;
    }
    if (kind == ContentsKind.COOKING) {
      return 1.0F;
    }
    return getLiquidFill01();
  }

  public List<ItemStack> getIngredientsForRender() {
    return ingredientsView;
  }

  public ItemStack getIngredient(int slot) {
    return slot >= 0 && slot < MAX_INGREDIENTS ? ingredients.get(slot) : ItemStack.EMPTY;
  }

  public void setIngredient(int slot, ItemStack stack) {
    if (slot < 0 || slot >= MAX_INGREDIENTS) {
      return;
    }
    ItemStack previous = ingredients.get(slot);
    if (previous.isEmpty() && !stack.isEmpty()) {
      ingredientCount++;
    } else if (!previous.isEmpty() && stack.isEmpty()) {
      ingredientCount = Math.max(0, ingredientCount - 1);
    }
    ingredients.set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
    stirsDone = Math.min(stirsDone, ingredientCount);
    if (kind == ContentsKind.EMPTY && ingredientCount > 0 && waterMb > 0) {
      kind = ContentsKind.WATER;
    }
    markDirty();
  }

  public boolean canExtractOneIngredient(boolean blockLit) {
    return (kind == ContentsKind.WATER || kind == ContentsKind.EMPTY)
        && cookProgress == 0
        && stirsDone == 0
        && ingredientCount > 0;
  }

  public ItemStack extractOneIngredient() {
    int slot = lastFilledIngredientSlot();
    if (slot == -1) {
      return ItemStack.EMPTY;
    }
    ItemStack extracted = ingredients.get(slot);
    ingredients.set(slot, ItemStack.EMPTY);
    ingredientCount = Math.max(0, ingredientCount - 1);
    markDirty();
    return extracted;
  }

  public boolean canInsertOne(ItemStack stack) {
    if (stack.isEmpty()
        || FireStarterHelper.isFireStarter(stack)
        || isRusticBottle(stack)
        || isLotusBlossom(stack)
        || isWaterContainer(stack)) {
      return false;
    }
    return (kind == ContentsKind.WATER || kind == ContentsKind.EMPTY)
        && firstEmptyIngredientSlot() != -1;
  }

  public boolean insertOne(ItemStack held) {
    if (!canInsertOne(held)) {
      return false;
    }
    if (kind == ContentsKind.EMPTY && waterMb > 0) {
      kind = ContentsKind.WATER;
    }
    int slot = firstEmptyIngredientSlot();
    ItemStack one = held.copyWithCount(1);
    ingredients.set(slot, one);
    ingredientCount++;
    stirsDone = Math.min(stirsDone, ingredientCount);
    markDirty();
    return true;
  }

  public boolean canStir(boolean blockLit) {
    return kind == ContentsKind.WATER
        && blockLit
        && ingredientCount > 0
        && cookProgress == 0
        && stirsDone < ingredientCount;
  }

  public StirResult stir(ServerLevel level) {
    stirsDone++;
    markDirty();
    if (stirsDone >= ingredientCount) {
      return tryStartCooking(level) ? StirResult.STARTED_COOKING : StirResult.STIRRED;
    }
    return StirResult.STIRRED;
  }

  public enum StirResult {
    STIRRED,
    STARTED_COOKING
  }

  public boolean canScoopMixtureWithRusticBottle() {
    return kind == ContentsKind.MIXTURE && servings > 0 && !mixtureResult.isEmpty();
  }

  public boolean tryScoopBottle(
      ServerLevel level,
      double x,
      double y,
      double z,
      Player player,
      InteractionHand hand,
      ItemStack held) {
    if (!canScoopMixtureWithRusticBottle() || !isRusticBottle(held)) {
      return false;
    }
    ItemStack resultBottle = mixtureResult.copy();
    if (!player.getAbilities().instabuild) {
      held.shrink(1);
    }
    if (!player.getInventory().add(resultBottle)) {
      level.addFreshEntity(new ItemEntity(level, x, y, z, resultBottle));
    }
    servings--;
    if (servings <= 0) {
      resetToEmpty();
    }
    markDirty();
    return true;
  }

  public boolean canCleanseSpoiledWithLotus() {
    return kind == ContentsKind.SPOILED;
  }

  public boolean tryCleanseSpoiled(Player player, InteractionHand hand, ItemStack held) {
    if (!canCleanseSpoiledWithLotus() || !isLotusBlossom(held)) {
      return false;
    }
    if (!player.getAbilities().instabuild) {
      held.shrink(1);
    }
    resetToEmpty();
    markDirty();
    return true;
  }

  public boolean canUseWaterContainer(ItemStack stack) {
    if (kind == ContentsKind.COOKING
        || kind == ContentsKind.MIXTURE
        || kind == ContentsKind.SPOILED) {
      return false;
    }
    if (stack.is(Items.WATER_BUCKET)) {
      return waterMb + BUCKET_MB <= DEFAULT_CAPACITY_MB;
    }
    if (stack.is(Items.BUCKET)) {
      return waterMb >= BUCKET_MB;
    }
    return stack.is(Items.GLASS_BOTTLE) && waterMb >= BOTTLE_MB;
  }

  public boolean tryUseWaterContainer(
      ServerLevel level,
      double x,
      double y,
      double z,
      Player player,
      InteractionHand hand,
      ItemStack held) {
    if (!canUseWaterContainer(held)) {
      return false;
    }
    if (held.is(Items.WATER_BUCKET)) {
      fillWater(BUCKET_MB);
      if (!player.getAbilities().instabuild) {
        player.setItemInHand(hand, new ItemStack(Items.BUCKET));
      }
      level.playSound(null, x, y, z, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
      return true;
    }
    if (held.is(Items.BUCKET)) {
      drainWater(BUCKET_MB);
      if (!player.getAbilities().instabuild) {
        player.setItemInHand(hand, new ItemStack(Items.WATER_BUCKET));
      }
      level.playSound(null, x, y, z, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
      return true;
    }
    drainWater(BOTTLE_MB);
    if (!player.getAbilities().instabuild) {
      held.shrink(1);
      ItemStack waterBottle = createWaterBottle();
      if (!player.getInventory().add(waterBottle)) {
        player.drop(waterBottle, false);
      }
    }
    level.playSound(null, x, y, z, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
    return true;
  }

  public void tickServer(ServerLevel level, boolean blockLit) {
    if (kind == ContentsKind.COOKING) {
      if (!blockLit) {
        return;
      }
      cookProgress++;
      if (cookProgress >= HexaliaConfig.brewingDuration()) {
        if (!finalizeCook(level)) {
          makeSpoiled();
        }
        markDirty();
      } else if ((cookProgress & 3) == 0) {
        markDirty();
      }
    } else if (kind == ContentsKind.MIXTURE && blockLit) {
      mixtureAgeTicks++;
      if (!overcooked && mixtureAgeTicks >= HexaliaConfig.overcookedDuration()) {
        overcooked = true;
        servings = Math.min(servings, 1);
        markDirty();
      }
    }
  }

  public void dropAll(Level level, double x, double y, double z) {
    for (ItemStack stack : ingredients) {
      if (!stack.isEmpty()) {
        Containers.dropItemStack(level, x, y, z, stack);
      }
    }
    resetToEmpty();
    markDirty();
  }

  public void save(CompoundTag tag, HolderLookup.Provider registries) {
    tag.putInt(TAG_WATER_MB, waterMb);
    tag.putInt(TAG_COOK_PROGRESS, cookProgress);
    tag.putInt(TAG_STIRS_DONE, stirsDone);
    tag.putInt(TAG_MIXTURE_AGE, mixtureAgeTicks);
    tag.putBoolean(TAG_OVERCOOKED, overcooked);
    tag.putInt(TAG_CONTENTS_KIND, kind.ordinal());
    tag.putInt(TAG_SERVINGS, servings);
    tag.putInt(TAG_MIX_COLOR, mixtureColor);
    if (lockedRecipeId != null) {
      tag.putString(TAG_LOCKED_RECIPE, lockedRecipeId);
    }
    ListTag list = new ListTag();
    for (int index = 0; index < MAX_INGREDIENTS; index++) {
      ItemStack stack = ingredients.get(index);
      if (stack.isEmpty()) {
        continue;
      }
      CompoundTag entry = new CompoundTag();
      entry.putInt("Slot", index);
      entry.put("Stack", stack.save(registries));
      list.add(entry);
    }
    tag.put(TAG_INGREDIENTS, list);
    if (!mixtureResult.isEmpty()) {
      tag.put(TAG_MIXTURE_RESULT, mixtureResult.save(registries));
    }
  }

  public void load(CompoundTag tag, HolderLookup.Provider registries) {
    waterMb = clamp(tag.getInt(TAG_WATER_MB), 0, DEFAULT_CAPACITY_MB);
    cookProgress = Math.max(0, tag.getInt(TAG_COOK_PROGRESS));
    stirsDone = Math.max(0, tag.getInt(TAG_STIRS_DONE));
    mixtureAgeTicks = Math.max(0, tag.getInt(TAG_MIXTURE_AGE));
    overcooked = tag.getBoolean(TAG_OVERCOOKED);
    ContentsKind[] values = ContentsKind.values();
    int kindId = tag.getInt(TAG_CONTENTS_KIND);
    kind = kindId >= 0 && kindId < values.length ? values[kindId] : ContentsKind.EMPTY;
    servings = Math.max(0, tag.getInt(TAG_SERVINGS));
    mixtureColor = tag.getInt(TAG_MIX_COLOR);
    lockedRecipeId =
        tag.contains(TAG_LOCKED_RECIPE, Tag.TAG_STRING) ? tag.getString(TAG_LOCKED_RECIPE) : null;
    mixtureResult =
        tag.contains(TAG_MIXTURE_RESULT, Tag.TAG_COMPOUND)
            ? ItemStack.parseOptional(registries, tag.getCompound(TAG_MIXTURE_RESULT))
            : ItemStack.EMPTY;
    clearIngredientsAndCount();
    if (tag.contains(TAG_INGREDIENTS, Tag.TAG_LIST)) {
      ListTag list = tag.getList(TAG_INGREDIENTS, Tag.TAG_COMPOUND);
      for (int index = 0; index < list.size(); index++) {
        CompoundTag entry = list.getCompound(index);
        int slot = entry.getInt("Slot");
        if (slot < 0 || slot >= MAX_INGREDIENTS || !entry.contains("Stack", Tag.TAG_COMPOUND)) {
          continue;
        }
        ItemStack stack = ItemStack.parseOptional(registries, entry.getCompound("Stack"));
        if (!stack.isEmpty()) {
          ingredients.set(slot, stack);
          ingredientCount++;
        }
      }
    }
    dirty = false;
  }

  private boolean tryStartCooking(ServerLevel level) {
    if (kind != ContentsKind.WATER || waterMb < DEFAULT_REQUIRED_WATER_MB || ingredientCount <= 0) {
      return false;
    }
    Optional<RecipeHolder<SmallCauldronRecipe>> match = findRecipeMatch(level);
    lockedRecipeId = match.map(holder -> holder.id().toString()).orElse(null);
    mixtureResult =
        match
            .map(holder -> holder.value().getResultItem(level.registryAccess()).copy())
            .orElse(ItemStack.EMPTY);
    clearIngredientsAndCount();
    stirsDone = 0;
    cookProgress = 0;
    kind = ContentsKind.COOKING;
    markDirty();
    return true;
  }

  private boolean finalizeCook(ServerLevel level) {
    Optional<RecipeHolder<SmallCauldronRecipe>> locked = findLockedRecipe(level);
    if (locked.isEmpty()) {
      return false;
    }
    if (mixtureResult.isEmpty()) {
      mixtureResult = locked.get().value().getResultItem(level.registryAccess()).copy();
    }
    drainWater(DEFAULT_REQUIRED_WATER_MB);
    kind = ContentsKind.MIXTURE;
    servings = SERVINGS_PER_MIXTURE;
    mixtureColor = WATER_RGB;
    cookProgress = 0;
    mixtureAgeTicks = 0;
    overcooked = false;
    return true;
  }

  private void makeSpoiled() {
    clearIngredientsAndCount();
    stirsDone = 0;
    kind = ContentsKind.SPOILED;
    servings = 0;
    mixtureColor = 0;
    cookProgress = 0;
    mixtureAgeTicks = 0;
    overcooked = false;
    lockedRecipeId = null;
    mixtureResult = ItemStack.EMPTY;
  }

  private Optional<RecipeHolder<SmallCauldronRecipe>> findRecipeMatch(ServerLevel level) {
    return level
        .getRecipeManager()
        .getRecipeFor(
            ModRecipeTypes.SMALL_CAULDRON.get(),
            new SmallCauldronRecipeInput(getIngredientListForRecipe()),
            level);
  }

  private Optional<RecipeHolder<SmallCauldronRecipe>> findLockedRecipe(ServerLevel level) {
    if (lockedRecipeId == null) {
      return Optional.empty();
    }
    ResourceLocation key = ResourceLocation.tryParse(lockedRecipeId);
    if (key == null) {
      return Optional.empty();
    }
    Optional<? extends RecipeHolder<?>> recipe = level.getRecipeManager().byKey(key);
    if (recipe.isEmpty() || !(recipe.get().value() instanceof SmallCauldronRecipe)) {
      return Optional.empty();
    }
    @SuppressWarnings("unchecked")
    RecipeHolder<SmallCauldronRecipe> typed = (RecipeHolder<SmallCauldronRecipe>) recipe.get();
    return Optional.of(typed);
  }

  private List<ItemStack> getIngredientListForRecipe() {
    List<ItemStack> list = new ArrayList<>(ingredientCount);
    for (ItemStack stack : ingredients) {
      if (!stack.isEmpty()) {
        list.add(stack);
      }
    }
    return list;
  }

  private int firstEmptyIngredientSlot() {
    for (int index = 0; index < MAX_INGREDIENTS; index++) {
      if (ingredients.get(index).isEmpty()) {
        return index;
      }
    }
    return -1;
  }

  private int lastFilledIngredientSlot() {
    for (int index = MAX_INGREDIENTS - 1; index >= 0; index--) {
      if (!ingredients.get(index).isEmpty()) {
        return index;
      }
    }
    return -1;
  }

  private int fillWater(int amountMb) {
    int accepted = Math.min(DEFAULT_CAPACITY_MB - waterMb, amountMb);
    if (accepted > 0) {
      waterMb += accepted;
      if (kind == ContentsKind.EMPTY) {
        kind = ContentsKind.WATER;
      }
      markDirty();
    }
    return accepted;
  }

  private int drainWater(int amountMb) {
    int removed = Math.min(waterMb, amountMb);
    if (removed > 0) {
      waterMb -= removed;
      if (waterMb == 0 && ingredientCount == 0 && kind == ContentsKind.WATER) {
        kind = ContentsKind.EMPTY;
      }
      markDirty();
    }
    return removed;
  }

  public boolean isRusticBottle(ItemStack stack) {
    return stack.is(ModItems.RUSTIC_BOTTLE.get());
  }

  public boolean isLotusBlossom(ItemStack stack) {
    return stack.is(ModItems.LOTUS_BLOSSOM.get());
  }

  public boolean isWaterContainer(ItemStack stack) {
    return stack.is(Items.WATER_BUCKET) || stack.is(Items.BUCKET) || stack.is(Items.GLASS_BOTTLE);
  }

  private static ItemStack createWaterBottle() {
    ItemStack bottle = new ItemStack(Items.POTION);
    bottle.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(Potions.WATER));
    return bottle;
  }

  public void resetToEmpty() {
    clearIngredientsAndCount();
    stirsDone = 0;
    cookProgress = 0;
    lockedRecipeId = null;
    servings = 0;
    mixtureColor = 0;
    mixtureAgeTicks = 0;
    overcooked = false;
    mixtureResult = ItemStack.EMPTY;
    kind = ContentsKind.EMPTY;
    waterMb = 0;
  }

  private void clearIngredientsAndCount() {
    for (int index = 0; index < MAX_INGREDIENTS; index++) {
      ingredients.set(index, ItemStack.EMPTY);
    }
    ingredientCount = 0;
  }

  private void markDirty() {
    dirty = true;
  }

  private static int darkenRgb(int rgb, float factor) {
    int r = clamp((int) (((rgb >> 16) & 0xFF) * factor), 0, 255);
    int g = clamp((int) (((rgb >> 8) & 0xFF) * factor), 0, 255);
    int b = clamp((int) ((rgb & 0xFF) * factor), 0, 255);
    return (r << 16) | (g << 8) | b;
  }

  private static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }
}
