package net.grapes.hexalia.block.entity;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.grapes.hexalia.networking.ModMessages;
import net.grapes.hexalia.recipe.RitualBrazierRecipe;
import net.minecraft.block.Block;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RitualBrazierBlockEntity extends BlockEntity implements SidedInventory {

    private static final int MOONLIGHT_DURATION = 400;
    private int timer = 0;
    private boolean active = false;
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public RitualBrazierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RITUAL_BRAZIER_BE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, RitualBrazierBlockEntity blockEntity) {
        if (world.isClient) return;

        if (blockEntity.active && !blockEntity.isEmpty()) {
            ItemStack itemStack = blockEntity.getStack(0);
            Optional<RitualBrazierRecipe> recipe = world.getRecipeManager()
                    .getFirstMatch(RitualBrazierRecipe.Type.INSTANCE, new SimpleInventory(itemStack), world);

            if (recipe.isPresent()) {
                if (isNight(world)) {
                    blockEntity.timer++;

                    if (blockEntity.timer >= MOONLIGHT_DURATION) {
                        blockEntity.setStack(0, ItemStack.EMPTY);
                        blockEntity.timer = 0;
                        blockEntity.setActive(false);
                        blockEntity.markDirty();

                        ItemStack resultStack = recipe.get().getOutput(world.getRegistryManager()).copy();
                        ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, resultStack);

                        spawnPoofParticles(world, pos);
                        world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    }
                } else {
                    blockEntity.cancelRitual();
                }
            } else {
                blockEntity.cancelRitual();
            }
        }
    }

    public boolean startMoonRitual(PlayerEntity player) {
        if (isEmpty()) {
            player.sendMessage(Text.translatable("message.hexalia.moonlight_ritual.invalid_item"), true);
            return false;
        }

        ItemStack itemStack = getStack(0);
        Optional<RitualBrazierRecipe> recipe = world.getRecipeManager()
                .getFirstMatch(RitualBrazierRecipe.Type.INSTANCE, new SimpleInventory(itemStack), world);

        if (recipe.isEmpty()) {
            player.sendMessage(Text.translatable("message.hexalia.moonlight_ritual.invalid_item"), true);
            return false;
        }

        if (!isNight(world)) {
            player.sendMessage(Text.translatable("message.hexalia.moonlight_ritual.not_night"), true);
            return false;
        }

        this.timer = 1;
        this.setActive(true);
        player.sendMessage(Text.translatable("message.hexalia.moonlight_ritual.started"), true);
        return true;
    }

    public void cancelRitual() {
        this.timer = 0;
        this.setActive(false);
        this.markDirty();
    }

    private static void spawnPoofParticles(World world, BlockPos pos) {
        if (world instanceof ServerWorld serverWorld) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;

            serverWorld.spawnParticles(ParticleTypes.POOF, x, y, z, 10, 0.2, 0.2, 0.2, 0.02);
        }
    }

    public void setActive(boolean active) {
        if (this.active != active) {
            this.active = active;
            this.markDirty();
            if (world != null && !world.isClient) {
                world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    private static boolean isNight(World world) {
        long time = world.getTimeOfDay();
        return time > 12500 && time < 23000;
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
        if (slot < 0 || slot >= this.inventory.size()) {
            return ItemStack.EMPTY;
        }
        return this.inventory.get(slot);
    }

    public ItemStack getRenderStack() {
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
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return pos.getSquaredDistance(player.getBlockPos()) <= 16;
    }

    @Override
    public void clear() {
        inventory.clear();
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
        this.timer = nbt.getInt("Timer");
        this.active = nbt.getBoolean("Active");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("Timer", timer);
        nbt.putBoolean("Active", active);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = super.toInitialChunkDataNbt();
        nbt.putBoolean("Active", this.active);
        Inventories.writeNbt(nbt, inventory);
        return nbt;
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
            if (this.active) {
                this.cancelRitual();
            }
            ItemStack itemStack = getStack(0).copy();
            setStack(0, ItemStack.EMPTY);
            markDirty();
            return itemStack;
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
}