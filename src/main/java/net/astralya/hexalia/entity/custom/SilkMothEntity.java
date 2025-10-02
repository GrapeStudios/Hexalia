package net.astralya.hexalia.entity.custom;

import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.component.item.MothData;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.ai.silkmoth.AttractedToLightGoal;
import net.astralya.hexalia.entity.ai.silkmoth.FlyWanderGoal;
import net.astralya.hexalia.entity.custom.variant.SilkMothVariant;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.SpawnReason;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
import software.bernie.geckolib.animation.*;

import java.util.Objects;

public class SilkMothEntity extends AnimalEntity implements GeoEntity, Flutterer {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
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

    @Override
    protected Vec3d getLeashOffset() {
        return new Vec3d(0.0, 0.5F * this.getHeight(), this.getWidth() * 0.2F);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected boolean isFlappingWings() {
        return !this.isOnGround();
    }


    @Override
    public boolean isInAir() {
        return !this.isOnGround();
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {

    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation nav = new BirdNavigation(this, world);
        nav.setCanPathThroughDoors(false);
        nav.setCanSwim(false);
        nav.setCanEnterOpenDoors(true);
        return nav;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.SILK_MOTH.create(world);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        if (!this.isOnGround()) {
            state.getController().setAnimation(
                    RawAnimation.begin().then("animation.silkmoth.flying", Animation.LoopType.LOOP)
            );
        } else {
            state.getController().setAnimation(
                    RawAnimation.begin().then("animation.silkmoth.idle", Animation.LoopType.LOOP)
            );
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack heldItem = player.getStackInHand(hand);

        if (heldItem.isOf(ModItems.RUSTIC_BOTTLE) && !this.getWorld().isClient()) {
            ItemStack bottledMoth = new ItemStack(ModItems.BOTTLED_MOTH);

            int variantId = this.getVariant().getId();
            String name = this.hasCustomName() ? Objects.requireNonNull(this.getCustomName()).getString() : "";

            bottledMoth.set(ModComponents.MOTH, new MothData(name, variantId));
            bottledMoth.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(variantId));

            this.discard();

            if (!player.getInventory().insertStack(bottledMoth)) {
                player.dropStack(bottledMoth);
            }

            this.getWorld().playSound(
                    null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.PLAYERS, 1.0F, 1.0F
            );

            heldItem.decrement(1);
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }


    @Override
    protected void initDataTracker(net.minecraft.entity.data.DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(DATA_ID_TYPE_VARIANT, 0);
    }

    public void setVariant(SilkMothVariant variant) {
        this.getDataTracker().set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    public SilkMothVariant getVariant() {
        return SilkMothVariant.byId(
                this.getDataTracker().get(DATA_ID_TYPE_VARIANT) & 255
        );
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        this.setVariant(Util.getRandom(SilkMothVariant.values(), this.random));
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    public void writeCustomDataToNbt(net.minecraft.nbt.NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("SilkMothVariant", this.getVariant().getId());
        if (this.hasCustomName()) {
            nbt.putString("MothName", Objects.requireNonNull(this.getCustomName()).getString());
        }
    }

    @Override
    public void readCustomDataFromNbt(net.minecraft.nbt.NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("SilkMothVariant")) {
            this.setVariant(SilkMothVariant.byId(nbt.getInt("SilkMothVariant")));
        }
        if (nbt.contains("MothName")) {
            this.setCustomName(net.minecraft.text.Text.literal(nbt.getString("MothName")));
        }
    }
}
