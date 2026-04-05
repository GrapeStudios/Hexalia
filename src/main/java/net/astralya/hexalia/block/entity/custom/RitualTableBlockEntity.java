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
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
    private float rotation = 0.0F;

    public RitualTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.RITUAL_TABLE, pos, state);
    }

    public float getRenderingRotation() {
        rotation = (rotation + 0.5F) % 360.0F;
        return rotation;
    }

    public void startTransformation(ItemStack output, int durationTicks, List<RitualBrazierBlockEntity> braziers) {
        if (transformTicksRemaining > 0) {
            return;
        }

        transformTicksRemaining = Math.max(1, durationTicks);
        totalTransformTicks = transformTicksRemaining;
        pendingOutput = output.copy();
        activeBraziers = new ArrayList<>(braziers);
        nextBrazierIndex = 0;
        markDirty();
        sync();
    }

    public void setGrownCropPositions(List<BlockPos> crops) {
        grownCrops = new ArrayList<>(crops);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, RitualTableBlockEntity blockEntity) {
        if (blockEntity.transformTicksRemaining <= 0) {
            return;
        }

        if (blockEntity.isEmpty() || hasMissingBrazierItems(blockEntity)) {
            cancelRitual(world, pos, blockEntity);
            return;
        }

        int base = blockEntity.totalTransformTicks > 0 ? blockEntity.totalTransformTicks : DURATION;
        int elapsed = base - blockEntity.transformTicksRemaining;

        handleActiveBraziers(world, pos, blockEntity, elapsed);
        blockEntity.transformTicksRemaining--;

        if (blockEntity.transformTicksRemaining == 0) {
            completeRitual(world, pos, blockEntity);
        } else {
            blockEntity.markDirty();
            blockEntity.sync();
        }
    }

    private static boolean hasMissingBrazierItems(RitualTableBlockEntity blockEntity) {
        for (int i = blockEntity.nextBrazierIndex; i < blockEntity.activeBraziers.size(); i++) {
            RitualBrazierBlockEntity brazier = blockEntity.activeBraziers.get(i);
            if (i == blockEntity.nextBrazierIndex && !blockEntity.cachedParticleItem.isEmpty()) {
                continue;
            }
            if (brazier == null || brazier.isRemoved() || brazier.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static void handleActiveBraziers(World world, BlockPos pos, RitualTableBlockEntity blockEntity, int elapsed) {
        if (blockEntity.activeBraziers.isEmpty() || blockEntity.nextBrazierIndex >= blockEntity.activeBraziers.size()) {
            return;
        }

        int ticksPerBrazier = 40;
        int currentTime = elapsed - (blockEntity.nextBrazierIndex * ticksPerBrazier);
        RitualBrazierBlockEntity brazier = blockEntity.activeBraziers.get(blockEntity.nextBrazierIndex);

        if (brazier == null) {
            return;
        }

        if (currentTime == 0) {
            blockEntity.cachedParticleItem = brazier.getStoredItem().copy();
            brazier.removeItem();

            BlockState brazierState = world.getBlockState(brazier.getPos());
            if (brazierState.getBlock() instanceof RitualBrazierBlock
                    && brazierState.contains(RitualBrazierBlock.SALTED)
                    && brazierState.get(RitualBrazierBlock.SALTED)) {
                world.setBlockState(brazier.getPos(), brazierState.with(RitualBrazierBlock.SALTED, false), 3);
            }
        }

        if (currentTime >= 0 && currentTime < ticksPerBrazier && world instanceof ServerWorld serverWorld) {
            spawnItemParticles(serverWorld, blockEntity.cachedParticleItem, brazier.getPos(), pos, currentTime, ticksPerBrazier);
        }

        if (currentTime == ticksPerBrazier - 1) {
            if (world instanceof ServerWorld serverWorld) {
                spawnAbsorbBurst(serverWorld, pos, blockEntity.cachedParticleItem);
            }
            blockEntity.nextBrazierIndex++;
            blockEntity.cachedParticleItem = ItemStack.EMPTY;
        }
    }

    private static void spawnItemParticles(ServerWorld serverWorld, ItemStack item, BlockPos from, BlockPos to, int time, int totalTime) {
        if (item.isEmpty()) {
            return;
        }

        ItemStackParticleEffect particle = new ItemStackParticleEffect(ParticleTypes.ITEM, item);
        double startX = from.getX() + 0.5;
        double startY = from.getY() + 0.4;
        double startZ = from.getZ() + 0.5;
        double endX = to.getX() + 0.5;
        double endY = to.getY() + 1.15;
        double endZ = to.getZ() + 0.5;
        double progress = time / (double) totalTime;
        double particleX = startX + (endX - startX) * progress;
        double particleY = startY + (endY - startY) * progress;
        double particleZ = startZ + (endZ - startZ) * progress;

        for (int i = 0; i < 3; i++) {
            double offsetX = (serverWorld.random.nextDouble() - 0.5) * 0.05;
            double offsetY = (serverWorld.random.nextDouble() - 0.5) * 0.05;
            double offsetZ = (serverWorld.random.nextDouble() - 0.5) * 0.05;
            double speed = 0.008 + serverWorld.random.nextDouble() * 0.004;
            double velocityX = (endX - startX) * speed;
            double velocityY = (endY - startY) * speed + 0.003;
            double velocityZ = (endZ - startZ) * speed;
            serverWorld.spawnParticles(particle, particleX + offsetX, particleY + offsetY, particleZ + offsetZ, 1, velocityX, velocityY, velocityZ, 0.0);
        }
    }

    private static void spawnAbsorbBurst(ServerWorld serverWorld, BlockPos pos, ItemStack item) {
        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 1.1;
        double centerZ = pos.getZ() + 0.5;

        for (int i = 0; i < 12; i++) {
            double offsetX = (serverWorld.random.nextDouble() - 0.5) * 0.5;
            double offsetY = serverWorld.random.nextDouble() * 0.3;
            double offsetZ = (serverWorld.random.nextDouble() - 0.5) * 0.5;
            double velocityX = (serverWorld.random.nextDouble() - 0.5) * 0.02;
            double velocityY = 0.04 + serverWorld.random.nextDouble() * 0.02;
            double velocityZ = (serverWorld.random.nextDouble() - 0.5) * 0.02;
            serverWorld.spawnParticles(ParticleTypes.WITCH, centerX + offsetX, centerY + offsetY, centerZ + offsetZ, 1, velocityX, velocityY, velocityZ, 0.0);
        }

        if (!item.isEmpty()) {
            ItemStackParticleEffect itemParticle = new ItemStackParticleEffect(ParticleTypes.ITEM, item);
            for (int i = 0; i < 8; i++) {
                double offsetX = (serverWorld.random.nextDouble() - 0.5) * 0.2;
                double offsetY = serverWorld.random.nextDouble() * 0.2;
                double offsetZ = (serverWorld.random.nextDouble() - 0.5) * 0.2;
                double velocityX = (serverWorld.random.nextDouble() - 0.5) * 0.005;
                double velocityY = 0.015 + serverWorld.random.nextDouble() * 0.005;
                double velocityZ = (serverWorld.random.nextDouble() - 0.5) * 0.005;
                serverWorld.spawnParticles(itemParticle, centerX + offsetX, centerY + offsetY, centerZ + offsetZ, 1, velocityX, velocityY, velocityZ, 0.0);
            }
        }

        serverWorld.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.4F, 1.2F + serverWorld.random.nextFloat() * 0.2F);
    }

    private static void completeRitual(World world, BlockPos pos, RitualTableBlockEntity blockEntity) {
        blockEntity.transformTicksRemaining = 0;
        blockEntity.totalTransformTicks = 0;

        ItemStack output = blockEntity.pendingOutput.copy();
        blockEntity.pendingOutput = ItemStack.EMPTY;
        blockEntity.activeBraziers = Collections.emptyList();
        blockEntity.nextBrazierIndex = 0;
        blockEntity.cachedParticleItem = ItemStack.EMPTY;

        if (output.isEmpty()) {
            blockEntity.markDirty();
            blockEntity.sync();
            return;
        }

        ItemStack one = output.copy();
        one.setCount(1);
        blockEntity.inventory.set(SLOT, one);

        for (BlockPos cropPos : blockEntity.grownCrops) {
            BlockState cropState = world.getBlockState(cropPos);
            if (!(cropState.getBlock() instanceof CropBlock)) {
                continue;
            }

            IntProperty ageProperty = null;
            for (var property : cropState.getProperties()) {
                if (property instanceof IntProperty intProperty && "age".equals(intProperty.getName())) {
                    ageProperty = intProperty;
                    break;
                }
            }

            if (ageProperty == null || !cropState.contains(ageProperty)) {
                continue;
            }

            world.setBlockState(cropPos, cropState.with(ageProperty, 0), 3);
        }

        world.playSound(null, pos, ModSoundEvents.RITUAL_SUCCESS, SoundCategory.BLOCKS, 0.8F, 1.0F);
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ModParticleType.LEAVES, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 15, 0.3, 0.3, 0.3, 0.0);
        }

        blockEntity.markDirty();
        blockEntity.sync();
    }

    private static void cancelRitual(World world, BlockPos pos, RitualTableBlockEntity blockEntity) {
        blockEntity.transformTicksRemaining = 0;
        blockEntity.totalTransformTicks = 0;
        blockEntity.pendingOutput = ItemStack.EMPTY;
        blockEntity.activeBraziers = Collections.emptyList();
        blockEntity.nextBrazierIndex = 0;
        blockEntity.cachedParticleItem = ItemStack.EMPTY;

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 12, 0.4, 0.4, 0.4, 0.02);
            PlayerEntity nearest = serverWorld.getClosestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5.0, false);
            if (nearest != null) {
                nearest.sendMessage(Text.translatable("message.hexalia.ritual.stopped_ritual"), true);
            }
        }

        world.playSound(null, pos, SoundEvents.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 0.4F, 0.6F);
        blockEntity.markDirty();
        blockEntity.sync();
    }

    private void sync() {
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        ItemStack stack = inventory.get(SLOT);
        if (!stack.isEmpty()) {
            nbt.put("Inv", stack.writeNbt(new NbtCompound()));
        }

        nbt.putInt("TicksLeft", transformTicksRemaining);
        nbt.putInt("TotalTicks", totalTransformTicks);

        if (!pendingOutput.isEmpty()) {
            nbt.put("PendingOut", pendingOutput.writeNbt(new NbtCompound()));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        inventory.set(SLOT, ItemStack.EMPTY);

        if (nbt.contains("Inv", NbtElement.COMPOUND_TYPE)) {
            inventory.set(SLOT, ItemStack.fromNbt(nbt.getCompound("Inv")));
        }

        transformTicksRemaining = nbt.getInt("TicksLeft");
        totalTransformTicks = nbt.getInt("TotalTicks");
        pendingOutput = nbt.contains("PendingOut", NbtElement.COMPOUND_TYPE)
                ? ItemStack.fromNbt(nbt.getCompound("PendingOut"))
                : ItemStack.EMPTY;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
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
            sync();
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
            sync();
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
            ItemStack one = stack.copy();
            one.setCount(1);
            inventory.set(SLOT, one);
        }

        markDirty();
        sync();
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
        sync();
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP || side == Direction.DOWN) {
            return INPUT_OUTPUT_SLOT;
        }
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction direction) {
        if (direction != Direction.UP) {
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
    public boolean canExtract(int slot, ItemStack stack, Direction direction) {
        if (direction != Direction.DOWN) {
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