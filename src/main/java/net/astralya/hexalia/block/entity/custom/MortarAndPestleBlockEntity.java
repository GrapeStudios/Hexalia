package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.MortarAndPestleRecipe;
import net.astralya.hexalia.recipe.MortarAndPestleRecipeInput;
import net.astralya.hexalia.util.SidedItemHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.Containers;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class MortarAndPestleBlockEntity extends SyncBlockEntity implements Clearable {

    public static final int SPIN_TICKS = 20;
    public static final int REQUIRED_SPINS = 3;

    public static final int INPUT_0 = 0;
    public static final int INPUT_1 = 1;
    public static final int INPUT_2 = 2;
    public static final int OUTPUT = 3;

    private final ItemStackHandler items;
    private ItemStack pendingResult;

    private int pestleTick;
    private int pestleCount;
    private boolean pestling;

    private final IItemHandler upInputHandler;
    private final IItemHandler downOutputHandler;

    public MortarAndPestleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.MORTAR_AND_PESTLE.get(), pos, state);

        this.items = new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                if (!hasLevel() || level == null) {
                    setChanged();
                    return;
                }

                if (!level.isClientSide) {
                    if (slot != OUTPUT) {
                        recomputePestlingState();
                    }
                    inventoryChanged();
                } else {
                    setChanged();
                }
            }

            @Override
            public int getSlotLimit(int slot) {
                if (slot == OUTPUT) return 64;
                return 1;
            }
        };

        this.upInputHandler = SidedItemHandlers.view(
                items,
                new int[]{INPUT_0, INPUT_1, INPUT_2},
                true,
                false
        );

        this.downOutputHandler = SidedItemHandlers.view(
                items,
                new int[]{OUTPUT},
                false,
                true
        );

        this.pendingResult = ItemStack.EMPTY;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MortarAndPestleBlockEntity be) {
        if (!level.isClientSide) {
            if (level.hasNeighborSignal(pos) && be.canStartSpin()) {
                be.startSpin();
            }
        }

        if (be.pestleTick > 0) {
            be.pestleTick--;
            be.setChanged();

            if (!level.isClientSide && be.pestleTick == 0) {
                be.tryFinishOnSpinEnd();
                be.inventoryChanged();
            }
        }
    }

    public ItemStackHandler getItems() {
        return items;
    }

    public boolean hasAnyInputs() {
        return !items.getStackInSlot(INPUT_0).isEmpty()
                || !items.getStackInSlot(INPUT_1).isEmpty()
                || !items.getStackInSlot(INPUT_2).isEmpty();
    }

    public boolean hasOutput() {
        return !items.getStackInSlot(OUTPUT).isEmpty();
    }

    public IItemHandler getItemHandler(Direction side) {
        return SidedItemHandlers.upDown(side, upInputHandler, downOutputHandler);
    }

    public int getPestleTick() {
        return pestleTick;
    }

    public boolean canInsertOne(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (hasOutput()) return false;

        return items.getStackInSlot(INPUT_0).isEmpty()
                || items.getStackInSlot(INPUT_1).isEmpty()
                || items.getStackInSlot(INPUT_2).isEmpty();
    }

    public boolean insertOneIntoNextEmpty(ItemStack held) {
        if (!canInsertOne(held)) return false;

        ItemStack one = held.copy();
        one.setCount(1);

        for (int slot = INPUT_0; slot <= INPUT_2; slot++) {
            if (items.getStackInSlot(slot).isEmpty()) {
                items.setStackInSlot(slot, one);
                return true;
            }
        }

        return false;
    }

    public ItemStack extractOneInput() {
        for (int slot = INPUT_2; slot >= INPUT_0; slot--) {
            ItemStack s = items.getStackInSlot(slot);
            if (!s.isEmpty()) {
                ItemStack out = s.copy();
                out.setCount(1);
                items.setStackInSlot(slot, ItemStack.EMPTY);

                if (level != null && !level.isClientSide) {
                    recomputePestlingState();
                    inventoryChanged();
                } else {
                    setChanged();
                }

                return out;
            }
        }

        return ItemStack.EMPTY;
    }

    public ItemStack takeOutputOne() {
        ItemStack out = items.getStackInSlot(OUTPUT);
        if (out.isEmpty()) return ItemStack.EMPTY;

        ItemStack give = out.copy();
        give.setCount(1);

        out.shrink(1);
        items.setStackInSlot(OUTPUT, out.isEmpty() ? ItemStack.EMPTY : out);

        if (level != null && !level.isClientSide) inventoryChanged();
        else setChanged();

        return give;
    }

    public void recomputePestlingState() {
        if (level == null || level.isClientSide) return;

        if (hasOutput() || !hasAnyInputs()) {
            pendingResult = ItemStack.EMPTY;
            pestling = false;
            pestleTick = 0;
            pestleCount = 0;
            return;
        }

        ItemStack newResult = computeRecipeResult();
        boolean valid = !newResult.isEmpty();

        if (!ItemStack.isSameItemSameComponents(pendingResult, newResult) || pendingResult.getCount() != newResult.getCount()) {
            pestleTick = 0;
            pestleCount = 0;
        }

        pendingResult = newResult;
        pestling = valid;

        if (!valid) {
            pestleTick = 0;
            pestleCount = 0;
        }
    }

    private ItemStack computeRecipeResult() {
        if (level == null) return ItemStack.EMPTY;

        MortarAndPestleRecipeInput input = new MortarAndPestleRecipeInput(
                items.getStackInSlot(INPUT_0),
                items.getStackInSlot(INPUT_1),
                items.getStackInSlot(INPUT_2)
        );

        Optional<RecipeHolder<MortarAndPestleRecipe>> match =
                level.getRecipeManager().getRecipeFor(ModRecipes.MORTAR_AND_PESTLE_TYPE.get(), input, level);

        return match.map(mortarAndPestleRecipeRecipeHolder ->
                mortarAndPestleRecipeRecipeHolder.value().getResultItem(level.registryAccess()).copy()).orElse(ItemStack.EMPTY);

    }

    public boolean canStartSpin() {
        return !hasOutput() && !pendingResult.isEmpty() && pestleTick <= 0 && pestleCount < REQUIRED_SPINS;
    }

    public boolean startSpin() {
        if (level == null) return false;

        if (!level.isClientSide) {
            recomputePestlingState();
        }

        if (!canStartSpin()) return false;

        pestling = true;
        pestleTick = SPIN_TICKS;
        pestleCount++;

        if (level instanceof ServerLevel server) {
            spawnCrushParticles(server);
        }

        if (!level.isClientSide) inventoryChanged();
        else setChanged();

        return true;
    }

    private void tryFinishOnSpinEnd() {
        if (level == null || level.isClientSide) return;
        if (!pestling) return;
        if (pestleCount < REQUIRED_SPINS) return;
        if (pendingResult.isEmpty()) return;
        if (hasOutput()) return;

        items.setStackInSlot(OUTPUT, pendingResult.copy());
        items.setStackInSlot(INPUT_0, ItemStack.EMPTY);
        items.setStackInSlot(INPUT_1, ItemStack.EMPTY);
        items.setStackInSlot(INPUT_2, ItemStack.EMPTY);

        pendingResult = ItemStack.EMPTY;
        pestling = false;
        pestleTick = 0;
        pestleCount = 0;
    }

    private void spawnCrushParticles(ServerLevel server) {
        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY() + 0.20;
        double z = worldPosition.getZ() + 0.5;

        for (int slot = INPUT_0; slot <= INPUT_2; slot++) {
            ItemStack stack = items.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, stack);

            for (int i = 0; i < 3; i++) {
                double ox = (server.random.nextDouble() - 0.5) * 0.12;
                double oy = server.random.nextDouble() * 0.06;
                double oz = (server.random.nextDouble() - 0.5) * 0.12;

                double vx = (server.random.nextDouble() - 0.5) * 0.03;
                double vy = 0.02 + server.random.nextDouble() * 0.02;
                double vz = (server.random.nextDouble() - 0.5) * 0.03;

                server.sendParticles(particle, x + ox, y + oy, z + oz, 1, vx, vy, vz, 0.0);
            }
        }
    }

    public void drops() {
        if (level == null) return;

        SimpleContainer container = new SimpleContainer(items.getSlots());
        for (int i = 0; i < items.getSlots(); i++) {
            container.setItem(i, items.getStackInSlot(i));
        }

        Containers.dropContents(level, worldPosition, container);

        for (int i = 0; i < items.getSlots(); i++) {
            items.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Items", items.serializeNBT(registries));
        tag.putInt("PestleTick", pestleTick);
        tag.putInt("PestleCount", pestleCount);
        tag.putBoolean("Pestling", pestling);

        if (!pendingResult.isEmpty()) {
            CompoundTag resultTag = new CompoundTag();
            pendingResult.save(registries, resultTag);
            tag.put("PendingResult", resultTag);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("Items", Tag.TAG_COMPOUND)) {
            items.deserializeNBT(registries, tag.getCompound("Items"));
        }

        pestleTick = tag.getInt("PestleTick");
        pestleCount = tag.getInt("PestleCount");
        pestling = tag.getBoolean("Pestling");

        if (tag.contains("PendingResult", Tag.TAG_COMPOUND)) {
            pendingResult = ItemStack.parseOptional(registries, tag.getCompound("PendingResult"));
        } else {
            pendingResult = ItemStack.EMPTY;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldPosition);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < items.getSlots(); i++) {
            items.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
}