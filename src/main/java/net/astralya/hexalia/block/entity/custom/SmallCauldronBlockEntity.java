package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.custom.SmallCauldronBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.gameplay.smallcauldron.SmallCauldronContents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmallCauldronBlockEntity extends BlockEntity implements SidedInventory {
    public static final int STIR_ANIM_TICKS = 20;

    private static final int HOPPER_SLOT_COUNT = 4;
    private static final int[] TOP_SLOTS = new int[]{0, 1, 2, 3};
    private static final int[] NO_SLOTS = new int[0];

    private static final String TAG_STIR_ANIM_TICK = "StirAnimTick";

    private static final int SPOILED_AURA_INTERVAL_TICKS = 20;
    private static final int SPOILED_POISON_DURATION_TICKS = 60;
    private static final int SPOILED_POISON_AMPLIFIER = 0;

    private final SmallCauldronContents contents = new SmallCauldronContents();

    private int stirAnimTick;
    private long clientStirStartGameTime;
    private int clientStirStartTick;
    private boolean stirAnimDirty;

    public SmallCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SMALL_CAULDRON, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, SmallCauldronBlockEntity blockEntity) {
        blockEntity.tick(world, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (!(world instanceof ServerWorld server)) {
            return;
        }

        boolean blockLit = state.get(SmallCauldronBlock.LIT);

        if (stirAnimTick > 0) {
            stirAnimTick--;
            stirAnimDirty = true;
        }

        contents.tickServer(server, blockLit);

        if (contents.isSpoiled()) {
            applySpoiledAura(server, pos);
        }

        syncIfNeeded(world);
    }

    private void applySpoiledAura(ServerWorld server, BlockPos pos) {
        if ((server.getTime() % SPOILED_AURA_INTERVAL_TICKS) != 0) {
            return;
        }

        Box area = new Box(pos).expand(1.0, 1.0, 1.0);
        for (PlayerEntity player : server.getEntitiesByClass(PlayerEntity.class, area, entity -> true)) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.POISON,
                    SPOILED_POISON_DURATION_TICKS,
                    SPOILED_POISON_AMPLIFIER,
                    false,
                    true,
                    true
            ));
        }
    }

    public boolean canStir(BlockState state, PlayerEntity player) {
        return contents.canStir(state.get(SmallCauldronBlock.LIT));
    }

    public boolean tryStir(BlockState state, PlayerEntity player) {
        if (!(world instanceof ServerWorld server)) {
            return false;
        }
        if (!contents.canStir(state.get(SmallCauldronBlock.LIT))) {
            return false;
        }

        SmallCauldronContents.StirResult result = contents.stir(server);
        stirAnimTick = STIR_ANIM_TICKS;
        stirAnimDirty = true;
        syncIfNeeded(world);

        return result == SmallCauldronContents.StirResult.STIRRED
                || result == SmallCauldronContents.StirResult.STARTED_COOKING;
    }

    public void triggerStirAnimation() {
        if (!(world instanceof ServerWorld)) {
            return;
        }
        stirAnimTick = STIR_ANIM_TICKS;
        stirAnimDirty = true;
        syncIfNeeded(world);
    }

    public int getStirAnimTick() {
        return stirAnimTick;
    }

    public float getStirProgress(float partialTicks) {
        if (world == null) {
            return 0.0F;
        }

        if (world.isClient) {
            if (clientStirStartTick <= 0) {
                return 0.0F;
            }
            float elapsed = (world.getTime() - clientStirStartGameTime) + partialTicks;
            float t = elapsed / (float) clientStirStartTick;
            if (t >= 1.0F) {
                return 0.0F;
            }
            return MathHelper.clamp(t, 0.0F, 1.0F);
        }

        int tick = getStirAnimTick();
        if (tick <= 0) {
            return 0.0F;
        }

        float t = (STIR_ANIM_TICKS - (tick - partialTicks)) / (float) STIR_ANIM_TICKS;
        return MathHelper.clamp(t, 0.0F, 1.0F);
    }

    public List<ItemStack> getIngredientsForRender() {
        return contents.getIngredientsForRender();
    }

    public float getLiquidFill01() {
        return contents.getLiquidFill01();
    }

    public float getVisualLiquidFill01() {
        return contents.getVisualLiquidFill01();
    }

    public int getVisualLiquidColor() {
        return contents.getVisualLiquidColor();
    }

    public boolean isSpoiled() {
        return contents.isSpoiled();
    }

    public boolean isCooking() {
        return contents.isCooking();
    }

    public boolean hasMixture() {
        return contents.hasMixture();
    }

    public boolean isOvercooked() {
        return contents.isOvercooked();
    }

    public int getMixtureBaseColor() {
        return contents.getMixtureBaseColor();
    }

    public boolean canExtractOneIngredient() {
        return contents.canExtractOneIngredient(getCachedState().get(SmallCauldronBlock.LIT));
    }

    public ItemStack extractOneIngredient() {
        if (!(world instanceof ServerWorld)) {
            return ItemStack.EMPTY;
        }
        ItemStack out = contents.extractOneIngredient();
        syncIfNeeded(world);
        return out;
    }

    public boolean canInsertOne(ItemStack stack) {
        return contents.canInsertOne(stack);
    }

    public boolean insertOneIntoCauldron(ItemStack held) {
        if (!(world instanceof ServerWorld)) {
            return false;
        }
        boolean ok = contents.insertOne(held);
        syncIfNeeded(world);
        return ok;
    }

    public boolean canScoopMixtureWithRusticBottle() {
        return contents.canScoopMixtureWithRusticBottle();
    }

    public boolean tryScoopBottlePublic(PlayerEntity player, Hand hand, ItemStack held) {
        if (!(world instanceof ServerWorld server)) {
            return false;
        }
        boolean ok = contents.tryScoopBottle(server, centerX(), topY(), centerZ(), player, hand, held);
        syncIfNeeded(world);
        return ok;
    }

    public boolean isRusticBottle(ItemStack stack) {
        return contents.isRusticBottle(stack);
    }

    public boolean isLotusBlossom(ItemStack stack) {
        return contents.isLotusBlossom(stack);
    }

    public boolean isWaterContainer(ItemStack stack) {
        return contents.isWaterContainer(stack);
    }

    public boolean canCleanseSpoiledWithLotus() {
        return contents.canCleanseSpoiledWithLotus();
    }

    public boolean tryCleanseSpoiledPublic(PlayerEntity player, Hand hand, ItemStack held) {
        if (!(world instanceof ServerWorld)) {
            return false;
        }
        boolean ok = contents.tryCleanseSpoiled(player, hand, held);
        syncIfNeeded(world);
        return ok;
    }

    public boolean canUseWaterContainer(ItemStack stack) {
        return contents.canUseWaterContainer(stack);
    }

    public boolean tryFillWithWaterPublic(PlayerEntity player, Hand hand, ItemStack held) {
        if (!(world instanceof ServerWorld server)) {
            return false;
        }
        boolean ok = contents.tryUseWaterContainer(server, centerX(), centerY(), centerZ(), player, hand, held);
        syncIfNeeded(world);
        return ok;
    }

    public void dropAll(World world) {
        if (world == null || world.isClient) {
            return;
        }
        contents.dropAll(world, centerX(), centerY(), centerZ());
        contents.clearDirty();
        stirAnimDirty = false;
        markDirty();
        syncIfNeeded(world);
    }

    private void syncIfNeeded(@Nullable World world) {
        boolean needs = stirAnimDirty || contents.isDirty();
        if (!needs) {
            return;
        }

        if (contents.isDirty()) {
            contents.clearDirty();
        }

        stirAnimDirty = false;
        markDirty();

        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    private double centerX() {
        return pos.getX() + 0.5;
    }

    private double centerY() {
        return pos.getY() + 0.5;
    }

    private double centerZ() {
        return pos.getZ() + 0.5;
    }

    private double topY() {
        return pos.getY() + 1.0;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt(TAG_STIR_ANIM_TICK, stirAnimTick);
        contents.save(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        int prevStir = stirAnimTick;
        stirAnimTick = nbt.getInt(TAG_STIR_ANIM_TICK);

        if (stirAnimTick < 0) {
            stirAnimTick = 0;
        }
        if (stirAnimTick > STIR_ANIM_TICKS) {
            stirAnimTick = STIR_ANIM_TICKS;
        }

        contents.load(nbt);

        if (world != null && world.isClient) {
            if (stirAnimTick > prevStir) {
                clientStirStartGameTime = world.getTime();
                clientStirStartTick = stirAnimTick;
            } else if (stirAnimTick <= 0) {
                clientStirStartTick = 0;
            }
        }

        stirAnimDirty = false;
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public int size() {
        return HOPPER_SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : contents.getIngredientsForRender()) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot < 0 || slot >= HOPPER_SLOT_COUNT) {
            return ItemStack.EMPTY;
        }

        List<ItemStack> stacks = contents.getIngredientsForRender();
        return slot < stacks.size() ? stacks.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (amount <= 0 || slot < 0 || slot >= HOPPER_SLOT_COUNT) {
            return ItemStack.EMPTY;
        }
        if (!(world instanceof ServerWorld)) {
            return ItemStack.EMPTY;
        }
        if (getStack(slot).isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack removed = contents.extractOneIngredient();
        syncIfNeeded(world);
        return removed;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot < 0 || slot >= HOPPER_SLOT_COUNT) {
            return ItemStack.EMPTY;
        }
        if (!(world instanceof ServerWorld)) {
            return ItemStack.EMPTY;
        }
        if (getStack(slot).isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack removed = contents.extractOneIngredient();
        syncIfNeeded(world);
        return removed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= HOPPER_SLOT_COUNT) {
            return;
        }
        if (!(world instanceof ServerWorld)) {
            return;
        }
        if (stack.isEmpty()) {
            return;
        }
        if (!getStack(slot).isEmpty()) {
            return;
        }

        contents.insertOne(stack);
        syncIfNeeded(world);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot < 0 || slot >= HOPPER_SLOT_COUNT) {
            return false;
        }
        if (stack.isEmpty()) {
            return false;
        }
        if (!getStack(slot).isEmpty()) {
            return false;
        }
        return contents.canInsertOne(stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return world != null
                && world.getBlockEntity(pos) == this
                && player.squaredDistanceTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clear() {
        if (world == null || world.isClient) {
            return;
        }
        dropAll(world);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return side == Direction.UP ? TOP_SLOTS : NO_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir != Direction.UP) {
            return false;
        }
        return isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }
}