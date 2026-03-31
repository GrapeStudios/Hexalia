package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.custom.RitualBrazierBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.astralya.hexalia.util.SidedItemHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = HexaliaMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RitualTableBlockEntity extends BlockEntity implements Container {

    public static final int DURATION = 8 * 20;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return !stack.is(ModItems.HEX_FOCUS.get());
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private final LazyOptional<IItemHandler> southInputOptional;
    private final LazyOptional<IItemHandler> downOutputOptional;
    private final LazyOptional<IItemHandler> lockedOptional;

    private ItemStack cachedParticleItem = ItemStack.EMPTY;
    private List<RitualBrazierBlockEntity> activeBraziers = Collections.emptyList();
    private List<BlockPos> grownCrops = Collections.emptyList();
    private ItemStack pendingOutput = ItemStack.EMPTY;

    private int transformTicksRemaining = 0;
    private int totalTransformTicks = 0;
    private int nextBrazierIndex = 0;
    private float rotation = 0.0F;

    public RitualTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.RITUAL_TABLE.get(), pos, state);
        this.southInputOptional = LazyOptional.of(() -> SidedItemHandlers.view(this.inventory, new int[]{0}, true, false));
        this.downOutputOptional = LazyOptional.of(() -> SidedItemHandlers.view(this.inventory, new int[]{0}, false, true));
        this.lockedOptional = LazyOptional.of(SidedItemHandlers::blocked);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.getStackInSlot(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        return this.inventory.getStackInSlot(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return this.inventory.extractItem(index, count, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return this.inventory.extractItem(index, 1, false);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        ItemStack one = stack.copy();
        one.setCount(1);
        this.inventory.setStackInSlot(index, one);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public float getRenderingRotation() {
        this.rotation = (this.rotation + 0.5F) % 360.0F;
        return this.rotation;
    }

    public void startTransformation(ItemStack output, int durationTicks, List<RitualBrazierBlockEntity> braziers) {
        if (this.transformTicksRemaining > 0) {
            return;
        }
        this.transformTicksRemaining = Math.max(1, durationTicks);
        this.totalTransformTicks = this.transformTicksRemaining;
        this.pendingOutput = output.copy();
        this.activeBraziers = new ArrayList<>(braziers);
        this.nextBrazierIndex = 0;
        this.setChanged();
    }

    public void setGrownCropPositions(List<BlockPos> crops) {
        this.grownCrops = new ArrayList<>(crops);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RitualTableBlockEntity blockEntity) {
        if (blockEntity.transformTicksRemaining <= 0) {
            return;
        }

        if (blockEntity.isEmpty() || hasMissingBrazierItems(blockEntity)) {
            cancelRitual(level, pos, blockEntity);
            return;
        }

        int base = blockEntity.totalTransformTicks > 0 ? blockEntity.totalTransformTicks : DURATION;
        int elapsed = base - blockEntity.transformTicksRemaining;

        handleActiveBraziers(level, pos, blockEntity, elapsed);
        blockEntity.transformTicksRemaining--;

        if (blockEntity.transformTicksRemaining == 0) {
            completeRitual(level, pos, blockEntity);
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

    private static void handleActiveBraziers(Level level, BlockPos pos, RitualTableBlockEntity blockEntity, int elapsed) {
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

            BlockState brazierState = level.getBlockState(brazier.getBlockPos());
            if (brazierState.getBlock() instanceof RitualBrazierBlock
                    && brazierState.hasProperty(RitualBrazierBlock.SALTED)
                    && brazierState.getValue(RitualBrazierBlock.SALTED)) {
                level.setBlock(brazier.getBlockPos(), brazierState.setValue(RitualBrazierBlock.SALTED, false), 3);
            }
        }

        if (currentTime >= 0 && currentTime < ticksPerBrazier && level instanceof ServerLevel serverLevel) {
            spawnItemParticles(serverLevel, blockEntity.cachedParticleItem, brazier.getBlockPos(), pos, currentTime, ticksPerBrazier);
        }

        if (currentTime == ticksPerBrazier - 1) {
            if (level instanceof ServerLevel serverLevel) {
                spawnAbsorbBurst(serverLevel, pos, blockEntity.cachedParticleItem);
            }
            blockEntity.nextBrazierIndex++;
            blockEntity.cachedParticleItem = ItemStack.EMPTY;
        }
    }

    private static void spawnItemParticles(ServerLevel serverLevel, ItemStack item, BlockPos from, BlockPos to, int time, int totalTime) {
        if (item.isEmpty()) {
            return;
        }

        ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, item);

        double startX = from.getX() + 0.5D;
        double startY = from.getY() + 0.4D;
        double startZ = from.getZ() + 0.5D;
        double endX = to.getX() + 0.5D;
        double endY = to.getY() + 1.15D;
        double endZ = to.getZ() + 0.5D;
        double progress = time / (double) totalTime;

        double px = startX + (endX - startX) * progress;
        double py = startY + (endY - startY) * progress;
        double pz = startZ + (endZ - startZ) * progress;

        for (int i = 0; i < 3; i++) {
            double offsetX = (serverLevel.random.nextDouble() - 0.5D) * 0.05D;
            double offsetY = (serverLevel.random.nextDouble() - 0.5D) * 0.05D;
            double offsetZ = (serverLevel.random.nextDouble() - 0.5D) * 0.05D;
            double speed = 0.008D + serverLevel.random.nextDouble() * 0.004D;

            double velocityX = (endX - startX) * speed;
            double velocityY = (endY - startY) * speed + 0.003D;
            double velocityZ = (endZ - startZ) * speed;

            serverLevel.sendParticles(particle, px + offsetX, py + offsetY, pz + offsetZ, 1, velocityX, velocityY, velocityZ, 0.0D);
        }
    }

    private static void spawnAbsorbBurst(ServerLevel serverLevel, BlockPos pos, ItemStack item) {
        double centerX = pos.getX() + 0.5D;
        double centerY = pos.getY() + 1.1D;
        double centerZ = pos.getZ() + 0.5D;

        for (int i = 0; i < 12; i++) {
            double offsetX = (serverLevel.random.nextDouble() - 0.5D) * 0.5D;
            double offsetY = serverLevel.random.nextDouble() * 0.3D;
            double offsetZ = (serverLevel.random.nextDouble() - 0.5D) * 0.5D;
            double velocityX = (serverLevel.random.nextDouble() - 0.5D) * 0.02D;
            double velocityY = 0.04D + serverLevel.random.nextDouble() * 0.02D;
            double velocityZ = (serverLevel.random.nextDouble() - 0.5D) * 0.02D;
            serverLevel.sendParticles(ParticleTypes.WITCH, centerX + offsetX, centerY + offsetY, centerZ + offsetZ, 1, velocityX, velocityY, velocityZ, 0.0D);
        }

        if (!item.isEmpty()) {
            for (int i = 0; i < 8; i++) {
                double offsetX = (serverLevel.random.nextDouble() - 0.5D) * 0.2D;
                double offsetY = serverLevel.random.nextDouble() * 0.2D;
                double offsetZ = (serverLevel.random.nextDouble() - 0.5D) * 0.2D;
                double velocityX = (serverLevel.random.nextDouble() - 0.5D) * 0.005D;
                double velocityY = 0.015D + serverLevel.random.nextDouble() * 0.005D;
                double velocityZ = (serverLevel.random.nextDouble() - 0.5D) * 0.005D;
                serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, item), centerX + offsetX, centerY + offsetY, centerZ + offsetZ, 1, velocityX, velocityY, velocityZ, 0.0D);
            }
        }

        serverLevel.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.4F, 1.2F + serverLevel.random.nextFloat() * 0.2F);
    }

    private static void completeRitual(Level level, BlockPos pos, RitualTableBlockEntity blockEntity) {
        blockEntity.setItem(0, blockEntity.pendingOutput);
        blockEntity.pendingOutput = ItemStack.EMPTY;

        for (BlockPos cropPos : blockEntity.grownCrops) {
            BlockState cropState = level.getBlockState(cropPos);
            if (!(cropState.getBlock() instanceof CropBlock)) {
                continue;
            }

            IntegerProperty ageProperty = null;
            for (Object property : cropState.getProperties()) {
                if (property instanceof IntegerProperty integerProperty && "age".equals(integerProperty.getName())) {
                    ageProperty = integerProperty;
                    break;
                }
            }

            if (ageProperty == null || !cropState.hasProperty(ageProperty)) {
                continue;
            }

            level.setBlock(cropPos, cropState.setValue(ageProperty, 0), 3);
        }

        blockEntity.activeBraziers = Collections.emptyList();
        blockEntity.nextBrazierIndex = 0;

        level.playSound(null, pos, ModSoundEvents.RITUAL_SUCCESS.get(), SoundSource.BLOCKS, 0.8F, 1.0F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticleType.LEAVES.get(), pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 15, 0.3D, 0.3D, 0.3D, 0.0D);
        }
        blockEntity.setChanged();
    }

    private static void cancelRitual(Level level, BlockPos pos, RitualTableBlockEntity blockEntity) {
        blockEntity.transformTicksRemaining = 0;
        blockEntity.totalTransformTicks = 0;
        blockEntity.pendingOutput = ItemStack.EMPTY;
        blockEntity.activeBraziers = Collections.emptyList();
        blockEntity.nextBrazierIndex = 0;
        blockEntity.cachedParticleItem = ItemStack.EMPTY;

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 12, 0.4D, 0.4D, 0.4D, 0.02D);
            Player nearest = serverLevel.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5.0D, false);
            if (nearest != null) {
                nearest.displayClientMessage(Component.translatable("message.hexalia.ritual.stopped_ritual"), true);
            }
        }

        level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 0.4F, 0.6F);
        blockEntity.setChanged();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.southInputOptional.invalidate();
        this.downOutputOptional.invalidate();
        this.lockedOptional.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inv", this.inventory.serializeNBT());
        tag.putInt("TicksLeft", this.transformTicksRemaining);
        tag.putInt("TotalTicks", this.totalTransformTicks);
        if (!this.pendingOutput.isEmpty()) {
            CompoundTag pendingTag = new CompoundTag();
            this.pendingOutput.save(pendingTag);
            tag.put("PendingOut", pendingTag);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.inventory.deserializeNBT(tag.getCompound("Inv"));
        this.transformTicksRemaining = tag.getInt("TicksLeft");
        this.totalTransformTicks = tag.getInt("TotalTicks");
        this.pendingOutput = tag.contains("PendingOut") ? ItemStack.of(tag.getCompound("PendingOut")) : ItemStack.EMPTY;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put("Inv", this.inventory.serializeNBT());
        tag.putInt("TicksLeft", this.transformTicksRemaining);
        tag.putInt("TotalTicks", this.totalTransformTicks);
        if (!this.pendingOutput.isEmpty()) {
            CompoundTag pendingTag = new CompoundTag();
            this.pendingOutput.save(pendingTag);
            tag.put("PendingOut", pendingTag);
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.inventory.deserializeNBT(tag.getCompound("Inv"));
        this.transformTicksRemaining = tag.getInt("TicksLeft");
        this.totalTransformTicks = tag.getInt("TotalTicks");
        this.pendingOutput = tag.contains("PendingOut") ? ItemStack.of(tag.getCompound("PendingOut")) : ItemStack.EMPTY;
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        super.onDataPacket(net, packet);
        if (this.level != null && this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            if (this.transformTicksRemaining > 0) {
                return this.lockedOptional.cast();
            }
            if (side == Direction.DOWN) {
                return this.downOutputOptional.cast();
            }
            return this.southInputOptional.cast();
        }
        return super.getCapability(capability, side);
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject() instanceof RitualTableBlockEntity) {
        }
    }
}