package net.grapes.hexalia.block.entity;

import net.grapes.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RitualBrazierBlockEntity extends BlockEntity implements WorldlyContainer {

    private static final int MOONLIGHT_DURATION = 200;
    private int timer = 0;
    private NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

    private static final Map<Item, Item> TRANSFORMATIONS = Map.of(
            Items.AMETHYST_SHARD, ModItems.MOON_CRYSTAL.get(),
            Items.GLOW_BERRIES, ModItems.MOON_BERRIES.get()
    );

    public RitualBrazierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RITUAL_BRAZIER_BE.get(), pos, state);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, RitualBrazierBlockEntity pBlockEntity) {
        if (pLevel.isClientSide) return;

        ItemStack itemStack = pBlockEntity.getItem(0);
        Item resultItem = TRANSFORMATIONS.get(itemStack.getItem());

        if (resultItem != null) {
            pBlockEntity.timer++;

            if (pBlockEntity.timer >= MOONLIGHT_DURATION) {
                if (isNight(pLevel) && isExposedToMoon(pLevel, pPos)) {
                    pBlockEntity.setItem(0, ItemStack.EMPTY);
                    pBlockEntity.timer = 0;
                    pBlockEntity.setChanged();

                    pLevel.sendBlockUpdated(pPos, pState, pState, Block.UPDATE_ALL);

                    ItemStack resultStack = new ItemStack(resultItem);
                    Containers.dropItemStack(pLevel, pPos.getX() + 0.5, pPos.getY() + 1.0, pPos.getZ() + 0.5, resultStack);

                    spawnParticles(pLevel, pPos);
                    pLevel.playSound(null, pPos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
                } else {
                    pBlockEntity.timer = 0;
                }
            }
        } else {
            pBlockEntity.timer = 0;
        }
    }

    private static void spawnParticles(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;

            serverLevel.sendParticles(ParticleTypes.POOF, x, y, z, 10, 0.2, 0.2, 0.2, 0.02);
        }
    }

    private static boolean isNight(Level level) {
        long time = level.getDayTime();
        return time > 13000 && time < 23000;
    }

    private static boolean isExposedToMoon(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.canSeeSky(pos);
        }
        return false;
    }

    @Override
    public int[] getSlotsForFace(@NotNull Direction direction) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return true;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return inventory.get(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= this.inventory.size()) {
            return ItemStack.EMPTY;
        }
        return this.inventory.get(slot);
    }

    public ItemStack getRenderStack() {
        return inventory.get(0);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(inventory, pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(inventory, pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        inventory.set(pSlot, pStack);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return worldPosition.distSqr(pPlayer.blockPosition()) <= 16;
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.inventory = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, inventory);
        this.timer = pTag.getInt("Timer");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        ContainerHelper.saveAllItems(pTag, inventory);
        pTag.putInt("Timer", timer);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public boolean addStack(ItemStack itemStack) {
        if (isEmpty() && !itemStack.isEmpty()) {
            setItem(0, itemStack.split(1));
            setChanged();
            return true;
        }
        return false;
    }

    public ItemStack removeStack() {
        if (!isEmpty()) {
            ItemStack itemStack = getItem(0).split(1);
            setChanged();
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}