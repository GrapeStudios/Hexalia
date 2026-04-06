package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.custom.SmallCauldronBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.gameplay.smallcauldron.SmallCauldronContents;
import net.astralya.hexalia.util.SidedItemHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmallCauldronBlockEntity extends SyncBlockEntity {

    public static final int STIR_ANIM_TICKS = 20;

    private static final String TAG_STIR_ANIM_TICK = "StirAnimTick";

    private final SmallCauldronContents contents = new SmallCauldronContents();

    private final IItemHandler upInputHandler = new UpInputHandler(this);

    private int stirAnimTick;

    private long clientStirStartGameTime;
    private int clientStirStartTick;

    private boolean stirAnimDirty;

    private static final int SPOILED_AURA_INTERVAL_TICKS = 20;
    private static final int SPOILED_POISON_DURATION_TICKS = 60;
    private static final int SPOILED_POISON_AMPLIFIER = 0;

    public SmallCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SMALL_CAULDRON.get(), pos, state);
    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
        if (contents.isCooking() || contents.isSpoiled()) {
            return SidedItemHandlers.blocked();
        }

        return SidedItemHandlers.upOnly(side, upInputHandler);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel server)) {
            return;
        }

        boolean blockLit = state.getValue(SmallCauldronBlock.LIT);

        if (stirAnimTick > 0) {
            stirAnimTick--;
            stirAnimDirty = true;
        }

        contents.tickServer(server, blockLit);

        if (contents.isSpoiled()) {
            applySpoiledAura(server, pos);
        }

        syncIfNeeded();
    }

    private void applySpoiledAura(ServerLevel server, BlockPos pos) {
        if ((server.getGameTime() % SPOILED_AURA_INTERVAL_TICKS) != 0) {
            return;
        }

        AABB area = new AABB(pos).inflate(1.0, 1.0, 1.0);

        for (Player p : server.getEntitiesOfClass(Player.class, area)) {
            p.addEffect(new MobEffectInstance(
                    MobEffects.POISON,
                    SPOILED_POISON_DURATION_TICKS,
                    SPOILED_POISON_AMPLIFIER,
                    false,
                    true,
                    true
            ));
        }
    }

    public boolean canStir(BlockState state, Player player) {
        return contents.canStir(state.getValue(SmallCauldronBlock.LIT));
    }

    public boolean tryStir(BlockState state, Player player) {
        if (!(level instanceof ServerLevel server)) {
            return false;
        }

        if (!contents.canStir(state.getValue(SmallCauldronBlock.LIT))) {
            return false;
        }

        SmallCauldronContents.StirResult result = contents.stir(server);

        stirAnimTick = STIR_ANIM_TICKS;
        stirAnimDirty = true;

        syncIfNeeded();
        return result == SmallCauldronContents.StirResult.STIRRED || result == SmallCauldronContents.StirResult.STARTED_COOKING;
    }

    public void triggerStirAnimation() {
        if (!(level instanceof ServerLevel)) {
            return;
        }

        stirAnimTick = STIR_ANIM_TICKS;
        stirAnimDirty = true;

        syncIfNeeded();
    }

    public int getStirAnimTick() {
        return stirAnimTick;
    }

    public float getStirProgress(float partialTicks) {
        Level level = getLevel();
        if (level == null) {
            return 0.0F;
        }

        if (level.isClientSide) {
            if (clientStirStartTick <= 0) {
                return 0.0F;
            }

            float elapsed = (level.getGameTime() - clientStirStartGameTime) + partialTicks;
            float t = elapsed / (float) clientStirStartTick;
            if (t >= 1.0F) {
                return 0.0F;
            }
            return Mth.clamp(t, 0.0F, 1.0F);
        }

        int tick = getStirAnimTick();
        if (tick <= 0) {
            return 0.0F;
        }

        float t = (STIR_ANIM_TICKS - (tick - partialTicks)) / (float) STIR_ANIM_TICKS;
        return Mth.clamp(t, 0.0F, 1.0F);
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
        return contents.canExtractOneIngredient(getBlockState().getValue(SmallCauldronBlock.LIT));
    }

    public ItemStack extractOneIngredient() {
        if (!(level instanceof ServerLevel)) {
            return ItemStack.EMPTY;
        }

        ItemStack out = contents.extractOneIngredient();
        syncIfNeeded();
        return out;
    }

    public boolean canInsertOne(ItemStack stack) {
        return contents.canInsertOne(stack);
    }

    public boolean insertOneIntoCauldron(ItemStack held) {
        if (!(level instanceof ServerLevel)) {
            return false;
        }

        boolean ok = contents.insertOne(held);
        syncIfNeeded();
        return ok;
    }

    public boolean canScoopMixtureWithRusticBottle() {
        return contents.canScoopMixtureWithRusticBottle();
    }

    public boolean tryScoopBottlePublic(Player player, InteractionHand hand, ItemStack held) {
        if (!(level instanceof ServerLevel server)) {
            return false;
        }

        boolean ok = contents.tryScoopBottle(server, centerX(), topY(), centerZ(), player, hand, held);
        syncIfNeeded();
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

    public boolean tryCleanseSpoiledPublic(Player player, InteractionHand hand, ItemStack held) {
        if (!(level instanceof ServerLevel)) {
            return false;
        }

        boolean ok = contents.tryCleanseSpoiled(player, hand, held);
        syncIfNeeded();
        return ok;
    }

    public boolean canUseWaterContainer(ItemStack stack) {
        return contents.canUseWaterContainer(stack);
    }

    public boolean tryFillWithWaterPublic(Player player, InteractionHand hand, ItemStack held) {
        if (!(level instanceof ServerLevel server)) {
            return false;
        }

        boolean ok = contents.tryUseWaterContainer(server, centerX(), centerY(), centerZ(), player, hand, held);
        syncIfNeeded();
        return ok;
    }

    public void dropAll(Level level) {
        if (level == null || level.isClientSide) {
            return;
        }

        contents.dropAll(level, centerX(), centerY(), centerZ());
        contents.clearDirty();
        stirAnimDirty = false;

        setChanged();
        inventoryChanged();
    }

    private void syncIfNeeded() {
        boolean needs = stirAnimDirty || contents.isDirty();
        if (!needs) {
            return;
        }

        if (contents.isDirty()) {
            contents.clearDirty();
        }

        stirAnimDirty = false;
        inventoryChanged();
    }

    private double centerX() {
        return worldPosition.getX() + 0.5;
    }

    private double centerY() {
        return worldPosition.getY() + 0.5;
    }

    private double centerZ() {
        return worldPosition.getZ() + 0.5;
    }

    private double topY() {
        return worldPosition.getY() + 1.0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_STIR_ANIM_TICK, stirAnimTick);
        contents.save(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        int prevStir = stirAnimTick;

        stirAnimTick = tag.getInt(TAG_STIR_ANIM_TICK);
        if (stirAnimTick < 0) stirAnimTick = 0;
        if (stirAnimTick > STIR_ANIM_TICKS) stirAnimTick = STIR_ANIM_TICKS;

        contents.load(tag, registries);

        Level level = getLevel();
        if (level != null && level.isClientSide) {
            if (stirAnimTick > prevStir) {
                clientStirStartGameTime = level.getGameTime();
                clientStirStartTick = stirAnimTick;
            } else if (stirAnimTick <= 0) {
                clientStirStartTick = 0;
            }
        }

        stirAnimDirty = false;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    private static final class UpInputHandler implements IItemHandler {
        private final SmallCauldronBlockEntity be;

        private UpInputHandler(SmallCauldronBlockEntity be) {
            this.be = be;
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot != 0 || stack.isEmpty()) return stack;
            if (!be.canInsertOne(stack)) return stack;

            ItemStack remainder = stack.copy();
            remainder.shrink(1);

            if (simulate) {
                return remainder;
            }

            ItemStack single = stack.copy();
            single.setCount(1);

            boolean ok = be.insertOneIntoCauldron(single);
            return ok ? remainder : stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 0 && be.canInsertOne(stack);
        }
    }
}