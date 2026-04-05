package net.astralya.hexalia.gameplay.smallcauldron;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.BrewItem;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
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

    public enum ContentsKind { EMPTY, WATER, COOKING, MIXTURE, SPOILED }

    private int waterMb;
    private int requiredWaterMb = DEFAULT_REQUIRED_WATER_MB;
    private int capacityMb = DEFAULT_CAPACITY_MB;
    private final DefaultedList<ItemStack> ingredients = DefaultedList.ofSize(MAX_INGREDIENTS, ItemStack.EMPTY);
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
        if (kind == ContentsKind.MIXTURE) return overcooked ? darkenRgb(getMixtureBaseColor(), 0.55F) : getMixtureBaseColor();
        if (kind == ContentsKind.WATER || kind == ContentsKind.COOKING) return WATER_RGB;
        return 0;
    }

    public float getLiquidFill01() {
        if (capacityMb <= 0) return 0.0F;
        if (kind == ContentsKind.COOKING || kind == ContentsKind.MIXTURE) return 1.0F;
        float fill = (float) waterMb / (float) capacityMb;
        return Math.max(0.0F, Math.min(1.0F, fill));
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
        if (stack.isOf(Items.FLINT_AND_STEEL)) return false;
        if (isRusticBottle(stack)) return false;
        if (isLotusBlossom(stack)) return false;
        if (isWaterContainer(stack)) return false;
        if (kind != ContentsKind.WATER && kind != ContentsKind.EMPTY) return false;
        return firstEmptyIngredientSlot() != -1;
    }

    public boolean insertOne(ItemStack held) {
        if (!canInsertOne(held)) return false;
        if (kind == ContentsKind.EMPTY && waterMb > 0) kind = ContentsKind.WATER;
        int slot = firstEmptyIngredientSlot();
        if (slot == -1) return false;
        ItemStack one = held.copy();
        one.setCount(1);
        ingredients.set(slot, one);
        ingredientCount++;
        if (stirsDone > ingredientCount) stirsDone = ingredientCount;
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

    public StirResult stir(ServerWorld world) {
        stirsDone++;
        markDirty();
        if (stirsDone >= ingredientCount) {
            boolean started = tryStartCooking(world);
            return started ? StirResult.STARTED_COOKING : StirResult.STIRRED;
        }
        return StirResult.STIRRED;
    }

    public boolean canScoopMixtureWithRusticBottle() {
        return kind == ContentsKind.MIXTURE && servings > 0 && !mixtureResult.isEmpty();
    }

    public boolean tryScoopBottle(ServerWorld world, double x, double y, double z, PlayerEntity player, Hand hand, ItemStack held) {
        if (!canScoopMixtureWithRusticBottle()) return false;
        if (!isRusticBottle(held)) return false;
        ItemStack resultBottle = mixtureResult.copy();
        if (!player.getAbilities().creativeMode) held.decrement(1);
        if (!player.getInventory().insertStack(resultBottle)) {
            world.spawnEntity(new ItemEntity(world, x, y, z, resultBottle));
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

    public boolean tryCleanseSpoiled(PlayerEntity player, Hand hand, ItemStack held) {
        if (kind != ContentsKind.SPOILED) return false;
        if (!isLotusBlossom(held)) return false;
        if (!player.getAbilities().creativeMode) held.decrement(1);
        resetToEmpty();
        markDirty();
        return true;
    }

    public boolean canUseWaterContainer(ItemStack stack) {
        if (kind == ContentsKind.COOKING || kind == ContentsKind.MIXTURE || kind == ContentsKind.SPOILED) return false;
        if (stack.isOf(Items.WATER_BUCKET)) return waterMb + BUCKET_MB <= capacityMb;
        if (stack.isOf(Items.BUCKET)) return waterMb >= BUCKET_MB;
        if (stack.isOf(Items.GLASS_BOTTLE)) return waterMb >= BOTTLE_MB;
        return false;
    }

    public boolean tryUseWaterContainer(ServerWorld world, double x, double y, double z, PlayerEntity player, Hand hand, ItemStack held) {
        if (kind == ContentsKind.COOKING || kind == ContentsKind.MIXTURE || kind == ContentsKind.SPOILED) return false;
        if (held.isOf(Items.WATER_BUCKET)) {
            if (fillWater(BUCKET_MB) == BUCKET_MB) {
                if (!player.getAbilities().creativeMode) player.setStackInHand(hand, new ItemStack(Items.BUCKET));
                world.playSound(null, x, y, z, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return true;
            }
            return false;
        }
        if (held.isOf(Items.BUCKET)) {
            if (drainWater(BUCKET_MB) == BUCKET_MB) {
                if (!player.getAbilities().creativeMode) player.setStackInHand(hand, new ItemStack(Items.WATER_BUCKET));
                world.playSound(null, x, y, z, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return true;
            }
            return false;
        }
        if (!held.isOf(Items.GLASS_BOTTLE)) return false;
        if (drainWater(BOTTLE_MB) == BOTTLE_MB) {
            if (!player.getAbilities().creativeMode) {
                held.decrement(1);
                ItemStack waterBottle = createWaterBottle();
                if (!player.getInventory().insertStack(waterBottle)) player.dropItem(waterBottle, false);
            }
            world.playSound(null, x, y, z, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    public TickResult tickServer(ServerWorld world, boolean blockLit) {
        if (kind == ContentsKind.COOKING) {
            if (!blockLit) return TickResult.NONE;
            cookProgress++;
            if (cookProgress >= cookTimeTicks) {
                if (!finalizeCook(world)) makeSpoiled();
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

    public void dropAll(World world, double x, double y, double z) {
        for (ItemStack stack : ingredients) {
            if (!stack.isEmpty()) ItemScatterer.spawn(world, x, y, z, stack);
        }
        resetToEmpty();
        markDirty();
    }

    public void save(NbtCompound nbt) {
        nbt.putInt(TAG_WATER_MB, waterMb);
        nbt.putInt(TAG_REQUIRED_WATER_MB, requiredWaterMb);
        nbt.putInt(TAG_CAPACITY_MB, capacityMb);
        nbt.putInt(TAG_COOK_TIME_TICKS, cookTimeTicks);
        nbt.putInt(TAG_COOK_PROGRESS, cookProgress);
        nbt.putInt(TAG_STIRS_DONE, stirsDone);
        nbt.putInt(TAG_MIXTURE_AGE, mixtureAgeTicks);
        nbt.putBoolean(TAG_OVERCOOKED, overcooked);
        nbt.putInt(TAG_CONTENTS_KIND, kind.ordinal());
        nbt.putInt(TAG_SERVINGS, servings);
        nbt.putInt(TAG_MIX_COLOR, mixtureColor);
        if (lockedRecipeId != null) nbt.putString(TAG_LOCKED_RECIPE, lockedRecipeId);

        NbtList list = new NbtList();
        for (int i = 0; i < MAX_INGREDIENTS; i++) {
            ItemStack stack = ingredients.get(i);
            if (stack.isEmpty()) continue;

            NbtCompound entry = new NbtCompound();
            entry.putInt("Slot", i);
            entry.put("Stack", stack.writeNbt(new NbtCompound()));
            list.add(entry);
        }
        nbt.put(TAG_INGREDIENTS, list);

        if (!mixtureResult.isEmpty()) {
            nbt.put(TAG_MIXTURE_RESULT, mixtureResult.writeNbt(new NbtCompound()));
        }
    }

    public void load(NbtCompound nbt) {
        capacityMb = nbt.contains(TAG_CAPACITY_MB) ? nbt.getInt(TAG_CAPACITY_MB) : DEFAULT_CAPACITY_MB;
        if (capacityMb <= 0) capacityMb = DEFAULT_CAPACITY_MB;
        waterMb = Math.max(0, Math.min(nbt.getInt(TAG_WATER_MB), capacityMb));
        requiredWaterMb = nbt.contains(TAG_REQUIRED_WATER_MB) ? nbt.getInt(TAG_REQUIRED_WATER_MB) : DEFAULT_REQUIRED_WATER_MB;
        if (requiredWaterMb <= 0 || requiredWaterMb > capacityMb) requiredWaterMb = DEFAULT_REQUIRED_WATER_MB;
        cookTimeTicks = nbt.contains(TAG_COOK_TIME_TICKS) ? Math.max(0, nbt.getInt(TAG_COOK_TIME_TICKS)) : DEFAULT_COOK_TIME_TICKS;
        cookProgress = Math.max(0, nbt.getInt(TAG_COOK_PROGRESS));
        stirsDone = Math.max(0, nbt.getInt(TAG_STIRS_DONE));
        mixtureAgeTicks = Math.max(0, nbt.getInt(TAG_MIXTURE_AGE));
        overcooked = nbt.getBoolean(TAG_OVERCOOKED);

        int kindId = nbt.getInt(TAG_CONTENTS_KIND);
        ContentsKind[] values = ContentsKind.values();
        kind = kindId >= 0 && kindId < values.length ? values[kindId] : ContentsKind.EMPTY;

        servings = Math.max(0, nbt.getInt(TAG_SERVINGS));
        mixtureColor = nbt.getInt(TAG_MIX_COLOR);
        mixtureResult = ItemStack.EMPTY;
        if (nbt.contains(TAG_MIXTURE_RESULT, NbtElement.COMPOUND_TYPE)) {
            mixtureResult = ItemStack.fromNbt(nbt.getCompound(TAG_MIXTURE_RESULT));
        }

        lockedRecipeId = nbt.contains(TAG_LOCKED_RECIPE, NbtElement.STRING_TYPE) ? nbt.getString(TAG_LOCKED_RECIPE) : null;

        clearIngredientsAndCount();
        if (nbt.contains(TAG_INGREDIENTS, NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList(TAG_INGREDIENTS, NbtElement.COMPOUND_TYPE);
            loadIngredientsLegacyAware(list);
        }

        if (kind == ContentsKind.MIXTURE && overcooked && servings > 1) {
            servings = 1;
        }

        dirty = false;
    }

    private void loadIngredientsLegacyAware(NbtList list) {
        boolean anyNested = false;
        boolean anySlot = false;

        for (int i = 0; i < list.size(); i++) {
            NbtCompound entry = list.getCompound(i);
            if (entry.contains("Slot", NbtElement.INT_TYPE)) anySlot = true;
            if (entry.contains("Slot", NbtElement.INT_TYPE) && entry.contains("Stack", NbtElement.COMPOUND_TYPE)) {
                anyNested = true;
                break;
            }
        }

        if (anyNested) {
            for (int i = 0; i < list.size(); i++) {
                NbtCompound entry = list.getCompound(i);
                int slot = entry.contains("Slot", NbtElement.INT_TYPE) ? entry.getInt("Slot") : -1;
                if (slot < 0 || slot >= MAX_INGREDIENTS) continue;
                if (!entry.contains("Stack", NbtElement.COMPOUND_TYPE)) continue;

                ItemStack stack = ItemStack.fromNbt(entry.getCompound("Stack"));
                if (!stack.isEmpty()) {
                    ingredients.set(slot, stack);
                    ingredientCount++;
                }
            }
            return;
        }

        if (anySlot) {
            for (int i = 0; i < list.size(); i++) {
                NbtCompound entry = list.getCompound(i);
                int slot = entry.contains("Slot", NbtElement.INT_TYPE) ? entry.getInt("Slot") : -1;
                if (slot < 0 || slot >= MAX_INGREDIENTS) continue;

                ItemStack stack = ItemStack.fromNbt(entry);
                if (!stack.isEmpty()) {
                    ingredients.set(slot, stack);
                    ingredientCount++;
                }
            }
            return;
        }

        int nextSlot = 0;
        for (int i = 0; i < list.size() && nextSlot < MAX_INGREDIENTS; i++) {
            NbtCompound entry = list.getCompound(i);
            ItemStack stack = ItemStack.fromNbt(entry);
            if (stack.isEmpty()) continue;

            ingredients.set(nextSlot, stack);
            ingredientCount++;
            nextSlot++;
        }
    }

    private boolean tryStartCooking(ServerWorld world) {
        if (kind != ContentsKind.WATER) return false;
        if (waterMb < requiredWaterMb) return false;
        if (ingredientCount <= 0) return false;
        if (cookProgress > 0) return false;

        Optional<SmallCauldronRecipe> match = findRecipeMatch(world);
        if (match.isPresent()) {
            SmallCauldronRecipe recipe = match.get();
            lockedRecipeId = recipe.getId().toString();
            mixtureResult = recipe.getOutput(world.getRegistryManager()).copy();
            cookTimeTicks = Math.max(1, recipe.getBrewTime());
        } else {
            lockedRecipeId = null;
            mixtureResult = ItemStack.EMPTY;
            cookTimeTicks = DEFAULT_COOK_TIME_TICKS;
        }

        clearIngredientsAndCount();
        stirsDone = 0;
        kind = ContentsKind.COOKING;
        cookProgress = 0;
        markDirty();
        return true;
    }

    private boolean finalizeCook(ServerWorld world) {
        Optional<SmallCauldronRecipe> locked = findLockedRecipe(world);
        if (locked.isEmpty()) return false;

        SmallCauldronRecipe recipe = locked.get();
        if (mixtureResult.isEmpty()) {
            ItemStack result = recipe.getOutput(world.getRegistryManager());
            mixtureResult = result.isEmpty() ? ItemStack.EMPTY : result.copy();
        }

        drainWater(requiredWaterMb);
        kind = ContentsKind.MIXTURE;
        servings = SERVINGS_PER_MIXTURE;
        mixtureColor = getRecipeMixtureColor(world, recipe);
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
        cookTimeTicks = DEFAULT_COOK_TIME_TICKS;
    }

    private Optional<SmallCauldronRecipe> findRecipeMatch(ServerWorld world) {
        SimpleInventory inventory = createRecipeInventory();
        return world.getRecipeManager().getFirstMatch(ModRecipes.SMALL_CAULDRON_TYPE, inventory, world).map(recipe -> (SmallCauldronRecipe) recipe);
    }

    private Optional<SmallCauldronRecipe> findLockedRecipe(ServerWorld world) {
        if (lockedRecipeId == null || lockedRecipeId.isEmpty()) return Optional.empty();

        Identifier id;
        try {
            id = new Identifier(lockedRecipeId);
        } catch (Exception ignored) {
            return Optional.empty();
        }

        for (SmallCauldronRecipe recipe : world.getRecipeManager().listAllOfType(ModRecipes.SMALL_CAULDRON_TYPE)) {
            if (recipe.getId().equals(id)) {
                return Optional.of(recipe);
            }
        }

        return Optional.empty();
    }

    private int getRecipeMixtureColor(ServerWorld world, SmallCauldronRecipe recipe) {
        ItemStack output = recipe.getOutput(world.getRegistryManager());
        if (output.isEmpty()) return WATER_RGB;
        if (output.getItem() instanceof BrewItem brewItem) {
            int color = brewItem.getBrewColor();
            return color != 0 ? color : WATER_RGB;
        }
        return WATER_RGB;
    }

    private SimpleInventory createRecipeInventory() {
        List<ItemStack> recipeStacks = getIngredientListForRecipe();
        SimpleInventory inventory = new SimpleInventory(recipeStacks.size());
        for (int i = 0; i < recipeStacks.size(); i++) {
            inventory.setStack(i, recipeStacks.get(i).copy());
        }
        return inventory;
    }

    private List<ItemStack> getIngredientListForRecipe() {
        if (ingredientCount <= 0) return List.of();
        List<ItemStack> list = new ArrayList<>(ingredientCount);
        for (ItemStack stack : ingredients) {
            if (!stack.isEmpty()) list.add(stack);
        }
        return list;
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

    private void clearIngredientsAndCount() {
        for (int i = 0; i < MAX_INGREDIENTS; i++) {
            ingredients.set(i, ItemStack.EMPTY);
        }
        ingredientCount = 0;
    }

    public int fillWater(int amountMb) {
        if (amountMb <= 0) return 0;
        int accepted = Math.min(capacityMb - waterMb, amountMb);
        if (accepted > 0) {
            waterMb = Math.min(waterMb + accepted, capacityMb);
            if (kind == ContentsKind.EMPTY && waterMb > 0) kind = ContentsKind.WATER;
            markDirty();
        }
        return accepted;
    }

    public int drainWater(int amountMb) {
        if (amountMb <= 0) return 0;
        int removed = Math.min(waterMb, amountMb);
        if (removed > 0) {
            waterMb = Math.max(0, waterMb - removed);
            if (waterMb == 0 && ingredientCount == 0 && kind == ContentsKind.WATER) kind = ContentsKind.EMPTY;
            markDirty();
        }
        return removed;
    }

    public boolean isRusticBottle(ItemStack stack) {
        return stack.isOf(ModItems.RUSTIC_BOTTLE);
    }

    public boolean isLotusBlossom(ItemStack stack) {
        return stack.isOf(ModItems.LOTUS_BLOSSOM);
    }

    public boolean isWaterContainer(ItemStack stack) {
        return stack.isOf(Items.WATER_BUCKET) || stack.isOf(Items.BUCKET) || stack.isOf(Items.GLASS_BOTTLE);
    }

    private static ItemStack createWaterBottle() {
        return PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
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
        cookTimeTicks = DEFAULT_COOK_TIME_TICKS;
    }

    private void markDirty() {
        dirty = true;
    }

    private static int darkenRgb(int rgb, float factor) {
        int r = Math.max(0, Math.min(255, (int) (((rgb >> 16) & 0xFF) * factor)));
        int g = Math.max(0, Math.min(255, (int) (((rgb >> 8) & 0xFF) * factor)));
        int b = Math.max(0, Math.min(255, (int) ((rgb & 0xFF) * factor)));
        return (r << 16) | (g << 8) | b;
    }

    public enum StirResult {
        STIRRED,
        STARTED_COOKING
    }

    public enum TickResult {
        NONE,
        PROGRESSED,
        STATE_CHANGED
    }
}