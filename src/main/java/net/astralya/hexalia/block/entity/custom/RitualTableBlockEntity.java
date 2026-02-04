package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.custom.RitualBrazierBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RitualTableBlockEntity extends BlockEntity implements Container {

    public static final int DURATION = 8 * 20;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return !stack.is(ModItems.HEX_FOCUS);
        }
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.invalidateCapabilities(getBlockPos());
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private ItemStack cachedParticleItem = ItemStack.EMPTY;
    private List<RitualBrazierBlockEntity> activeBraziers = Collections.emptyList();
    private List<BlockPos> grownCrops = Collections.emptyList();
    private ItemStack pendingOutput = ItemStack.EMPTY;

    private int transformTicksRemaining = 0;
    private int totalTransformTicks = 0;
    private int nextBrazierIndex = 0;
    private float rotation = 0.0f;

    public RitualTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.RITUAL_TABLE.get(), pos, state);
    }

    public ItemStackHandler getItemHandler() { return inventory; }

    @Override public int getContainerSize() { return 1; }
    @Override public boolean isEmpty() { return inventory.getStackInSlot(0).isEmpty(); }
    @Override public ItemStack getItem(int i) { return inventory.getStackInSlot(i); }
    @Override public ItemStack removeItem(int i,int c){ return inventory.extractItem(i, c, false); }
    @Override public ItemStack removeItemNoUpdate(int i){ return inventory.extractItem(i, 1, false); }
    @Override public void setItem(int i, ItemStack s) { inventory.setStackInSlot(i, s.copyWithCount(1)); }
    @Override public boolean stillValid(Player p) { return Container.stillValidBlockEntity(this, p); }
    @Override public void clearContent() { inventory.setStackInSlot(0, ItemStack.EMPTY); }

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
        setChanged();
    }

    public void setGrownCropPositions(List<BlockPos> crops) {
        this.grownCrops = new ArrayList<>(crops);
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
            if (bs.getBlock() instanceof RitualBrazierBlock
                    && bs.hasProperty(RitualBrazierBlock.SALTED)
                    && bs.getValue(RitualBrazierBlock.SALTED)) {
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

    private static void spawnItemParticles(ServerLevel server, ItemStack item, BlockPos from, BlockPos to,
                                           int time, int totalTime) {
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
        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 1.1;
        double centerZ = pos.getZ() + 0.5;

        for (int i = 0; i < 12; i++) {
            double offsetX = (server.random.nextDouble() - 0.5) * 0.5;
            double offsetY = server.random.nextDouble() * 0.3;
            double offsetZ = (server.random.nextDouble() - 0.5) * 0.5;
            double velX = (server.random.nextDouble() - 0.5) * 0.02;
            double velY = 0.04 + server.random.nextDouble() * 0.02;
            double velZ = (server.random.nextDouble() - 0.5) * 0.02;
            server.sendParticles(ParticleTypes.WITCH, centerX + offsetX, centerY + offsetY, centerZ + offsetZ, 1, velX, velY, velZ, 0.0);
        }

        if (!item.isEmpty()) {
            for (int i = 0; i < 8; i++) {
                double offsetX = (server.random.nextDouble() - 0.5) * 0.2;
                double offsetY = server.random.nextDouble() * 0.2;
                double offsetZ = (server.random.nextDouble() - 0.5) * 0.2;
                double velX = (server.random.nextDouble() - 0.5) * 0.005;
                double velY = 0.015 + server.random.nextDouble() * 0.005;
                double velZ = (server.random.nextDouble() - 0.5) * 0.005;
                server.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, item), centerX + offsetX, centerY + offsetY, centerZ + offsetZ, 1, velX, velY, velZ, 0.0);
            }
        }

        server.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.4f, 1.2f + server.random.nextFloat() * 0.2f);
    }

    private static void completeRitual(Level level, BlockPos pos, RitualTableBlockEntity be) {
        be.setItem(0, be.pendingOutput);
        be.pendingOutput = ItemStack.EMPTY;

        for (BlockPos cropPos : be.grownCrops) {
            BlockState state = level.getBlockState(cropPos);
            if (!(state.getBlock() instanceof CropBlock)) {
                continue;
            }

            IntegerProperty ageProp = null;
            for (var prop : state.getProperties()) {
                if (prop instanceof IntegerProperty ip && "age".equals(ip.getName())) {
                    ageProp = ip;
                    break;
                }
            }

            if (ageProp == null || !state.hasProperty(ageProp)) {
                continue;
            }

            level.setBlock(cropPos, state.setValue(ageProp, 0), 3);
        }


        be.activeBraziers = Collections.emptyList();
        be.nextBrazierIndex = 0;

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

        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 12, 0.4, 0.4, 0.4, 0.02);
            Player nearest = server.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, false);
            if (nearest != null) nearest.displayClientMessage(Component.translatable("message.hexalia.ritual.stopped_ritual"), true);
        }
        level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 0.4f, 0.6f);
        be.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        super.saveAdditional(tag, regs);
        tag.put("Inv", inventory.serializeNBT(regs));
        tag.putInt("TicksLeft", this.transformTicksRemaining);
        tag.putInt("TotalTicks", this.totalTransformTicks);
        if (!this.pendingOutput.isEmpty()) {
            tag.put("PendingOut", this.pendingOutput.save(regs));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        super.loadAdditional(tag, regs);
        inventory.deserializeNBT(regs, tag.getCompound("Inv"));
        this.transformTicksRemaining = tag.getInt("TicksLeft");
        this.totalTransformTicks = tag.getInt("TotalTicks");
        this.pendingOutput = tag.contains("PendingOut")
                ? ItemStack.parseOptional(regs, tag.getCompound("PendingOut"))
                : ItemStack.EMPTY;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider regs) {
        return saveWithoutMetadata(regs);
    }
}
