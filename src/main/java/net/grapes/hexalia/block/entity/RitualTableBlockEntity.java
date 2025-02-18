package net.grapes.hexalia.block.entity;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.grapes.hexalia.block.custom.RitualBrazierBlock;
import net.grapes.hexalia.networking.ModMessages;
import net.grapes.hexalia.recipe.TransmutationRecipe;
import net.grapes.hexalia.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class RitualTableBlockEntity extends BlockEntity implements ImplementedInventory {

    private int progress = 0;
    private static final int MAX_PROGRESS = 120;
    private boolean ritualInProgress = false;

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public RitualTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RITUAL_TABLE_BE, pos, state);
    }

    public boolean startRitual() {
        return performTransmutation(world, pos);
    }

    public void cancelRitual() {
        ritualInProgress = false;
        progress = 0;
        markDirty();
    }

    public static void tick(ServerWorld world, BlockPos pos, BlockState state, RitualTableBlockEntity ritualTableBlockEntity) {
        if (ritualTableBlockEntity.ritualInProgress) {
            ritualTableBlockEntity.progress++;

            if (ritualTableBlockEntity.progress >= MAX_PROGRESS) {
                ritualTableBlockEntity.completeRitual(world, pos);
            }

            ritualTableBlockEntity.markDirty();
        }
    }

    private void completeRitual(ServerWorld world, BlockPos pos) {
        performRitualEffect(world, pos);
        cancelRitual();
    }

    private void performRitualEffect(ServerWorld world, BlockPos pos) {
        world.spawnParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                20, 0.5, 0.5, 0.5, 0.1);
        world.playSound(null, pos, ModSounds.RITUAL_SUCCESS, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    private void spawnParticleEffect(World world, BlockPos pos, DefaultParticleType particleType, int minParticles, int maxParticles) {
        int particleCount = ThreadLocalRandom.current().nextInt(minParticles, maxParticles + 1);
        for (int i = 0; i < particleCount; i++) {
            double offsetX = ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
            double offsetY = ThreadLocalRandom.current().nextDouble(0, 0.5);
            double offsetZ = ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
            world.addParticle(particleType, pos.getX() + 0.5 + offsetX, pos.getY() + 1.0 + offsetY,
                    pos.getZ() + 0.5 + offsetZ, 0, 0, 0);
        }
    }

    public boolean canStartRitual(World world, BlockPos tablePos) {
        if (!isRitualReady(world, tablePos)) {
            return false;
        }

        ItemStack inputStack = getStack(0);
        if (inputStack.isEmpty()) {
            sendMessageToPlayer(world, tablePos, "message.hexalia.ritual.missing_ingredients");
            spawnParticleEffect(world, tablePos, ParticleTypes.SMOKE, 10, 20);
            return false;
        }

        Optional<TransmutationRecipe> recipeOptional = world.getRecipeManager().getFirstMatch(
                TransmutationRecipe.Type.INSTANCE, this, world
        );

        if (recipeOptional.isEmpty()) {
            sendMessageToPlayer(world, tablePos, "message.hexalia.ritual.missing_ingredients");
            spawnParticleEffect(world, tablePos, ParticleTypes.SMOKE, 10, 20);
            return false;
        }

        TransmutationRecipe recipe = recipeOptional.get();
        boolean hasRequiredSalt = processSaltBlocks(world, tablePos, recipe, false);

        if (!hasRequiredSalt) {
            sendMessageToPlayer(world, tablePos, "message.hexalia.ritual.missing_ingredients");
            spawnParticleEffect(world, tablePos, ParticleTypes.SMOKE, 10, 20);
            return false;
        }

        return true;
    }

    private boolean isRitualReady(World world, BlockPos tablePos) {
        BlockPos[] brazierPositions = {
                tablePos.add(-2, 0, 0),
                tablePos.add(2, 0, 0),
                tablePos.add(0, 0, -2),
                tablePos.add(0, 0, 2)
        };

        for (BlockPos pos : brazierPositions) {
            BlockState blockState = world.getBlockState(pos);
            if (!(blockState.getBlock() instanceof RitualBrazierBlock)){
                sendMessageToPlayer(world, tablePos, "message.hexalia.ritual.missing_brazier");
                spawnParticleEffect(world, tablePos, ParticleTypes.SMOKE, 10, 20);
                return false;
            }

            if (!blockState.get(RitualBrazierBlock.SALTED)) {
                sendMessageToPlayer(world, tablePos, "message.hexalia.ritual.missing_salt");
                spawnParticleEffect(world, tablePos, ParticleTypes.SMOKE, 10, 20);
                return false;
            }
        }

        boolean cropsValid = validateAndResetCrops(world, tablePos, false);
        if (!cropsValid) {
            sendMessageToPlayer(world, tablePos, "message.hexalia.ritual.invalid_crops");
            spawnParticleEffect(world, tablePos, ParticleTypes.SMOKE, 10, 20);
        }
        
        return cropsValid;
    }

    private boolean validateAndResetCrops(World world, BlockPos tablePos, boolean resetCrops) {
        BlockPos[] cropPositions = {
                tablePos.add(-2, 0, -2), tablePos.add(-1, 0, -2), tablePos.add(1, 0, -2), tablePos.add(2, 0, -2),
                tablePos.add(-2, 0, -1), tablePos.add(-1, 0, -1), tablePos.add(1, 0, -1), tablePos.add(2, 0, -1),
                tablePos.add(-2, 0,  1), tablePos.add(-1, 0,  1), tablePos.add(1, 0,  1), tablePos.add(2, 0,  1),
                tablePos.add(-2, 0,  2), tablePos.add(-1, 0,  2), tablePos.add(1, 0,  2), tablePos.add(2, 0,  2)
        };

        boolean allCropsValid = true;

        for (BlockPos pos : cropPositions) {
            BlockState state = world.getBlockState(pos);

            if (state.getBlock() instanceof CropBlock crop) {
                if (crop.getAge(state) < crop.getMaxAge()) {
                    allCropsValid = false;
                } else if (resetCrops) {
                    BlockState resetState = crop.withAge(0);
                    world.setBlockState(pos, resetState, Block.NOTIFY_ALL);
                }
            } else {
                allCropsValid = false;
            }
        }
        return allCropsValid;
    }

    public boolean processSaltBlocks(World world, BlockPos tablePos, TransmutationRecipe recipe, boolean consume) {
        DefaultedList<ItemStack> requiredSaltItems = DefaultedList.of();
        requiredSaltItems.addAll(recipe.getSaltItems());

        BlockPos[] brazierPositions = {
                tablePos.add(-2, 0, 0),
                tablePos.add(2, 0, 0),
                tablePos.add(0, 0, -2),
                tablePos.add(0, 0, 2)
        };

        for (BlockPos pos : brazierPositions) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RitualBrazierBlockEntity brazier) {
                Iterator<ItemStack> iterator = requiredSaltItems.iterator();
                while (iterator.hasNext()) {
                    ItemStack item = iterator.next();
                    if (ItemStack.areEqual(brazier.getStack(0), item)) {
                        iterator.remove();
                        if (consume) {
                            brazier.removeStack(0);
                            brazier.markDirty();

                            BlockState brazierState = world.getBlockState(pos);
                            if (brazierState.get(RitualBrazierBlock.SALTED)) {
                                world.setBlockState(pos, brazierState.with(RitualBrazierBlock.SALTED, false), Block.NOTIFY_ALL);
                            }

                            world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_ALL);
                        }
                        break;
                    }
                }
            }
        }
        return requiredSaltItems.isEmpty();
    }


    private void sendMessageToPlayer(World world, BlockPos pos, String messageKey) {
        if (!world.isClient) {
            PlayerEntity nearestPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, false);
            if (nearestPlayer != null) {
                nearestPlayer.sendMessage(Text.translatable(messageKey), true);
            }
        }
    }

    public boolean performTransmutation(World world, BlockPos pos) {
        SimpleInventory inventory = new SimpleInventory(this.size());
        ItemStack inputStack = this.getStack(0);

        if (!isRitualReady(world, pos)) {
            return false;
        }

        if (inputStack.isEmpty()) {
            sendMessageToPlayer(world, pos, "message.hexalia.ritual.missing_ingredients");
            return false;
        }

        inventory.setStack(0, inputStack);

        Optional<TransmutationRecipe> recipeOptional = world.getRecipeManager().getFirstMatch(
                TransmutationRecipe.Type.INSTANCE, this, world
        );

        if (recipeOptional.isEmpty()) {
            sendMessageToPlayer(world, pos, "message.hexalia.ritual.missing_ingredients");
            return false;
        }

        TransmutationRecipe recipe = recipeOptional.get();

        boolean hasRequiredSalt = processSaltBlocks(world, pos, recipe, false);
        if (!hasRequiredSalt) {
            sendMessageToPlayer(world, pos, "message.hexalia.ritual.missing_ingredients");
            return false;
        }

        processSaltBlocks(world, pos, recipe, true);
        this.removeStack(0, 1);
        this.setStack(0, recipe.getOutput(world.getRegistryManager()).copy());
        this.markDirty();

        world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_ALL);
        validateAndResetCrops(world, pos, true);

        return true;
    }


    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.progress = nbt.getInt("Progress");
        this.ritualInProgress = nbt.getBoolean("RitualInProgress");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("Progress", this.progress);
        nbt.putBoolean("RitualInProgress", this.ritualInProgress);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (!world.isClient()) {
            sync();
        }
    }

    private void sync() {
        if (world instanceof ServerWorld serverWorld) {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeInt(inventory.size());
            for (ItemStack itemStack : inventory) {
                data.writeItemStack(itemStack);
            }
            data.writeBlockPos(getPos());

            for (ServerPlayerEntity player : PlayerLookup.tracking(serverWorld, getPos())) {
                ServerPlayNetworking.send(player, ModMessages.SYNC_ITEM, data);
            }
        }
    }

    public ItemStack getRenderStack() {
        return this.getStack(0);
    }

    public void setInventory(DefaultedList<ItemStack> list) {
        for (int i = 0; i < list.size(); i++) {
            this.inventory.set(i, list.get(i));
        }
        markDirty();
    }

    public boolean addItem(ItemStack itemStack) {
        if (isEmpty() && !itemStack.isEmpty()) {
            setStack(0, itemStack.split(1));
            markDirty();
            return true;
        }
        return false;
    }

    public ItemStack removeItem() {
        if (!isEmpty()) {
            ItemStack itemStack = getStoredItem().split(1);
            markDirty();
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    public boolean isEmpty() {
        return getStack(0).isEmpty();
    }

    public ItemStack getStoredItem() {
        return getStack(0);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        Inventories.writeNbt(nbtCompound, inventory, true);
        return nbtCompound;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
