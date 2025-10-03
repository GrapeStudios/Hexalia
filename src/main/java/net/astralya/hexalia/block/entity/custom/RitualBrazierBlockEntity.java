package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.RitualBrazierBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.RitualBrazierRecipe;
import net.astralya.hexalia.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

import java.util.Optional;

@Mod.EventBusSubscriber(modid = HexaliaMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RitualBrazierBlockEntity extends SyncBlockEntity {

    public enum RitualResult { SUCCESS, NO_CELESTIAL_BLOOMS, INVALID_ITEM }

    private final ItemStackHandler inventory;
    private final LazyOptional<IItemHandler> inventoryOptional;
    private boolean isRitualFocusItem;
    private float rotation;

    public RitualBrazierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.RITUAL_BRAZIER_BE.get(), pos, state);
        this.inventory = createHandler();
        this.inventoryOptional = LazyOptional.of(() -> inventory);
        this.isRitualFocusItem = false;
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                inventoryChanged();
            }
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    public RitualResult tryCelestialRitual() {
        if (level == null || isRitualFocusItem) return RitualResult.INVALID_ITEM;

        BlockPos bloomPos = findNearbyCelestialBloom();
        if (bloomPos == null) return RitualResult.NO_CELESTIAL_BLOOMS;

        Optional<RitualBrazierRecipe> match = level.getRecipeManager()
                .getRecipeFor(RitualBrazierRecipe.Type.INSTANCE, new SimpleContainer(getStoredItem()), level);
        if (match.isEmpty()) return RitualResult.INVALID_ITEM;

        ItemStack resultStack = match.get().getResultItem(level.registryAccess());
        Direction eject = getBlockState().getValue(RitualBrazierBlock.FACING).getCounterClockWise();

        ModUtil.spawnItemEntity(
                level,
                resultStack.copy(),
                worldPosition.getX() + 0.5 + (eject.getStepX() * 0.2),
                worldPosition.getY() + 0.2,
                worldPosition.getZ() + 0.5 + (eject.getStepZ() * 0.2),
                eject.getStepX() * 0.2F,
                0.0F,
                eject.getStepZ() * 0.2F
        );

        removeItem();

        if (!level.isClientSide) {
            level.setBlock(bloomPos, Blocks.DEAD_BUSH.defaultBlockState(), 3);
            if (level instanceof ServerLevel server) {
                emitEffects(server, bloomPos);
            }
        }

        return RitualResult.SUCCESS;
    }

    private BlockPos findNearbyCelestialBloom() {
        if (level == null) return null;
        BlockPos origin = getBlockPos();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if (dx == 0 && dz == 0) continue;
                BlockPos check = origin.offset(dx, 0, dz);
                if (level.getBlockState(check).is(ModBlocks.CELESTIAL_BLOOM.get())) {
                    return check;
                }
            }
        }
        return null;
    }

    private void emitEffects(ServerLevel server, BlockPos at) {
        server.sendParticles(
                ModParticleType.LEAVES.get(),
                at.getX() + 0.5, at.getY() + 0.6, at.getZ() + 0.5,
                15, 0.2, 0.25, 0.2, 0.0
        );
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (level != null) {
            level.playSound(null,
                    worldPosition.getX() + 0.5F,
                    worldPosition.getY() + 0.5F,
                    worldPosition.getZ() + 0.5F,
                    sound, SoundSource.BLOCKS, volume, pitch);
        }
    }

    public boolean addItem(ItemStack stack) {
        if (isEmpty() && !stack.isEmpty()) {
            inventory.setStackInSlot(0, stack.split(1));
            isRitualFocusItem = false;
            inventoryChanged();
            return true;
        }
        return false;
    }

    public ItemStack removeItem() {
        if (!isEmpty()) {
            isRitualFocusItem = false;
            ItemStack taken = getStoredItem().split(1);
            inventoryChanged();
            return taken;
        }
        return ItemStack.EMPTY;
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    public ItemStack getStoredItem() {
        return inventory.getStackInSlot(0);
    }

    public boolean isEmpty() {
        return inventory.getStackInSlot(0).isEmpty();
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if (rotation >= 360) rotation = 0;
        return rotation;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        inventoryOptional.invalidate();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putBoolean("IsItemImbued", this.isRitualFocusItem);
        tag.put("Inventory", this.inventory.serializeNBT());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        isRitualFocusItem = tag.getBoolean("IsItemImbued");
        inventory.deserializeNBT(tag.getCompound("Inventory"));
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        if (level != null && level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        isRitualFocusItem = tag.getBoolean("IsItemImbued");
        inventory.deserializeNBT(tag.getCompound("Inventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("IsItemImbued", isRitualFocusItem);
        tag.put("Inventory", inventory.serializeNBT());
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return inventoryOptional.cast();
        return super.getCapability(cap, side);
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject() instanceof RitualBrazierBlockEntity) {
        }
    }
}