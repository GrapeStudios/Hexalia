package net.astralya.hexalia.entity.custom;

import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.ai.silkmoth.AttractedToLightGoal;
import net.astralya.hexalia.entity.ai.silkmoth.FlyWanderGoal;
import net.astralya.hexalia.entity.custom.variant.SilkMothVariant;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.BottledMothItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class SilkMothEntity extends AnimalEntity implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final TrackedData<Integer> VARIANT =
            DataTracker.registerData(SilkMothEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public SilkMothEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16.0F);
        this.setPathfindingPenalty(PathNodeType.COCOA, -1.0F);
        this.setPathfindingPenalty(PathNodeType.FENCE, -1.0F);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 2.0D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.3f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new AvoidSunlightGoal(this));
        this.goalSelector.add(2, new EscapeSunlightGoal(this, 1.0));
        this.goalSelector.add(3, new AttractedToLightGoal(this, 1.5d));
        this.goalSelector.add(4, new SwimGoal(this));

        this.goalSelector.add(5, new FlyWanderGoal(this, 0.8D));

        this.goalSelector.add(6, new LookAroundGoal(this));
        this.goalSelector.add(7, new LookAtEntityGoal(this, net.minecraft.entity.player.PlayerEntity.class, 6.0F));
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.SILK_MOTH.create(world);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height / 2.0f;
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world) {
            public boolean isValidPosition(BlockPos pos) {
                return !this.world.getBlockState(pos.down()).isAir();
            }
        };
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(false);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushAway(Entity entity) {
    }

    @Override
    protected boolean isFlappingWings() {
        return !this.isOnGround();
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<SilkMothEntity> silkMothAnimationState) {
        if ((!this.isOnGround())) {
            silkMothAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.silkmoth.flying", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
            silkMothAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.silkmoth.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemInHand = player.getStackInHand(hand);

        if (itemInHand.isOf(ModItems.RUSTIC_BOTTLE)) {
            if (!this.getWorld().isClient()) {
                ItemStack mothBottle = new ItemStack(ModItems.BOTTLED_MOTH);

                NbtCompound entityTag = new NbtCompound();
                if (this.hasCustomName()) {
                    entityTag.putString(BottledMothItem.MOTH_NAME, this.getCustomName().getString());
                }
                SilkMothVariant variant = this.getVariant();
                int customModelData = switch (variant) {
                    case BLUE -> 1;
                    case PINK -> 2;
                    case BLACK -> 3;
                    default -> 0;
                };
                entityTag.putInt("CustomModelData", customModelData);
                mothBottle.setNbt(entityTag);
                this.discard();

                if (!player.getInventory().insertStack(mothBottle)) {
                    this.dropStack(mothBottle);
                }

                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.PLAYERS, 1.0F, 1.0F);
                itemInHand.decrement(1);

                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    }


    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
    }

    /* VARIANTS */

    public void setVariant(SilkMothVariant variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    public SilkMothVariant getVariant() {
        return SilkMothVariant.byId(this.getTypeVariant() & 255);
    }

    private int getTypeVariant() {
        return this.dataTracker.get(VARIANT);
    }

    public void setSilkMothVariant(SilkMothVariant variant) {
        this.dataTracker.set(VARIANT, variant.getId() & 255);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                 @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        SilkMothVariant silkVariant = Util.getRandom(SilkMothVariant.values(), this.random);
        setSilkMothVariant(silkVariant);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("SilkMothVariant", this.getVariant().getId());
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("SilkMothVariant")) {
            this.setVariant(SilkMothVariant.byId(nbt.getInt("SilkMothVariant")));
        }
    }
}

