package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.menu.NestingBlockMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class NestingBlockEntity extends BlockEntity implements Inventory, ExtendedScreenHandlerFactory<BlockPos> {

    public static final int COLUMNS = 9;
    public static final int ROWS = 1;
    public static final int SIZE = COLUMNS * ROWS;

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);
    private float openProgress;
    private float openProgressOld;
    private int openCount;

    public NestingBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.NESTING_BLOCK, pos, state);
    }

    public float getOpenProgress(float partialTick) {
        return MathHelper.lerp(partialTick, this.openProgressOld, this.openProgress);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, NestingBlockEntity be) {
        be.onSyncedBlockEvent(1, be.openCount);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, NestingBlockEntity be) {
        be.openProgressOld = be.openProgress;
        float target = be.openCount > 0 ? 1.0F : 0.0F;
        float speed = 0.2F;
        be.openProgress += (target - be.openProgress) * speed;
        be.openProgress = MathHelper.clamp(be.openProgress, 0.0F, 1.0F);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (this.world == null || this.isRemoved() || player.isSpectator()) return;
        openCount++;
        if (openCount == 1) {
            world.playSound(null, pos, SoundEvents.BLOCK_GRASS_STEP, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            world.addSyncedBlockEvent(pos, getCachedState().getBlock(), 1, openCount);
        }
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (this.world == null || this.isRemoved() || player.isSpectator()) return;
        openCount--;
        if (openCount < 0) openCount = 0;
        if (openCount == 0) {
            world.playSound(null, pos, SoundEvents.BLOCK_GRASS_STEP, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }
        world.addSyncedBlockEvent(pos, getCachedState().getBlock(), 1, openCount);
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.openCount = data;
            return true;
        }
        return super.onSyncedBlockEvent(type, data);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(items, slot, amount);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = Inventories.removeStack(items, slot);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        int max = getMaxCountPerStack();
        if (stack.getCount() > max) stack.setCount(max);
        markDirty();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world == null) return false;
        if (this.world.getBlockEntity(this.pos) != this) return false;
        double dx = player.getX() - (this.pos.getX() + 0.5D);
        double dy = player.getY() - (this.pos.getY() + 0.5D);
        double dz = player.getZ() - (this.pos.getZ() + 0.5D);
        return (dx * dx + dy * dy + dz * dz) <= 64.0D;
    }

    @Override
    public void clear() {
        items.clear();
        markDirty();
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.hexalia.nesting_block");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new NestingBlockMenu(syncId, playerInventory, this);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        Inventories.readNbt(nbt, this.items, registries);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        Inventories.writeNbt(nbt, this.items, registries);
    }
}