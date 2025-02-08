package net.grapes.hexalia.block.entity;


import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.networking.ModMessages;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RitualBrazierBlockEntity extends BlockEntity implements ImplementedInventory {

    private static final int MOONLIGHT_DURATION = 60;
    private int timer = 0;
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    private static final Map<Item, Item> TRANSFORMATIONS = Map.of(
            Items.AMETHYST_SHARD, ModItems.MOON_CRYSTAL,
            Items.GLOW_BERRIES, ModItems.MOON_BERRIES);

    public RitualBrazierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RITUAL_BRAZIER_BE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, RitualBrazierBlockEntity blockEntity) {
        if (world.isClient) return;

        ItemStack itemStack = blockEntity.getStack(0);
        Item resultItem = TRANSFORMATIONS.get(itemStack.getItem());

        if (resultItem != null) {
            blockEntity.timer++;

            if (blockEntity.timer >= MOONLIGHT_DURATION) {
                if (isNight(world) && isExposedToMoon(world, pos)) {
                    blockEntity.setStack(0, ItemStack.EMPTY);
                    blockEntity.timer = 0;
                    blockEntity.markDirty();

                    world.updateListeners(pos, state, state, Block.NOTIFY_ALL);

                    ItemStack resultStack = new ItemStack(resultItem);
                    ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, resultStack);

                    spawnParticles(world, pos);
                    world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                } else {
                    blockEntity.timer = 0;
                }
            }
        } else {
            blockEntity.timer = 0;
        }
    }

    private static void spawnParticles(World world, BlockPos pos) {
        if (world instanceof ServerWorld serverWorld) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;

            serverWorld.spawnParticles(ParticleTypes.POOF, x, y, z, 10, 0.2, 0.2, 0.2, 0.02);
        }
    }

    private static boolean isNight(World world) {
        long time = world.getTimeOfDay();
        return time > 13000 && time < 23000;
    }

    private static boolean isExposedToMoon(World world, BlockPos pos) {
      if (world instanceof ServerWorld serverWorld) {
          return serverWorld.isSkyVisible(pos);
      }
      return false;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.timer = nbt.getInt("Timer");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("Timer", timer);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
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

    public ItemStack getRenderStack() {
        return this.getStack(0);
    }

    public void setInventory(DefaultedList<ItemStack> list) {
        for (int i = 0; i < list.size(); i++) {
            this.inventory.set(i, list.get(i));
        }
        markDirty();
    }

    public boolean addItem(ItemStack itemStack) {
        if (isEmpty() && !itemStack.isEmpty()) {
            setStack(0, itemStack.split(1));
            markDirty();
            return true;
        }
        return false;
    }

    public ItemStack removeItem() {
        if (!isEmpty()) {
            ItemStack itemStack = getStoredItem().split(1);
            markDirty();
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    public boolean isEmpty() {
        return getStack(0).isEmpty();
    }

    public ItemStack getStoredItem() {
        return getStack(0);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        Inventories.writeNbt(nbtCompound, inventory, true);
        return nbtCompound;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}