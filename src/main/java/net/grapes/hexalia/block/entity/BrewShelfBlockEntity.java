package net.grapes.hexalia.block.entity;

import net.grapes.hexalia.block.custom.BrewShelfBlock;
import net.grapes.hexalia.screen.BrewShelfScreenHandler;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.tick.OrderedTick;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BrewShelfBlockEntity extends LootableContainerBlockEntity {
    public static final int MAX_SIZE = 27;

    private DefaultedList<ItemStack> inventory;
    private final ViewerCountManager stateManager;

    public BrewShelfBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BREW_SHELF_BE, pos, state);
        this.inventory = DefaultedList.ofSize(MAX_SIZE, ItemStack.EMPTY);
        this.stateManager = new ViewerCountManager() {
            protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
                BrewShelfBlockEntity.this.playSound(state, SoundEvents.BLOCK_BARREL_OPEN);
                BrewShelfBlockEntity.this.setOpen(state, true);
            }

            protected void onContainerClose(World world, BlockPos pos, BlockState state) {
                BrewShelfBlockEntity.this.playSound(state, SoundEvents.BLOCK_BARREL_CLOSE);
                BrewShelfBlockEntity.this.setOpen(state, false);
            }

            protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
            }

            protected boolean isPlayerViewing(PlayerEntity player) {
                if (player.currentScreenHandler instanceof GenericContainerScreenHandler genericContainerScreenHandler) {
                    Inventory inventory = genericContainerScreenHandler.getInventory();
                    return inventory == BrewShelfBlockEntity.this;
                } else {
                    return false;
                }
            }
        };
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("hexalia.container.brew_shelf");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new BrewShelfScreenHandler(syncId, playerInventory, this);
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        inventory = list;
    }

    @Override
    public int size() {
        return MAX_SIZE;
    }

    private boolean isAllowedItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.BREWS);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return isAllowedItem(stack);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.openContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.closeContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory);
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        Inventories.writeNbt(nbtCompound, inventory, true);
        return nbtCompound;
    }

    public void tick() {
        if (!this.removed) {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }
        if (this.stateManager.getViewerCount() > 0) {
            scheduleTick();
        } else {
            BlockState blockstate = getCachedState();
            if (!(blockstate.getBlock() instanceof BrewShelfBlock)) {
                markRemoved();
            }
        }
    }

    private void scheduleTick() {
        Objects.requireNonNull(getWorld()).getBlockTickScheduler().scheduleTick(OrderedTick.create(getCachedState().getBlock(), getPos()));
    }

    void setOpen(BlockState state, boolean open) {
        assert this.world != null;
        this.world.setBlockState(this.getPos(), state.with(BrewShelfBlock.OPEN, open), 3);
    }

    void playSound(BlockState state, SoundEvent soundEvent) {
        Vec3i vec3i = state.get(BrewShelfBlock.FACING).getVector();
        double d = (double) this.pos.getX() + 0.5 + (double) vec3i.getX() / 2.0;
        double e = (double) this.pos.getY() + 0.5 + (double) vec3i.getY() / 2.0;
        double f = (double) this.pos.getZ() + 0.5 + (double) vec3i.getZ() / 2.0;
        assert this.world != null;
        this.world.playSound(null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
    }
}
