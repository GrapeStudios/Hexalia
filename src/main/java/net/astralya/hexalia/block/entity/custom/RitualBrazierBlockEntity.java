package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.entity.ModBlockEntities;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.RitualBrazierBlock;
import net.astralya.hexalia.networking.ModMessages;
import net.astralya.hexalia.recipe.RitualBrazierRecipe;
import net.astralya.hexalia.util.ModUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RitualBrazierBlockEntity extends BlockEntity implements SidedInventory {

    public enum RitualResult {
        SUCCESS,
        NO_CELESTIAL_BLOOMS,
        INVALID_ITEM
    }

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private float rotation;
    private boolean active = false;

    public RitualBrazierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RITUAL_BRAZIER_BE, pos, state);
    }

    public RitualResult tryCelestialRitual() {
        if (world == null || isEmpty()) return RitualResult.INVALID_ITEM;

        if (!hasEnoughNearbyCelestialBlooms()) {
            return RitualResult.NO_CELESTIAL_BLOOMS;
        }

        Optional<RitualBrazierRecipe> matchingRecipe = world.getRecipeManager()
                .getFirstMatch(RitualBrazierRecipe.Type.INSTANCE, new SimpleInventory(getStoredItem()), world);

        if (matchingRecipe.isEmpty()) {
            return RitualResult.INVALID_ITEM;
        }

        ItemStack resultStack = matchingRecipe.get().getOutput(world.getRegistryManager()).copy();
        Direction direction = getCachedState().get(RitualBrazierBlock.FACING).rotateYCounterclockwise();

        ModUtil.spawnItemEntity(world, resultStack,
                pos.getX() + 0.5 + (direction.getOffsetX() * 0.2),
                pos.getY() + 0.2,
                pos.getZ() + 0.5 + (direction.getOffsetZ() * 0.2),
                direction.getOffsetX() * 0.2F, 0.0F, direction.getOffsetZ() * 0.2F);

        removeStack();
        return RitualResult.SUCCESS;
    }

    public boolean hasEnoughNearbyCelestialBlooms() {
        if (world == null) return false;

        int count = 0;
        BlockPos origin = getPos();

        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if (dx == 0 && dz == 0) continue;

                BlockPos checkPos = origin.add(dx, 0, dz);
                if (world.getBlockState(checkPos).isOf(ModBlocks.CELESTIAL_BLOOM)) {
                    count++;
                    if (count >= 2) return true;
                }
            }
        }
        return false;
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if (rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return inventory.get(0).isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    public ItemStack getStoredItem() {
        return getStack(0);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(inventory, slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = Inventories.removeStack(inventory, slot);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return pos.getSquaredDistance(player.getBlockPos()) <= 16;
    }

    @Override
    public void clear() {
        inventory.clear();
        markDirty();
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        Inventories.readNbt(nbt, inventory);
        this.rotation = nbt.getFloat("Rotation");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putFloat("Rotation", rotation);
    }

    public boolean addStack(ItemStack itemStack) {
        if (isEmpty() && !itemStack.isEmpty()) {
            setStack(0, itemStack.split(1));
            markDirty();
            return true;
        }
        return false;
    }

    public ItemStack removeStack() {
        if (!isEmpty()) {
            ItemStack item = getStack(0).split(1);
            markDirty();
            return item;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (!world.isClient()) {
            sync();
        }
    }

    private void sync() {
        if (world instanceof ServerWorld serverWorld) {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeInt(inventory.size());
            for (ItemStack itemStack : inventory) {
                data.writeItemStack(itemStack);
            }
            data.writeBlockPos(getPos());

            for (ServerPlayerEntity player : PlayerLookup.tracking(serverWorld, getPos())) {
                ServerPlayNetworking.send(player, ModMessages.SYNC_ITEM, data);
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = super.toInitialChunkDataNbt();
        Inventories.writeNbt(nbt, inventory);
        return nbt;
    }
}