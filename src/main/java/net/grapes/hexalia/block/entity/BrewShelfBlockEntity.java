package net.grapes.hexalia.block.entity;

import net.grapes.hexalia.block.custom.BrewShelfBlock;
import net.grapes.hexalia.screen.BrewShelfMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BrewShelfBlockEntity extends RandomizableContainerBlockEntity {

    public static final int MAX_SIZE = 27;

    private NonNullList<ItemStack> inventory;
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {

        @Override
        protected void onOpen(Level pLevel, BlockPos pPos, BlockState pState) {
            BrewShelfBlockEntity.this.playSound(pState, SoundEvents.BARREL_OPEN);
        }

        @Override
        protected void onClose(Level pLevel, BlockPos pPos, BlockState pState) {
            BrewShelfBlockEntity.this.playSound(pState, SoundEvents.BARREL_CLOSE);
        }

        @Override
        protected void openerCountChanged(Level pLevel, BlockPos pPos, BlockState pState, int pCount, int pOpenCount) {

        }

        @Override
        protected boolean isOwnContainer(Player pPlayer) {
            if (pPlayer.containerMenu instanceof ChestMenu) {
                Container container = ((ChestMenu) pPlayer.containerMenu).getContainer();
                return container == BrewShelfBlockEntity.this;
            } else {
                return false;
            }
        }
    };

    public BrewShelfBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BREW_SHELF_BLOCK_BE.get(), pPos, pBlockState);
        this.inventory = NonNullList.withSize(MAX_SIZE, ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (!this.trySaveLootTable(pTag)) {
            ContainerHelper.saveAllItems(pTag, this.inventory);
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(pTag)) {
            ContainerHelper.loadAllItems(pTag, this.inventory);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BrewShelfMenu(pContainerId, pPlayerInventory, this.inventory);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> pItemStacks) {
        this.inventory = pItemStacks;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.hexalia.brew_shelf");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return ChestMenu.threeRows(pContainerId, pInventory, this);
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    public void startOpen(Player pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()) {
            this.openersCounter.incrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public void stopOpen(Player pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()) {
            this.openersCounter.decrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    void playSound(BlockState pState, SoundEvent pSound) {
        Vec3i vec3i = pState.getValue(BrewShelfBlock.FACING).getNormal();
        double d0 = (double)this.worldPosition.getX() + 0.5D + (double)vec3i.getX() / 2.0D;
        double d1 = (double)this.worldPosition.getY() + 0.5D + (double)vec3i.getY() / 2.0D;
        double d2 = (double)this.worldPosition.getZ() + 0.5D + (double)vec3i.getZ() / 2.0D;
        this.level.playSound(null, d0, d1, d2, pSound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }
}
