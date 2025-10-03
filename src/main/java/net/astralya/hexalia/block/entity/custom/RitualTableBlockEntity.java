package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.custom.RitualBrazierBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RitualTableBlockEntity extends BlockEntity implements Container {

    public static final int DURATION = 8 * 20;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    private LazyOptional<ItemStackHandler> itemCap = LazyOptional.of(() -> inventory);

    private ItemStack cachedParticleItem = ItemStack.EMPTY;
    private List<RitualBrazierBlockEntity> activeBraziers = Collections.emptyList();
    private List<BlockPos> grownCrops = Collections.emptyList();
    private ItemStack pendingOutput = ItemStack.EMPTY;

    private int transformTicksRemaining = 0;
    private int totalTransformTicks = 0;
    private int nextBrazierIndex = 0;
    private float rotation = 0.0f;

    public RitualTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.RITUAL_TABLE_BE.get(), pos, state);
    }

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemCap.cast();
        }
        return super.getCapability(cap, side);
    }

    public void invalidateCaps() {
        super.invalidateCaps();
        itemCap.invalidate();
    }

    public void reviveCaps() {
        super.reviveCaps();
        itemCap = LazyOptional.of(() -> inventory);
    }

    public ItemStackHandler getItemHandler() {
        return inventory;
    }

    public int getContainerSize() {
        return 1;
    }

    public boolean isEmpty() {
        return inventory.getStackInSlot(0).isEmpty();
    }

    public ItemStack getItem(int i) {
        return inventory.getStackInSlot(i);
    }

    public ItemStack removeItem(int index, int count) {
        ItemStack extracted = inventory.extractItem(index, count, false);
        setChanged();
        return extracted;
    }

    public ItemStack removeItemNoUpdate(int index) {
        ItemStack current = inventory.getStackInSlot(index);
        if (!current.isEmpty()) {
            inventory.setStackInSlot(index, ItemStack.EMPTY);
            setChanged();
        }
        return current;
    }

    public void setItem(int index, ItemStack stack) {
        if (!stack.isEmpty()) stack = stack.copyWithCount(1);
        inventory.setStackInSlot(index, stack);
        setChanged();
    }

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        double dx = player.getX() - (worldPosition.getX() + 0.5);
        double dy = player.getY() - (worldPosition.getY() + 0.5);
        double dz = player.getZ() - (worldPosition.getZ() + 0.5);
        return dx * dx + dy * dy + dz * dz <= 64.0;
    }

    public void clearContent() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public int getMaxStackSize() {
        return 1;
    }

    public float getRenderingRotation() {
        rotation = (rotation + 0.5f) % 360f;
        return rotation;
    }

    public void startTransformation(ItemStack output, int durationTicks, List<RitualBrazierBlockEntity> braziers, List<BlockPos> grownCropPositions) {
        if (transformTicksRemaining > 0) return;
        this.transformTicksRemaining = Math.max(1, durationTicks);
        this.totalTransformTicks = this.transformTicksRemaining;
        this.pendingOutput = output.copy();
        this.activeBraziers = new ArrayList<>(braziers);
        this.grownCrops = new ArrayList<>(grownCropPositions);
        this.nextBrazierIndex = 0;
        this.cachedParticleItem = ItemStack.EMPTY;
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState st, RitualTableBlockEntity be) {
        if (be.transformTicksRemaining <= 0) return;
        if (be.isEmpty() || hasMissingBrazierItems(be)) {
            cancelRitual(level, pos, be);
            return;
        }
        int base = be.totalTransformTicks > 0 ? be.totalTransformTicks : DURATION;
        int elapsed = base - be.transformTicksRemaining;
        handleActiveBraziers(level, pos, be, elapsed);
        be.transformTicksRemaining--;
        if (be.transformTicksRemaining == 0) {
            completeRitual(level, pos, be);
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

    private static void handleActiveBraziers(Level level, BlockPos pos, RitualTableBlockEntity be, int elapsed) {
        if (be.activeBraziers.isEmpty() || be.nextBrazierIndex >= be.activeBraziers.size()) return;
        int ticksPerBrazier = 40;
        int currentTime = elapsed - (be.nextBrazierIndex * ticksPerBrazier);
        RitualBrazierBlockEntity brazier = be.activeBraziers.get(be.nextBrazierIndex);
        if (brazier == null) return;

        if (currentTime == 0) {
            be.cachedParticleItem = brazier.getStoredItem().copy();
            brazier.removeItem();
            BlockState bs = level.getBlockState(brazier.getBlockPos());
            if (bs.getBlock() instanceof RitualBrazierBlock && bs.hasProperty(RitualBrazierBlock.SALTED) && bs.getValue(RitualBrazierBlock.SALTED)) {
                level.setBlock(brazier.getBlockPos(), bs.setValue(RitualBrazierBlock.SALTED, false), 3);
            }
        }

        if (currentTime >= 0 && currentTime < ticksPerBrazier && level instanceof ServerLevel server) {
            spawnItemParticles(server, be.cachedParticleItem, brazier.getBlockPos(), pos, currentTime, ticksPerBrazier);
        }

        if (currentTime == ticksPerBrazier - 1) {
            if (level instanceof ServerLevel server) {
                spawnAbsorbBurst(server, pos, be.cachedParticleItem);
            }
            be.nextBrazierIndex++;
            be.cachedParticleItem = ItemStack.EMPTY;
        }
    }

    private static void spawnItemParticles(ServerLevel server, ItemStack item, BlockPos from, BlockPos to, int time, int totalTime) {
        if (item.isEmpty()) return;
        ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, item);

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

            server.sendParticles(particle, px + offsetX, py + offsetY, pz + offsetZ, 1, velX, velY, velZ, 0.0);
        }
    }

    private static void spawnAbsorbBurst(ServerLevel server, BlockPos pos, ItemStack item) {
        double cx = pos.getX() + 0.5, cy = pos.getY() + 1.1, cz = pos.getZ() + 0.5;

        for (int i = 0; i < 12; i++) {
            double ox = (server.random.nextDouble() - 0.5) * 0.5;
            double oy = server.random.nextDouble() * 0.3;
            double oz = (server.random.nextDouble() - 0.5) * 0.5;
            double vx = (server.random.nextDouble() - 0.5) * 0.02;
            double vy = 0.04 + server.random.nextDouble() * 0.02;
            double vz = (server.random.nextDouble() - 0.5) * 0.02;

            server.sendParticles(ParticleTypes.WITCH, cx + ox, cy + oy, cz + oz, 1, vx, vy, vz, 0.0);
        }

        if (!item.isEmpty()) {
            for (int i = 0; i < 8; i++) {
                double ox = (server.random.nextDouble() - 0.5) * 0.2;
                double oy = server.random.nextDouble() * 0.2;
                double oz = (server.random.nextDouble() - 0.5) * 0.2;
                double vx = (server.random.nextDouble() - 0.5) * 0.005;
                double vy = 0.015 + server.random.nextDouble() * 0.005;
                double vz = (server.random.nextDouble() - 0.5) * 0.005;

                server.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, item), cx + ox, cy + oy, cz + oz, 1, vx, vy, vz, 0.0);
            }
        }

        server.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.4f, 1.2f + server.random.nextFloat() * 0.2f);
    }

    private static void completeRitual(Level level, BlockPos pos, RitualTableBlockEntity be) {
        be.setItem(0, be.pendingOutput);
        be.pendingOutput = ItemStack.EMPTY;

        for (BlockPos cropPos : be.grownCrops) {
            BlockState state = level.getBlockState(cropPos);
            if (state.getBlock() instanceof CropBlock crop && state.hasProperty(CropBlock.AGE)) {
                level.setBlock(cropPos, state.setValue(CropBlock.AGE, 0), 3);
            }
        }
        be.activeBraziers = Collections.emptyList();
        be.nextBrazierIndex = 0;
        be.cachedParticleItem = ItemStack.EMPTY;
        be.grownCrops = Collections.emptyList();

        level.playSound(null, pos, ModSoundEvents.RITUAL_SUCCESS.get(), SoundSource.BLOCKS, 0.8f, 1.0f);
        if (level instanceof ServerLevel server) {
            server.sendParticles(ModParticleType.LEAVES.get(), pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 15, 0.3, 0.3, 0.3, 0.0);
        }
        be.setChanged();
    }

    private static void cancelRitual(Level level, BlockPos pos, RitualTableBlockEntity be) {
        be.transformTicksRemaining = 0;
        be.totalTransformTicks = 0;
        be.pendingOutput = ItemStack.EMPTY;
        be.activeBraziers = Collections.emptyList();
        be.nextBrazierIndex = 0;
        be.cachedParticleItem = ItemStack.EMPTY;
        be.grownCrops = Collections.emptyList();

        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 12, 0.4, 0.4, 0.4, 0.02);
            Player nearest = server.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, false);
            if (nearest != null) {
                nearest.displayClientMessage(Component.translatable("message.hexalia.ritual.stopped_ritual"), true);
            }
        }
        level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 0.4f, 0.6f);
        be.setChanged();
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inv", inventory.serializeNBT());
        tag.putInt("TicksLeft", this.transformTicksRemaining);
        tag.putInt("TotalTicks", this.totalTransformTicks);
        if (!this.pendingOutput.isEmpty()) {
            CompoundTag out = new CompoundTag();
            this.pendingOutput.save(out);
            tag.put("PendingOut", out);
        }
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("Inv"));
        this.transformTicksRemaining = tag.getInt("TicksLeft");
        this.totalTransformTicks = tag.getInt("TotalTicks");
        this.pendingOutput = tag.contains("PendingOut") ? ItemStack.of(tag.getCompound("PendingOut")) : ItemStack.EMPTY;
    }

    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void setGrownCropPositions(List<BlockPos> crops) {
        this.grownCrops = new ArrayList<>(crops);
    }

    public static List<BlockPos> collectGrownCrops(Level level, BlockPos tablePos) {
        List<BlockPos> list = new ArrayList<>(16);
        BlockPos[] positions = {
                tablePos.offset(-2, 0, -2), tablePos.offset(-1, 0, -2), tablePos.offset(1, 0, -2), tablePos.offset(2, 0, -2),
                tablePos.offset(-2, 0, -1), tablePos.offset(-1, 0, -1), tablePos.offset(1, 0, -1), tablePos.offset(2, 0, -1),
                tablePos.offset(-2, 0, 1), tablePos.offset(-1, 0, 1), tablePos.offset(1, 0, 1), tablePos.offset(2, 0, 1),
                tablePos.offset(-2, 0, 2), tablePos.offset(-1, 0, 2), tablePos.offset(1, 0, 2), tablePos.offset(2, 0, 2)
        };
        for (BlockPos p : positions) {
            BlockState st = level.getBlockState(p);
            if (st.getBlock() instanceof CropBlock crop && crop.getAge(st) >= crop.getMaxAge()) {
                list.add(p.immutable());
            }
        }
        return list;
    }
}
