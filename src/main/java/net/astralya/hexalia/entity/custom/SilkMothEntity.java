package net.astralya.hexalia.entity.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.component.item.MothData;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.gameplay.moths.ai.DriftFlyGoal;
import net.astralya.hexalia.gameplay.moths.ai.HoverAroundLightGoal;
import net.astralya.hexalia.gameplay.moths.ai.LayEggOnLeavesGoal;
import net.astralya.hexalia.gameplay.moths.ai.UnstuckNudgeGoal;
import net.astralya.hexalia.entity.custom.variant.SilkMothVariant;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class SilkMothEntity extends AnimalEntity implements GeoEntity {

    private static final String TAG_VARIANT = "SilkMothVariant";
    private static final String TAG_NAME = "MothName";
    private static final String TAG_EGG_READY = "EggReady";
    private static final String TAG_EGG_COOLDOWN = "EggCooldownTicks";
    private static final String TAG_EGG_POS = "EggLayPos";
    private static final int DEFAULT_EGG_COOLDOWN_TICKS = 20 * 20;

    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(SilkMothEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private boolean eggReady;
    private int eggCooldownTicks;
    private BlockPos eggLayPos;

    public SilkMothEntity(EntityType<? extends AnimalEntity> type, World world) {
        super(type, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16.0F);
        this.setPathfindingPenalty(PathNodeType.COCOA, -1.0F);
        this.setPathfindingPenalty(PathNodeType.FENCE, -1.0F);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 2.0D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.3F)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new EscapeSunlightGoal(this, 1.25D));
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(3, new LayEggOnLeavesGoal(this, 1.0D));
        this.goalSelector.add(4, new TemptGoal(this, 1.1D, Ingredient.ofItems(ModItems.FRAGRANT_NECTAR), false));
        this.goalSelector.add(5, new HoverAroundLightGoal(this, 1.0D));
        this.goalSelector.add(6, new DriftFlyGoal(this, 0.55D));
        this.goalSelector.add(7, new UnstuckNudgeGoal(this));
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation navigation = new BirdNavigation(this, world) {
            @Override
            public boolean isValidPosition(BlockPos pos) {
                return !this.world.getBlockState(pos.down()).isAir();
            }
        };
        navigation.setCanPathThroughDoors(false);
        navigation.setCanSwim(false);
        navigation.setCanEnterOpenDoors(true);
        return navigation;
    }

    @Override
    public Vec3d getLeashOffset() {
        return new Vec3d(0.0D, 0.5F * this.getStandingEyeHeight(), this.getWidth() * 0.2F);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isFlappingWings() {
        return !this.isOnGround();
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(ModItems.FRAGRANT_NECTAR);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.isOnGround() && this.getVelocity().y < 0) {
            this.setVelocity(this.getVelocity().multiply(1, 0.6, 1));
        }
        if (!this.getWorld().isClient && this.eggCooldownTicks > 0) {
            this.eggCooldownTicks--;
        }
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld serverWorld, PassiveEntity other) {
        return ModEntities.SILK_MOTH_ENTITY.create(serverWorld);
    }

    @Override
    public void breed(ServerWorld world, AnimalEntity partner) {
        this.setBreedingAge(6000);
        partner.setBreedingAge(6000);
        this.resetLoveTicks();
        partner.resetLoveTicks();
        if (this.eggCooldownTicks > 0) {
            world.sendEntityStatus(this, (byte) 18);
            return;
        }
        SilkMothEntity eggLayer = this.pickEggLayer(partner);
        if (eggLayer == null) {
            world.sendEntityStatus(this, (byte) 18);
            return;
        }
        BlockPos target = eggLayer.findEggLayPos(world);
        if (target != null) {
            eggLayer.eggReady = true;
            eggLayer.eggCooldownTicks = DEFAULT_EGG_COOLDOWN_TICKS;
            eggLayer.eggLayPos = target;
            world.playSound(null, eggLayer.getBlockPos(), SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        }
        world.sendEntityStatus(this, (byte) 18);
        world.sendEntityStatus(partner, (byte) 18);
    }

    @Nullable
    private SilkMothEntity pickEggLayer(AnimalEntity partner) {
        if (!(partner instanceof SilkMothEntity other)) {
            return null;
        }
        return this.random.nextBoolean() ? this : other;
    }

    @Nullable
    private BlockPos findEggLayPos(ServerWorld world) {
        BlockPos base = this.getBlockPos();
        int radius = 6;
        int down = 10;
        int up = 6;
        BlockPos preferred = this.findEggLayPosAboveNesting(world, base, radius, down, up);
        if (preferred != null) {
            return preferred;
        }
        return this.findEggLayPosAboveLeaves(world, base, radius, down, up);
    }

    @Nullable
    private BlockPos findEggLayPosAboveNesting(ServerWorld world, BlockPos base, int radius, int down, int up) {
        int validCount = 0;
        BlockPos chosen = null;
        for (int dy = -down; dy <= up; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos belowPos = base.add(dx, dy, dz);
                    if (!world.getBlockState(belowPos).isOf(ModBlocks.NESTING_BLOCK)) {
                        continue;
                    }
                    BlockPos placePos = belowPos.up();
                    if (!world.getBlockState(placePos).isAir()) {
                        continue;
                    }
                    validCount++;
                    if (chosen == null || this.random.nextInt(validCount) == 0) {
                        chosen = placePos;
                    }
                }
            }
        }
        return chosen;
    }

    @Nullable
    private BlockPos findEggLayPosAboveLeaves(ServerWorld world, BlockPos base, int radius, int down, int up) {
        int validCount = 0;
        BlockPos chosen = null;
        for (int dy = -down; dy <= up; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos belowPos = base.add(dx, dy, dz);
                    if (!world.getBlockState(belowPos).isIn(BlockTags.LEAVES)) {
                        continue;
                    }
                    BlockPos placePos = belowPos.up();
                    if (!world.getBlockState(placePos).isAir()) {
                        continue;
                    }
                    validCount++;
                    if (chosen == null || this.random.nextInt(validCount) == 0) {
                        chosen = placePos;
                    }
                }
            }
        }
        return chosen;
    }

    public boolean isValidEggTarget(ServerWorld world, BlockPos pos) {
        if (!world.getBlockState(pos).isAir()) {
            return false;
        }
        BlockState below = world.getBlockState(pos.down());
        return below.isIn(BlockTags.LEAVES) || below.isOf(ModBlocks.NESTING_BLOCK);
    }

    public boolean isEggReady() {
        return this.eggReady;
    }

    @Nullable
    public BlockPos getEggLayPos() {
        return this.eggLayPos;
    }

    public void clearEggTarget() {
        this.eggReady = false;
        this.eggLayPos = null;
    }

    public void finishEggLaying() {
        this.eggReady = false;
        this.eggLayPos = null;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack heldItem = player.getStackInHand(hand);
        ActionResult bottleResult = this.tryBottle(player, heldItem);
        if (bottleResult.isAccepted()) {
            return bottleResult;
        }
        return super.interactMob(player, hand);
    }

    private ActionResult tryBottle(PlayerEntity player, ItemStack heldItem) {
        if (!heldItem.isOf(ModItems.RUSTIC_BOTTLE)) {
            return ActionResult.PASS;
        }
        if (this.getWorld().isClient) {
            return ActionResult.SUCCESS;
        }
        ItemStack bottledMoth = new ItemStack(ModItems.BOTTLED_MOTH);
        String name = this.hasCustomName() ? this.getCustomName().getString() : "";
        int variantId = this.getVariant().getId();
        bottledMoth.set(ModComponents.MOTH, new MothData(name, variantId));
        this.remove(RemovalReason.DISCARDED);
        if (!player.getInventory().insertStack(bottledMoth)) {
            this.dropStack(bottledMoth);
        }
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.PLAYERS, 1.0F, 1.0F);
        if (!player.getAbilities().creativeMode) {
            heldItem.decrement(1);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(DATA_ID_TYPE_VARIANT, 0);
    }

    public void setVariant(SilkMothVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    public SilkMothVariant getVariant() {
        return SilkMothVariant.byId(this.dataTracker.get(DATA_ID_TYPE_VARIANT) & 255);
    }

    @Override
    public @Nullable EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason reason,
                                           @Nullable EntityData spawnData) {
        this.setVariant(Util.getRandom(SilkMothVariant.values(), this.random));
        return super.initialize(world, difficulty, reason, spawnData);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putInt(TAG_VARIANT, this.getVariant().getId());
        tag.putBoolean(TAG_EGG_READY, this.eggReady);
        tag.putInt(TAG_EGG_COOLDOWN, this.eggCooldownTicks);
        if (this.eggLayPos != null) {
            tag.putLong(TAG_EGG_POS, this.eggLayPos.asLong());
        }
        if (this.hasCustomName()) {
            tag.putString(TAG_NAME, this.getCustomName().getString());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        if (tag.contains(TAG_VARIANT)) {
            this.setVariant(SilkMothVariant.byId(tag.getInt(TAG_VARIANT)));
        }
        if (tag.contains(TAG_EGG_READY)) {
            this.eggReady = tag.getBoolean(TAG_EGG_READY);
        }
        if (tag.contains(TAG_EGG_COOLDOWN)) {
            this.eggCooldownTicks = tag.getInt(TAG_EGG_COOLDOWN);
        }
        if (tag.contains(TAG_EGG_POS)) {
            this.eggLayPos = BlockPos.fromLong(tag.getLong(TAG_EGG_POS));
        } else {
            this.eggLayPos = null;
        }
        if (tag.contains(TAG_NAME)) {
            this.setCustomName(Text.literal(tag.getString(TAG_NAME)));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        if (!this.isOnGround()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.silkmoth.flying", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.silkmoth.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}