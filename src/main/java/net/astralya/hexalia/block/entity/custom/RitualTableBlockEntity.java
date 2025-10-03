package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RitualTableBlockEntity extends SyncBlockEntity implements Inventory {

    private static final int TICKS_PER_BRAZIER = 40;

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    private ItemStack cachedParticleItem = ItemStack.EMPTY;
    private List<RitualBrazierBlockEntity> activeBraziers = Collections.emptyList();
    private List<BlockPos> grownCrops = Collections.emptyList();
    private ItemStack pendingOutput = ItemStack.EMPTY;

    private int transformTicksRemaining = 0;
    private int totalTransformTicks = 0;
    private int nextBrazierIndex = 0;
    private float rotation = 0.0f;

    public RitualTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.RITUAL_TABLE, pos, state);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot == 0 ? items.get(0) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack out = Inventories.splitStack(items, slot, amount);
        if (!out.isEmpty()) inventoryChanged();
        return out;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack out = Inventories.removeStack(items, slot);
        if (!out.isEmpty()) inventoryChanged();
        return out;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot != 0) return;
        if (stack.isOf(ModItems.HEX_FOCUS)) return;
        items.set(0, stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
        inventoryChanged();
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (world == null || world.getBlockEntity(pos) != this) return false;
        double cx = pos.getX() + 0.5, cy = pos.getY() + 0.5, cz = pos.getZ() + 0.5;
        return player.squaredDistanceTo(cx, cy, cz) <= 64.0;
    }

    @Override
    public void clear() {
        items.set(0, ItemStack.EMPTY);
        inventoryChanged();
    }

    public float getRenderingRotation() {
        rotation = (rotation + 0.5f) % 360f;
        return rotation;
    }

    public void startTransformation(ItemStack output, int durationTicks, List<RitualBrazierBlockEntity> braziers) {
        if (transformTicksRemaining > 0) return;
        transformTicksRemaining = durationTicks;
        totalTransformTicks = durationTicks;
        pendingOutput = output.copy();
        activeBraziers = new ArrayList<>(braziers);
        nextBrazierIndex = 0;
        markDirty();
    }

    public void setGrownCropPositions(List<BlockPos> crops) {
        grownCrops = new ArrayList<>(crops);
    }

    public static void tick(World world, BlockPos pos, BlockState state, RitualTableBlockEntity be) {
        if (be.transformTicksRemaining <= 0) return;
        if (be.isEmpty() || hasMissingBrazierItems(be)) {
            cancelRitual(world, pos, be);
            return;
        }

        int elapsed = be.totalTransformTicks - be.transformTicksRemaining;
        handleActiveBraziers(world, pos, be, elapsed);
        be.transformTicksRemaining--;

        if (be.transformTicksRemaining == 0) {
            completeRitual(world, pos, be);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("TicksLeft", transformTicksRemaining);
        nbt.putInt("TotalTicks", totalTransformTicks);

        if (!pendingOutput.isEmpty()) {
            NbtCompound out = new NbtCompound();
            pendingOutput.writeNbt(out);
            nbt.put("PendingOut", out);
        }

        Inventories.writeNbt(nbt, items);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        transformTicksRemaining = nbt.getInt("TicksLeft");
        totalTransformTicks = nbt.getInt("TotalTicks");

        if (nbt.contains("PendingOut", NbtElement.COMPOUND_TYPE)) {
            pendingOutput = ItemStack.fromNbt(nbt.getCompound("PendingOut"));
        } else {
            pendingOutput = ItemStack.EMPTY;
        }

        items.set(0, ItemStack.EMPTY);
        Inventories.readNbt(nbt, items);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        return nbt;
    }

    public boolean addItem(ItemStack fromPlayer) {
        if (isEmpty() && !fromPlayer.isEmpty() && !fromPlayer.isOf(ModItems.HEX_FOCUS)) {
            ItemStack one = fromPlayer.copy();
            one.setCount(1);
            setStack(0, one);
            return true;
        }
        return false;
    }

    public ItemStack removeItem() {
        if (isEmpty()) return ItemStack.EMPTY;
        ItemStack out = getStack(0);
        setStack(0, ItemStack.EMPTY);
        return out;
    }

    public ItemStack getStoredItem() {
        return getStack(0);
    }

    protected void inventoryChanged() {
        if (world == null) return;
        markDirty();
        if (!world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            ((ServerWorld) world).getChunkManager().markForUpdate(pos);
        }
    }

    private static boolean hasMissingBrazierItems(RitualTableBlockEntity be) {
        for (int i = be.nextBrazierIndex; i < be.activeBraziers.size(); i++) {
            RitualBrazierBlockEntity brazier = be.activeBraziers.get(i);
            if (i == be.nextBrazierIndex && !be.cachedParticleItem.isEmpty()) continue;
            if (brazier.isEmpty()) return true;
        }
        return false;
    }

    private static void handleActiveBraziers(World world, BlockPos pos, RitualTableBlockEntity be, int elapsed) {
        if (be.activeBraziers.isEmpty() || be.nextBrazierIndex >= be.activeBraziers.size()) return;

        int ticksPerBrazier = TICKS_PER_BRAZIER;
        int currentTime = elapsed - (be.nextBrazierIndex * ticksPerBrazier);
        RitualBrazierBlockEntity brazier = be.activeBraziers.get(be.nextBrazierIndex);

        if (currentTime == 0) {
            be.cachedParticleItem = brazier.getStoredItem().copy();
            brazier.removeItem();
        }

        if (currentTime >= 0 && currentTime < ticksPerBrazier && world instanceof ServerWorld server) {
            spawnItemParticles(server, be.cachedParticleItem, brazier.getPos(), pos, currentTime, ticksPerBrazier);
        }

        if (currentTime == ticksPerBrazier - 1) {
            if (world instanceof ServerWorld server) {
                spawnAbsorbBurst(server, pos, be.cachedParticleItem);
            }
            be.nextBrazierIndex++;
            be.cachedParticleItem = ItemStack.EMPTY;
        }
    }

    private static void spawnItemParticles(ServerWorld server, ItemStack item, BlockPos from, BlockPos to, int time, int totalTime) {
        if (item.isEmpty()) return;
        ItemStackParticleEffect particle = new ItemStackParticleEffect(ParticleTypes.ITEM, item);

        double startX = from.getX() + 0.5, startY = from.getY() + 0.4, startZ = from.getZ() + 0.5;
        double endX = to.getX() + 0.5, endY = to.getY() + 1.15, endZ = to.getZ() + 0.5;
        double progress = time / (double) totalTime;

        double px = startX + (endX - startX) * progress;
        double py = startY + (endY - startY) * progress;
        double pz = startZ + (endZ - startZ) * progress;

        for (int i = 0; i < 3; i++) {
            double offsetX = (server.random.nextDouble() - 0.5) * 0.05;
            double offsetY = (server.random.nextDouble() - 0.5) * 0.05;
            double offsetZ = (server.random.nextDouble() - 0.5) * 0.05;
            double speed = 0.008 + server.random.nextDouble() * 0.004;

            double velX = (endX - startX) * speed;
            double velY = (endY - startY) * speed + 0.003;
            double velZ = (endZ - startZ) * speed;

            server.spawnParticles(particle, px + offsetX, py + offsetY, pz + offsetZ, 1, velX, velY, velZ, 0.0);
        }
    }

    private static void spawnAbsorbBurst(ServerWorld server, BlockPos pos, ItemStack item) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 1.1;
        double cz = pos.getZ() + 0.5;

        for (int i = 0; i < 12; i++) {
            double dx = (server.random.nextDouble() - 0.5) * 0.5;
            double dy = server.random.nextDouble() * 0.3;
            double dz = (server.random.nextDouble() - 0.5) * 0.5;
            double vx = (server.random.nextDouble() - 0.5) * 0.02;
            double vy = 0.04 + server.random.nextDouble() * 0.02;
            double vz = (server.random.nextDouble() - 0.5) * 0.02;

            server.spawnParticles(ParticleTypes.WITCH, cx + dx, cy + dy, cz + dz, 1, vx, vy, vz, 0.0);
        }

        if (!item.isEmpty()) {
            for (int i = 0; i < 8; i++) {
                double dx = (server.random.nextDouble() - 0.5) * 0.2;
                double dy = server.random.nextDouble() * 0.2;
                double dz = (server.random.nextDouble() - 0.5) * 0.2;
                double vx = (server.random.nextDouble() - 0.5) * 0.005;
                double vy = 0.015 + server.random.nextDouble() * 0.005;
                double vz = (server.random.nextDouble() - 0.5) * 0.005;

                server.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, item), cx + dx, cy + dy, cz + dz, 1, vx, vy, vz, 0.0);
            }
        }

        server.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.4f, 1.2f + server.random.nextFloat() * 0.2f);
    }

    private static void completeRitual(World world, BlockPos pos, RitualTableBlockEntity be) {
        be.setStack(0, be.pendingOutput);
        be.pendingOutput = ItemStack.EMPTY;

        for (BlockPos cropPos : be.grownCrops) {
            BlockState st = world.getBlockState(cropPos);
            if (st.getBlock() instanceof CropBlock crop) {
                world.setBlockState(cropPos, crop.withAge(0), 3);
            }
        }

        be.activeBraziers = Collections.emptyList();
        be.nextBrazierIndex = 0;
        be.totalTransformTicks = 0;

        world.playSound(null, pos, ModSoundEvents.RITUAL_SUCCESS, SoundCategory.BLOCKS, 0.8f, 1.0f);

        if (world instanceof ServerWorld server) {
            server.spawnParticles(ModParticleType.LEAVES, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 15, 0.3, 0.3, 0.3, 0.0);
        }
        be.markDirty();
    }

    private static void cancelRitual(World world, BlockPos pos, RitualTableBlockEntity be) {
        be.transformTicksRemaining = 0;
        be.totalTransformTicks = 0;
        be.pendingOutput = ItemStack.EMPTY;
        be.activeBraziers = Collections.emptyList();
        be.nextBrazierIndex = 0;
        be.cachedParticleItem = ItemStack.EMPTY;

        if (world instanceof ServerWorld server) {
            server.spawnParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 12, 0.4, 0.4, 0.4, 0.02);
            PlayerEntity nearest = server.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, false);
            if (nearest != null) {
                nearest.sendMessage(Text.translatable("message.hexalia.ritual.stopped_ritual"), true);
            }
        }
        world.playSound(null, pos, SoundEvents.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 0.4f, 0.6f);
        be.markDirty();
    }
}