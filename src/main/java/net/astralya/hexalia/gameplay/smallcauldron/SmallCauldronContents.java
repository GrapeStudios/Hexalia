package net.astralya.hexalia.gameplay.smallcauldron;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.BrewItem;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.astralya.hexalia.recipe.SmallCauldronRecipeInput;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class SmallCauldronContents {

    public static final int MAX_INGREDIENTS = 4;
    public static final int SERVINGS_PER_MIXTURE = 2;

    public static final int DEFAULT_REQUIRED_WATER_MB = 1000;
    public static final int DEFAULT_COOK_TIME_TICKS = Configuration.BREWING_DURATION.get();
    public static final int DEFAULT_OVERCOOK_AFTER_TICKS = Configuration.OVERCOOKED_DURATION.get();

    public static final int DEFAULT_CAPACITY_MB = 1000;

    public static final int BUCKET_MB = 1000;
    public static final int BOTTLE_MB = 250;

    public static final float VISUAL_FILL_FULL = 1.0F;
    public static final float VISUAL_FILL_LOW = 0.5F;

    private static final int WATER_RGB = 0x3F76E4;
    private static final int SPOILED_RGB = 0x3E5A3A;

    private static final int COOK_SYNC_INTERVAL_MASK = 0x03;
    private static final int MIXTURE_SYNC_INTERVAL_MASK = 0x1F;

    private static final String TAG_WATER_MB = "WaterMb";
    private static final String TAG_REQUIRED_WATER_MB = "RequiredWaterMb";
    private static final String TAG_CAPACITY_MB = "CapacityMb";
    private static final String TAG_COOK_TIME_TICKS = "CookTimeTicks";
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

    public enum ContentsKind {
        EMPTY,
        WATER,
        COOKING,
        MIXTURE,
        SPOILED
    }

    private int waterMb;
    private int requiredWaterMb = DEFAULT_REQUIRED_WATER_MB;
    private int capacityMb = DEFAULT_CAPACITY_MB;

    private final NonNullList<ItemStack> ingredients = NonNullList.withSize(MAX_INGREDIENTS, ItemStack.EMPTY);
    private final List<ItemStack> ingredientsView = Collections.unmodifiableList(ingredients);

    private int ingredientCount;

    private int stirsDone;
    private int cookTimeTicks = DEFAULT_COOK_TIME_TICKS;
    private int cookProgress;

    private int mixtureAgeTicks;
    private boolean overcooked;

    private ContentsKind kind = ContentsKind.EMPTY;

    private int servings;
    private int mixtureColor;

    private ItemStack mixtureResult = ItemStack.EMPTY;

    @Nullable
    private String lockedRecipeId;

    private boolean dirty;

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        dirty = false;
    }

    public ContentsKind kind() {
        return kind;
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
        if (kind == ContentsKind.SPOILED) return SPOILED_RGB;
        if (kind == ContentsKind.MIXTURE) return overcooked ? darkenRgb(getMixtureBaseColor(), 0.55f) : getMixtureBaseColor();
        if (kind == ContentsKind.WATER || kind == ContentsKind.COOKING) return WATER_RGB;
        return 0;
    }

    public float getLiquidFill01() {
        if (capacityMb <= 0) return 0.0F;
        if (kind == ContentsKind.COOKING || kind == ContentsKind.MIXTURE) return 1.0F;
        float f = (float) waterMb / (float) capacityMb;
        if (f < 0.0F) return 0.0F;
        if (f > 1.0F) return 1.0F;
        return f;
    }

    public float getVisualLiquidFill01() {
        if (kind == ContentsKind.MIXTURE) {
            if (overcooked) return VISUAL_FILL_LOW;
            return servings <= 1 ? VISUAL_FILL_LOW : VISUAL_FILL_FULL;
        }
        if (kind == ContentsKind.COOKING) return VISUAL_FILL_FULL;
        return getLiquidFill01();
    }

    public List<ItemStack> getIngredientsForRender() {
        return ingredientsView;
    }

    public boolean hasAnyIngredients() {
        return ingredientCount > 0;
    }

    public boolean canExtractOneIngredient(boolean blockLit) {
        if (kind != ContentsKind.WATER && kind != ContentsKind.EMPTY) return false;
        if (cookProgress > 0 || stirsDone > 0) return false;
        return ingredientCount > 0;
    }

    public ItemStack extractOneIngredient() {
        int slot = lastFilledIngredientSlot();
        if (slot == -1) return ItemStack.EMPTY;

        ItemStack extracted = ingredients.get(slot);
        ingredients.set(slot, ItemStack.EMPTY);

        if (!extracted.isEmpty()) {
            ingredientCount--;
            if (ingredientCount < 0) ingredientCount = 0;
        }

        markDirty();
        return extracted;
    }

    public boolean canInsertOne(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.is(Items.FLINT_AND_STEEL)) return false;
        if (isRusticBottle(stack)) return false;
        if (isLotusBlossom(stack)) return false;
        if (isWaterContainer(stack)) return false;
        if (kind != ContentsKind.WATER && kind != ContentsKind.EMPTY) return false;
        return firstEmptyIngredientSlot() != -1;
    }

    public boolean insertOne(ItemStack held) {
        if (!canInsertOne(held)) return false;

        if (kind == ContentsKind.EMPTY && waterMb > 0) {
            kind = ContentsKind.WATER;
        }

        int slot = firstEmptyIngredientSlot();
        if (slot == -1) return false;

        ItemStack one = held.copy();
        one.setCount(1);
        ingredients.set(slot, one);
        ingredientCount++;

        if (stirsDone > ingredientCount) {
            stirsDone = ingredientCount;
        }

        markDirty();
        return true;
    }

    public boolean canStir(boolean blockLit) {
        if (kind != ContentsKind.WATER) return false;
        if (!blockLit) return false;
        if (ingredientCount <= 0) return false;
        if (cookProgress > 0) return false;
        return stirsDone < ingredientCount;
    }

    public StirResult stir(ServerLevel level) {
        stirsDone++;
        markDirty();

        if (stirsDone >= ingredientCount) {
            boolean started = tryStartCooking(level);
            return started ? StirResult.STARTED_COOKING : StirResult.STIRRED;
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

    public boolean tryScoopBottle(ServerLevel level, double x, double y, double z, Player player, InteractionHand hand, ItemStack held) {
        if (!canScoopMixtureWithRusticBottle()) return false;
        if (!isRusticBottle(held)) return false;

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
        } else if (overcooked && servings > 1) {
            servings = 1;
        }

        markDirty();
        return true;
    }

    public boolean canCleanseSpoiledWithLotus() {
        return kind == ContentsKind.SPOILED;
    }

    public boolean tryCleanseSpoiled(Player player, InteractionHand hand, ItemStack held) {
        if (kind != ContentsKind.SPOILED) return false;
        if (!isLotusBlossom(held)) return false;

        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }

        resetToEmpty();
        markDirty();
        return true;
    }

    public boolean canUseWaterContainer(ItemStack stack) {
        if (kind == ContentsKind.COOKING || kind == ContentsKind.MIXTURE || kind == ContentsKind.SPOILED) return false;

        if (stack.is(Items.WATER_BUCKET)) return waterMb + BUCKET_MB <= capacityMb;
        if (stack.is(Items.BUCKET)) return waterMb >= BUCKET_MB;
        if (stack.is(Items.GLASS_BOTTLE)) return waterMb >= BOTTLE_MB;

        return false;
    }

    public boolean tryUseWaterContainer(ServerLevel level, double x, double y, double z, Player player, InteractionHand hand, ItemStack held) {
        if (kind == ContentsKind.COOKING || kind == ContentsKind.MIXTURE || kind == ContentsKind.SPOILED) return false;

        if (held.is(Items.WATER_BUCKET)) {
            int filled = fillWater(BUCKET_MB);
            if (filled == BUCKET_MB) {
                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                }
                level.playSound(null, x, y, z, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                return true;
            }
            return false;
        }

        if (held.is(Items.BUCKET)) {
            int drained = drainWater(BUCKET_MB);
            if (drained == BUCKET_MB) {
                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(hand, new ItemStack(Items.WATER_BUCKET));
                }
                level.playSound(null, x, y, z, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                return true;
            }
            return false;
        }

        if (!held.is(Items.GLASS_BOTTLE)) {
            return false;
        }

        int drained = drainWater(BOTTLE_MB);
        if (drained == BOTTLE_MB) {
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

        return false;
    }

    public TickResult tickServer(ServerLevel level, boolean blockLit) {
        if (kind == ContentsKind.COOKING) {
            if (!blockLit) return TickResult.NONE;

            cookProgress++;

            if (cookProgress >= cookTimeTicks) {
                if (!finalizeCook(level)) {
                    makeSpoiled(level);
                }
                markDirty();
                return TickResult.STATE_CHANGED;
            }

            if ((cookProgress & COOK_SYNC_INTERVAL_MASK) == 0) {
                markDirty();
                return TickResult.PROGRESSED;
            }

            return TickResult.NONE;
        }

        if (kind == ContentsKind.MIXTURE) {
            if (!blockLit) return TickResult.NONE;

            mixtureAgeTicks++;

            if (!overcooked && mixtureAgeTicks >= DEFAULT_OVERCOOK_AFTER_TICKS) {
                overcooked = true;
                if (servings > 1) servings = 1;
                markDirty();
                return TickResult.STATE_CHANGED;
            }

            if ((mixtureAgeTicks & MIXTURE_SYNC_INTERVAL_MASK) == 0) {
                markDirty();
                return TickResult.PROGRESSED;
            }

            return TickResult.NONE;
        }

        return TickResult.NONE;
    }

    public enum TickResult {
        NONE,
        PROGRESSED,
        STATE_CHANGED
    }

    public void dropAll(Level level, double x, double y, double z) {
        for (ItemStack s : ingredients) {
            if (!s.isEmpty()) {
                Containers.dropItemStack(level, x, y, z, s);
            }
        }
        resetToEmpty();
        markDirty();
    }

    public void save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt(TAG_WATER_MB, waterMb);
        tag.putInt(TAG_REQUIRED_WATER_MB, requiredWaterMb);
        tag.putInt(TAG_CAPACITY_MB, capacityMb);

        tag.putInt(TAG_COOK_TIME_TICKS, cookTimeTicks);
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
        for (int i = 0; i < MAX_INGREDIENTS; i++) {
            ItemStack stack = ingredients.get(i);
            if (stack.isEmpty()) continue;

            CompoundTag entry = new CompoundTag();
            entry.putInt("Slot", i);

            CompoundTag stackTagIn = new CompoundTag();
            Tag saved = stack.save(registries, stackTagIn);
            CompoundTag stackTagOut = saved instanceof CompoundTag ct ? ct : stackTagIn;

            entry.put("Stack", stackTagOut);
            list.add(entry);
        }
        tag.put(TAG_INGREDIENTS, list);

        if (!mixtureResult.isEmpty()) {
            CompoundTag rIn = new CompoundTag();
            Tag saved = mixtureResult.save(registries, rIn);
            CompoundTag rOut = saved instanceof CompoundTag ct ? ct : rIn;
            tag.put(TAG_MIXTURE_RESULT, rOut);
        }
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        capacityMb = tag.contains(TAG_CAPACITY_MB) ? tag.getInt(TAG_CAPACITY_MB) : DEFAULT_CAPACITY_MB;
        if (capacityMb <= 0) capacityMb = DEFAULT_CAPACITY_MB;

        waterMb = tag.getInt(TAG_WATER_MB);
        if (waterMb < 0) waterMb = 0;
        if (waterMb > capacityMb) waterMb = capacityMb;

        requiredWaterMb = tag.contains(TAG_REQUIRED_WATER_MB) ? tag.getInt(TAG_REQUIRED_WATER_MB) : DEFAULT_REQUIRED_WATER_MB;
        if (requiredWaterMb <= 0) requiredWaterMb = DEFAULT_REQUIRED_WATER_MB;
        if (requiredWaterMb > capacityMb) requiredWaterMb = capacityMb;

        cookTimeTicks = tag.contains(TAG_COOK_TIME_TICKS) ? tag.getInt(TAG_COOK_TIME_TICKS) : DEFAULT_COOK_TIME_TICKS;
        if (cookTimeTicks < 0) cookTimeTicks = DEFAULT_COOK_TIME_TICKS;

        cookProgress = tag.getInt(TAG_COOK_PROGRESS);
        if (cookProgress < 0) cookProgress = 0;

        stirsDone = tag.getInt(TAG_STIRS_DONE);
        if (stirsDone < 0) stirsDone = 0;

        mixtureAgeTicks = tag.getInt(TAG_MIXTURE_AGE);
        if (mixtureAgeTicks < 0) mixtureAgeTicks = 0;

        overcooked = tag.getBoolean(TAG_OVERCOOKED);

        int kindId = tag.getInt(TAG_CONTENTS_KIND);
        ContentsKind[] values = ContentsKind.values();
        kind = kindId >= 0 && kindId < values.length ? values[kindId] : ContentsKind.EMPTY;

        servings = tag.getInt(TAG_SERVINGS);
        if (servings < 0) servings = 0;

        mixtureColor = tag.getInt(TAG_MIX_COLOR);

        mixtureResult = ItemStack.EMPTY;
        if (tag.contains(TAG_MIXTURE_RESULT, Tag.TAG_COMPOUND)) {
            mixtureResult = ItemStack.parseOptional(registries, tag.getCompound(TAG_MIXTURE_RESULT));
        }

        lockedRecipeId = tag.contains(TAG_LOCKED_RECIPE, Tag.TAG_STRING) ? tag.getString(TAG_LOCKED_RECIPE) : null;

        clearIngredientsAndCount();

        if (tag.contains(TAG_INGREDIENTS, Tag.TAG_LIST)) {
            ListTag list = tag.getList(TAG_INGREDIENTS, Tag.TAG_COMPOUND);
            loadIngredientsLegacyAware(list, registries);
        }

        if (kind == ContentsKind.MIXTURE && overcooked && servings > 1) {
            servings = 1;
        }

        dirty = false;
    }

    private void loadIngredientsLegacyAware(ListTag list, HolderLookup.Provider registries) {
        boolean anyNested = false;
        boolean anySlot = false;

        for (int i = 0; i < list.size(); i++) {
            CompoundTag e = list.getCompound(i);
            if (e.contains("Slot", Tag.TAG_INT)) anySlot = true;
            if (e.contains("Slot", Tag.TAG_INT) && e.contains("Stack", Tag.TAG_COMPOUND)) {
                anyNested = true;
                break;
            }
        }

        if (anyNested) {
            for (int i = 0; i < list.size(); i++) {
                CompoundTag e = list.getCompound(i);
                int slot = e.contains("Slot", Tag.TAG_INT) ? e.getInt("Slot") : -1;
                if (slot < 0 || slot >= MAX_INGREDIENTS) continue;
                if (!e.contains("Stack", Tag.TAG_COMPOUND)) continue;

                ItemStack stack = ItemStack.parseOptional(registries, e.getCompound("Stack"));
                if (!stack.isEmpty()) {
                    ingredients.set(slot, stack);
                    ingredientCount++;
                }
            }
            return;
        }

        if (anySlot) {
            for (int i = 0; i < list.size(); i++) {
                CompoundTag e = list.getCompound(i);
                int slot = e.contains("Slot", Tag.TAG_INT) ? e.getInt("Slot") : -1;
                if (slot < 0 || slot >= MAX_INGREDIENTS) continue;

                ItemStack stack = ItemStack.parseOptional(registries, e);
                if (!stack.isEmpty()) {
                    ingredients.set(slot, stack);
                    ingredientCount++;
                }
            }
            return;
        }

        int idx = 0;
        for (int i = 0; i < list.size() && idx < MAX_INGREDIENTS; i++) {
            CompoundTag e = list.getCompound(i);
            ItemStack stack = ItemStack.parseOptional(registries, e);
            if (stack.isEmpty()) continue;
            ingredients.set(idx, stack);
            ingredientCount++;
            idx++;
        }
    }

    private boolean tryStartCooking(ServerLevel level) {
        if (kind != ContentsKind.WATER) return false;
        if (waterMb < requiredWaterMb) return false;
        if (ingredientCount <= 0) return false;
        if (cookProgress > 0) return false;

        Optional<RecipeHolder<SmallCauldronRecipe>> match = findRecipeMatch(level);
        if (match.isPresent()) {
            lockedRecipeId = match.get().id().toString();
            mixtureResult = match.get().value().getResultItem(level.registryAccess()).copy();
        } else {
            lockedRecipeId = null;
            mixtureResult = ItemStack.EMPTY;
        }

        clearIngredientsAndCount();
        stirsDone = 0;

        kind = ContentsKind.COOKING;
        cookProgress = 0;

        markDirty();
        return true;
    }

    private boolean finalizeCook(ServerLevel level) {
        Optional<RecipeHolder<SmallCauldronRecipe>> locked = findLockedRecipe(level);
        if (locked.isEmpty()) {
            return false;
        }

        SmallCauldronRecipe recipe = locked.get().value();

        if (mixtureResult.isEmpty()) {
            ItemStack result = recipe.getResultItem(level.registryAccess());
            mixtureResult = result.isEmpty() ? ItemStack.EMPTY : result.copy();
        }

        drainWater(requiredWaterMb);

        kind = ContentsKind.MIXTURE;
        servings = SERVINGS_PER_MIXTURE;

        mixtureColor = getRecipeMixtureColor(level, recipe);

        cookProgress = 0;
        mixtureAgeTicks = 0;
        overcooked = false;

        return true;
    }

    private void makeSpoiled(ServerLevel level) {
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
        SmallCauldronRecipeInput input = new SmallCauldronRecipeInput(getIngredientListForRecipe());
        return level.getRecipeManager().getRecipeFor(ModRecipes.SMALL_CAULDRON_TYPE.get(), input, level);
    }

    private Optional<RecipeHolder<SmallCauldronRecipe>> findLockedRecipe(ServerLevel level) {
        if (lockedRecipeId == null) return Optional.empty();

        ResourceLocation key = ResourceLocation.tryParse(lockedRecipeId);
        if (key == null) return Optional.empty();

        Optional<? extends RecipeHolder<?>> any = level.getRecipeManager().byKey(key);
        if (any.isEmpty()) return Optional.empty();

        RecipeHolder<?> holder = any.get();
        if (!(holder.value() instanceof SmallCauldronRecipe)) return Optional.empty();

        @SuppressWarnings("unchecked")
        RecipeHolder<SmallCauldronRecipe> typed = (RecipeHolder<SmallCauldronRecipe>) holder;

        return Optional.of(typed);
    }

    private int getRecipeMixtureColor(ServerLevel level, SmallCauldronRecipe recipe) {
        ItemStack out = recipe.getResultItem(level.registryAccess());
        if (out.isEmpty()) return WATER_RGB;

        if (out.getItem() instanceof BrewItem brewItem) {
            int c = brewItem.getBrewColor();
            return c != 0 ? c : WATER_RGB;
        }

        return WATER_RGB;
    }

    private int firstEmptyIngredientSlot() {
        for (int i = 0; i < MAX_INGREDIENTS; i++) {
            if (ingredients.get(i).isEmpty()) return i;
        }
        return -1;
    }

    private int lastFilledIngredientSlot() {
        for (int i = MAX_INGREDIENTS - 1; i >= 0; i--) {
            if (!ingredients.get(i).isEmpty()) return i;
        }
        return -1;
    }

    private List<ItemStack> getIngredientListForRecipe() {
        if (ingredientCount <= 0) return List.of();

        List<ItemStack> list = new ArrayList<>(ingredientCount);
        for (ItemStack s : ingredients) {
            if (!s.isEmpty()) list.add(s);
        }
        return list;
    }

    private void clearIngredientsAndCount() {
        for (int i = 0; i < MAX_INGREDIENTS; i++) {
            ingredients.set(i, ItemStack.EMPTY);
        }
        ingredientCount = 0;
    }

    public int fillWater(int amountMb) {
        if (amountMb <= 0) return 0;

        int space = capacityMb - waterMb;
        int accepted = Math.min(space, amountMb);

        if (accepted > 0) {
            waterMb += accepted;
            if (waterMb > capacityMb) waterMb = capacityMb;

            if (kind == ContentsKind.EMPTY && waterMb > 0) {
                kind = ContentsKind.WATER;
            }

            markDirty();
        }

        return accepted;
    }

    public int drainWater(int amountMb) {
        if (amountMb <= 0) return 0;

        int removed = Math.min(waterMb, amountMb);

        if (removed > 0) {
            waterMb -= removed;
            if (waterMb < 0) waterMb = 0;

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

    private void resetToEmpty() {
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

    private void markDirty() {
        dirty = true;
    }

    private static int darkenRgb(int rgb, float factor) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        r = Math.max(0, Math.min(255, (int) (r * factor)));
        g = Math.max(0, Math.min(255, (int) (g * factor)));
        b = Math.max(0, Math.min(255, (int) (b * factor)));

        return (r << 16) | (g << 8) | b;
    }
}