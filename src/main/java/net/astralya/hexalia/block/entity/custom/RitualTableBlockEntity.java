package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.custom.RitualBrazierBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RitualTableBlockEntity extends BlockEntity implements SidedInventory {

    public static final int DURATION = 8 * 20;

    private static final int SLOT = 0;
    private static final int[] INPUT_OUTPUT_SLOT = new int[]{SLOT};

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
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

    public float getRenderingRotation() {
        rotation = (rotation + 0.5f) % 360f;
        return rotation;
    }

    public void startTransformation(ItemStack output, int durationTicks, List<RitualBrazierBlockEntity> braziers) {
        if (transformTicksRemaining > 0) return;
        this.transformTicksRemaining = Math.max(1, durationTicks);
        this.totalTransformTicks = this.transformTicksRemaining;
        this.pendingOutput = output.copy();
        this.activeBraziers = new ArrayList<>(braziers);
        this.nextBrazierIndex = 0;
        markDirty();
    }

    public void setGrownCropPositions(List<BlockPos> crops) {
        this.grownCrops = new ArrayList<>(crops);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, RitualTableBlockEntity be) {
        if (be.transformTicksRemaining <= 0) return;
        if (be.isEmpty() || hasMissingBrazierItems(be)) {
            cancelRitual(world, pos, be);
            return;
        }
        int base = be.totalTransformTicks > 0 ? be.totalTransformTicks : DURATION;
        int elapsed = base - be.transformTicksRemaining;
        handleActiveBraziers(world, pos, be, elapsed);
        be.transformTicksRemaining--;
        if (be.transformTicksRemaining == 0) {
            completeRitual(world, pos, be);
        }
    }

    private static boolean hasMissingBrazierItems(RitualTableBlockEntity be) {
        for (int i = be.nextBrazierIndex; i < be.activeBraziers.size(); i++) {
            RitualBrazierBlockEntity brazier = be.activeBraziers.get(i);
            if (i == be.nextBrazierIndex && !be.cachedParticleItem.isEmpty()) continue;
            if (brazier == null || brazier.isRemoved() || brazier.isEmpty()) return true;
        }
        return false;
    }

    private static void handleActiveBraziers(World world, BlockPos pos, RitualTableBlockEntity be, int elapsed) {
        if (be.activeBraziers.isEmpty() || be.nextBrazierIndex >= be.activeBraziers.size()) return;
        int ticksPerBrazier = 40;
        int currentTime = elapsed - (be.nextBrazierIndex * ticksPerBrazier);
        RitualBrazierBlockEntity brazier = be.activeBraziers.get(be.nextBrazierIndex);
        if (brazier == null) return;
        if (currentTime == 0) {
            be.cachedParticleItem = brazier.getStoredItem().copy();
            brazier.removeItem();
            BlockState brazierState = world.getBlockState(brazier.getPos());
            if (brazierState.getBlock() instanceof RitualBrazierBlock
                    && brazierState.contains(RitualBrazierBlock.SALTED)
                    && brazierState.get(RitualBrazierBlock.SALTED)) {
                world.setBlockState(brazier.getPos(), brazierState.with(RitualBrazierBlock.SALTED, false), 3);
            }
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
        double startX = from.getX() + 0.5;
        double startY = from.getY() + 0.4;
        double startZ = from.getZ() + 0.5;
        double endX = to.getX() + 0.5;
        double endY = to.getY() + 1.15;
        double endZ = to.getZ() + 0.5;
        double progress = time / (double) totalTime;
        double px = startX + (endX - startX) * progress;
        double py = startY + (endY - startY) * progress;
        double pz = startZ + (endZ - startZ) * progress;
        for (int i = 0; i < 3; i++) {
            double ox = (server.random.nextDouble() - 0.5) * 0.05;
            double oy = (server.random.nextDouble() - 0.5) * 0.05;
            double oz = (server.random.nextDouble() - 0.5) * 0.05;
            double speed = 0.008 + server.random.nextDouble() * 0.004;
            double vx = (endX - startX) * speed;
            double vy = (endY - startY) * speed + 0.003;
            double vz = (endZ - startZ) * speed;
            server.spawnParticles(particle, px + ox, py + oy, pz + oz, 1, vx, vy, vz, 0.0);
        }
    }

    private static void spawnAbsorbBurst(ServerWorld server, BlockPos pos, ItemStack item) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 1.1;
        double cz = pos.getZ() + 0.5;
        for (int i = 0; i < 12; i++) {
            double ox = (server.random.nextDouble() - 0.5) * 0.5;
            double oy = server.random.nextDouble() * 0.3;
            double oz = (server.random.nextDouble() - 0.5) * 0.5;
            double vx = (server.random.nextDouble() - 0.5) * 0.02;
            double vy = 0.04 + server.random.nextDouble() * 0.02;
            double vz = (server.random.nextDouble() - 0.5) * 0.02;
            server.spawnParticles(ParticleTypes.WITCH, cx + ox, cy + oy, cz + oz, 1, vx, vy, vz, 0.0);
        }
        if (!item.isEmpty()) {
            for (int i = 0; i < 8; i++) {
                double ox = (server.random.nextDouble() - 0.5) * 0.2;
                double oy = server.random.nextDouble() * 0.2;
                double oz = (server.random.nextDouble() - 0.5) * 0.2;
                double vx = (server.random.nextDouble() - 0.5) * 0.005;
                double vy = 0.015 + server.random.nextDouble() * 0.005;
                double vz = (server.random.nextDouble() - 0.5) * 0.005;
                server.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, item), cx + ox, cy + oy, cz + oz, 1, vx, vy, vz, 0.0);
            }
        }
        server.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.4f, 1.2f + server.random.nextFloat() * 0.2f);
    }

    private static void completeRitual(World world, BlockPos pos, RitualTableBlockEntity be) {
        be.setStack(SLOT, be.pendingOutput);
        be.pendingOutput = ItemStack.EMPTY;
        for (BlockPos cropPos : be.grownCrops) {
            BlockState state = world.getBlockState(cropPos);
            if (!(state.getBlock() instanceof CropBlock)) continue;
            IntProperty ageProp = null;
            for (var property : state.getProperties()) {
                if (property instanceof IntProperty intProperty && "age".equals(intProperty.getName())) {
                    ageProp = intProperty;
                    break;
                }
            }
            if (ageProp == null || !state.contains(ageProp)) continue;
            world.setBlockState(cropPos, state.with(ageProp, 0), 3);
        }
        be.activeBraziers = Collections.emptyList();
        be.nextBrazierIndex = 0;
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

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        ItemStack stack = inventory.get(SLOT);
        if (!stack.isEmpty()) {
            nbt.put("Inv", (NbtCompound) stack.encode(registries));
        }

        nbt.putInt("TicksLeft", transformTicksRemaining);
        nbt.putInt("TotalTicks", totalTransformTicks);

        if (!pendingOutput.isEmpty()) {
            nbt.put("PendingOut", (NbtCompound) pendingOutput.encode(registries));
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        inventory.set(SLOT, ItemStack.EMPTY);

        if (nbt.contains("Inv", NbtCompound.COMPOUND_TYPE)) {
            inventory.set(SLOT, ItemStack.fromNbt(registries, nbt.getCompound("Inv")).orElse(ItemStack.EMPTY));
        }

        transformTicksRemaining = nbt.getInt("TicksLeft");
        totalTransformTicks = nbt.getInt("TotalTicks");
        pendingOutput = nbt.contains("PendingOut", NbtCompound.COMPOUND_TYPE)
                ? ItemStack.fromNbt(registries, nbt.getCompound("PendingOut")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt, registries);
        return nbt;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return inventory.get(SLOT).isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot == SLOT ? inventory.get(SLOT) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot != SLOT || transformTicksRemaining > 0) {
            return ItemStack.EMPTY;
        }

        ItemStack removed = Inventories.splitStack(inventory, slot, amount);
        if (!removed.isEmpty()) {
            markDirty();
            if (world != null && !world.isClient) {
                world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            }
        }
        return removed;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot != SLOT || transformTicksRemaining > 0) {
            return ItemStack.EMPTY;
        }

        ItemStack removed = Inventories.removeStack(inventory, slot);
        if (!removed.isEmpty()) {
            markDirty();
            if (world != null && !world.isClient) {
                world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            }
        }
        return removed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot != SLOT || transformTicksRemaining > 0) {
            return;
        }

        if (stack.isEmpty()) {
            inventory.set(SLOT, ItemStack.EMPTY);
        } else {
            inventory.set(SLOT, stack.copyWithCount(1));
        }

        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot != SLOT) {
            return false;
        }
        if (transformTicksRemaining > 0) {
            return false;
        }
        if (stack.isEmpty()) {
            return false;
        }
        return !stack.isOf(ModItems.HEX_FOCUS) && inventory.get(SLOT).isEmpty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return world != null
                && world.getBlockEntity(pos) == this
                && player.squaredDistanceTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clear() {
        inventory.set(SLOT, ItemStack.EMPTY);
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) {
            return INPUT_OUTPUT_SLOT;
        }
        if (side == Direction.DOWN) {
            return INPUT_OUTPUT_SLOT;
        }
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir != Direction.UP) {
            return false;
        }
        if (slot != SLOT) {
            return false;
        }
        if (transformTicksRemaining > 0) {
            return false;
        }
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.isOf(ModItems.HEX_FOCUS)) {
            return false;
        }
        return inventory.get(SLOT).isEmpty();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir != Direction.DOWN) {
            return false;
        }
        if (slot != SLOT) {
            return false;
        }
        if (transformTicksRemaining > 0) {
            return false;
        }
        return !stack.isEmpty();
    }
}