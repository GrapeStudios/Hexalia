package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.RitualBrazierBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.RitualBrazierRecipeInput;
import net.astralya.hexalia.util.ModUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class RitualBrazierBlockEntity extends SyncBlockEntity implements Inventory {
    public enum RitualResult { SUCCESS, NO_CELESTIAL_BLOOMS, INVALID_ITEM }

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private boolean isRitualFocusItem = false;
    private float rotation = 0f;

    public RitualBrazierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.RITUAL_BRAZIER, pos, state);
    }

    @Override public int getMaxCountPerStack() { return 1; }
    @Override public int size() { return 1; }
    @Override public boolean isEmpty() { return inventory.getFirst().isEmpty(); }
    @Override public ItemStack getStack(int slot) { return slot == 0 ? inventory.getFirst() : ItemStack.EMPTY; }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(inventory, slot, amount);
        if (!result.isEmpty()) inventoryChanged();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = Inventories.removeStack(inventory, slot);
        if (!result.isEmpty()) inventoryChanged();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
        inventoryChanged();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (world == null || world.getBlockEntity(pos) != this) return false;
        double cx = pos.getX() + 0.5, cy = pos.getY() + 0.5, cz = pos.getZ() + 0.5;
        return player.squaredDistanceTo(cx, cy, cz) <= 64.0;
    }

    @Override
    public void clear() {
        inventory.set(0, ItemStack.EMPTY);
        isRitualFocusItem = false;
        inventoryChanged();
    }

    public boolean addItem(ItemStack fromPlayer) {
        if (isEmpty() && !fromPlayer.isEmpty()) {
            ItemStack one = fromPlayer.split(1);
            inventory.set(0, one.isEmpty() ? ItemStack.EMPTY : one);
            isRitualFocusItem = false;
            inventoryChanged();
            return true;
        }
        return false;
    }

    public ItemStack removeItem() {
        if (isEmpty()) return ItemStack.EMPTY;
        isRitualFocusItem = false;
        ItemStack out = inventory.getFirst();
        inventory.set(0, ItemStack.EMPTY);
        inventoryChanged();
        return out;
    }

    public ItemStack getStoredItem() { return getStack(0); }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if (rotation >= 360f) rotation = 0f;
        return rotation;
    }

    public RitualResult tryCelestialRitual() {
        if (world == null || isRitualFocusItem) return RitualResult.INVALID_ITEM;

        BlockPos bloomPos = findNearbyCelestialBloom();
        if (bloomPos == null) return RitualResult.NO_CELESTIAL_BLOOMS;

        ItemStack input = getStoredItem();
        if (input.isEmpty()) return RitualResult.INVALID_ITEM;

        var match = world.getRecipeManager().getFirstMatch(
                ModRecipes.RITUAL_BRAZIER_TYPE,
                new RitualBrazierRecipeInput(input),
                world
        );
        if (match.isEmpty()) return RitualResult.INVALID_ITEM;

        RecipeEntry<?> entry = match.get();
        ItemStack result = entry.value().getResult(world.getRegistryManager());
        if (result.isEmpty()) return RitualResult.INVALID_ITEM;

        Direction facing = getCachedState().get(RitualBrazierBlock.FACING);
        Direction eject = facing.rotateYCounterclockwise();

        double ox = 0.5 + (eject.getOffsetX() * 0.2);
        double oy = 0.2;
        double oz = 0.5 + (eject.getOffsetZ() * 0.2);

        float vx = eject.getOffsetX() * 0.2f;
        float vy = 0f;
        float vz = eject.getOffsetZ() * 0.2f;

        ItemStack out = result.copy();
        if (ModUtil.hasSpawnItemEntity()) {
            ModUtil.spawnItemEntity(world, out, pos.getX() + ox, pos.getY() + oy, pos.getZ() + oz, vx, vy, vz);
        } else {
            world.spawnEntity(new ItemEntity(world, pos.getX() + ox, pos.getY() + oy, pos.getZ() + oz, out, vx, vy, vz));
        }

        removeItem();

        if (!world.isClient) {
            world.setBlockState(bloomPos, Blocks.DEAD_BUSH.getDefaultState(), 3);
            if (world instanceof ServerWorld server) {
                emitEffects(server, bloomPos);
            }
        }

        return RitualResult.SUCCESS;
    }

    private BlockPos findNearbyCelestialBloom() {
        if (world == null) return null;
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if (dx == 0 && dz == 0) continue;
                BlockPos check = pos.add(dx, 0, dz);
                if (world.getBlockState(check).isOf(ModBlocks.CELESTIAL_BLOOM)) {
                    return check;
                }
            }
        }
        return null;
    }

    private void emitEffects(ServerWorld server, BlockPos at) {
        server.spawnParticles(
                ModParticleType.LEAVES,
                at.getX() + 0.5, at.getY() + 0.6, at.getZ() + 0.5,
                15, 0.2, 0.25, 0.2, 0.0
        );
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (world != null) world.playSound(null, pos, sound, SoundCategory.BLOCKS, volume, pitch);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putBoolean("IsItemImbued", this.isRitualFocusItem);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.isRitualFocusItem = nbt.getBoolean("IsItemImbued");
        this.inventory.set(0, ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory, registryLookup);
    }
}
