package net.grapes.hexalia.entity.custom;

import net.grapes.hexalia.entity.ModEntities;
import net.grapes.hexalia.entity.ai.silkmoth.AttractedToLightGoal;
import net.grapes.hexalia.entity.ai.silkmoth.AvoidSunlightGoal;
import net.grapes.hexalia.entity.ai.silkmoth.FlyRandomlyGoal;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class SilkMothEntity extends AnimalEntity implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public SilkMothEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.experiencePoints = 1;
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
        this.goalSelector.add(1, new AttractedToLightGoal(this, 1.5d));
        this.goalSelector.add(2, new AvoidSunlightGoal(this));
        this.goalSelector.add(3, new FlyRandomlyGoal(this));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.75f, 1));
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
        if ((!this.isOnGround() || silkMothAnimationState.isMoving()) &&
                !(this.getNavigation().isIdle() && (this.isAttractedToLight() || this.isAvoidingSunlight()))) {
            silkMothAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.silkmoth.flying", Animation.LoopType.LOOP));
        } else if (this.getNavigation().isIdle() && (this.isAttractedToLight() || this.isAvoidingSunlight())) {
            silkMothAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.silkmoth.idle", Animation.LoopType.LOOP));
        }

        return PlayState.CONTINUE;
    }

    private boolean isAttractedToLight() {
        return this.goalSelector.getRunningGoals().anyMatch(goal -> goal.getGoal() instanceof AttractedToLightGoal);
    }

    private boolean isAvoidingSunlight() {
        return this.goalSelector.getRunningGoals().anyMatch(goal -> goal.getGoal() instanceof AvoidSunlightGoal);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemInHand = player.getStackInHand(hand);

        // Verificar si el jugador tiene una botella rústica en la mano
        if (itemInHand.isOf(ModItems.RUSTIC_BOTTLE)) {
            // Atrapar la polilla
            if (!this.getWorld().isClient) {
                this.remove(RemovalReason.KILLED);

                // Añadir una botella con la polilla dentro al inventario del jugador
                ItemStack mothBottle = new ItemStack(ModItems.MOTH_IN_BOTTLE);
                if (!player.getInventory().insertStack(mothBottle)) {
                    this.dropStack(mothBottle); // Dejar caer el item si el inventario está lleno
                }

                // Reproduce un sonido (opcional)
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS, 1.0F, 1.0F);

                // Consumir la botella rústica vacía
                if (!player.isCreative()) {
                    itemInHand.decrement(1);
                }

                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    }
}

