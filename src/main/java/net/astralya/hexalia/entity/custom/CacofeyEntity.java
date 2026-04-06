package net.astralya.hexalia.entity.custom;

import net.astralya.hexalia.gameplay.cacofey.ai.CacofeyAnchorHoverGoal;
import net.astralya.hexalia.gameplay.cacofey.ai.CacofeyHarvestGoal;
import net.astralya.hexalia.gameplay.cacofey.ai.CacofeyStealGoal;
import net.astralya.hexalia.gameplay.moths.ai.DriftFlyGoal;
import net.astralya.hexalia.gameplay.moths.ai.UnstuckNudgeGoal;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.HexFocusItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.SitGoal;
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
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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

public class CacofeyEntity extends TameableEntity implements GeoEntity {

    private static final TrackedData<ItemStack> HELD_ITEM =
            DataTracker.registerData(CacofeyEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Boolean> INSPECTING =
            DataTracker.registerData(CacofeyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Byte> CACOFEY_MODE =
            DataTracker.registerData(CacofeyEntity.class, TrackedDataHandlerRegistry.BYTE);

    private static final String TAG_STEAL_COOLDOWN = "StealCooldown";
    private static final String TAG_HELD_ITEM      = "HeldItem";
    private static final String TAG_MODE           = "CacofeyMode";
    private static final String TAG_ANCHOR_X       = "AnchorX";
    private static final String TAG_ANCHOR_Y       = "AnchorY";
    private static final String TAG_ANCHOR_Z       = "AnchorZ";

    public int stealCooldown = 0;
    private @Nullable BlockPos anchorPos = null;

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public CacofeyEntity(EntityType<? extends TameableEntity> type, World world) {
        super(type, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16.0F);
        this.setPathfindingPenalty(PathNodeType.FENCE, -1.0F);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.7F)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SitGoal(this));
        this.goalSelector.add(2, new TemptGoal(this, 1.1D, Ingredient.ofItems(ModItems.GALEBERRIES_COOKIE), false));
        this.goalSelector.add(3, new CacofeyStealGoal(this));
        this.goalSelector.add(4, new CacofeyHarvestGoal(this));
        this.goalSelector.add(5, new CacofeyAnchorHoverGoal(this));
        this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0D, 5.0F, 2.0F) {
            @Override
            public boolean canStart() {
                return getMode() == CacofeyMode.FOLLOW && super.canStart();
            }

            @Override
            public boolean shouldContinue() {
                return getMode() == CacofeyMode.FOLLOW && super.shouldContinue();
            }
        });
        this.goalSelector.add(7, new DriftFlyGoal(this, 0.6D) {
            @Override
            public boolean canStart() {
                return getMode() != CacofeyMode.STAY && super.canStart();
            }

            @Override
            public boolean shouldContinue() {
                return getMode() != CacofeyMode.STAY && super.shouldContinue();
            }

            @Override
            public void start() {
                if (getMode() != CacofeyMode.STAY) {
                    super.start();
                }
            }

            @Override
            public void stop() {
                super.stop();
                getNavigation().stop();
            }
        });
        this.goalSelector.add(8, new UnstuckNudgeGoal(this) {
            @Override
            public boolean canStart() {
                return getMode() != CacofeyMode.STAY && super.canStart();
            }

            @Override
            public boolean shouldContinue() {
                return getMode() != CacofeyMode.STAY && super.shouldContinue();
            }
        });
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
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HELD_ITEM, ItemStack.EMPTY);
        builder.add(INSPECTING, false);
        builder.add(CACOFEY_MODE, (byte) CacofeyMode.FOLLOW.ordinal());
    }

    public CacofeyMode getMode() {
        return CacofeyMode.values()[this.dataTracker.get(CACOFEY_MODE)];
    }

    public void setMode(CacofeyMode mode) {
        this.dataTracker.set(CACOFEY_MODE, (byte) mode.ordinal());
        boolean staying = mode == CacofeyMode.STAY;
        this.setSitting(staying);

        if (staying) {
            this.getNavigation().stop();
            this.setVelocity(Vec3d.ZERO);
        }
    }

    public @Nullable BlockPos getAnchorPos() {
        return anchorPos;
    }

    public void setAnchorPos(@Nullable BlockPos pos) {
        this.anchorPos = pos;
    }

    public ItemStack getHeldItem() {
        return this.dataTracker.get(HELD_ITEM);
    }

    public void setHeldItem(ItemStack stack) {
        this.dataTracker.set(HELD_ITEM, stack.copy());
    }

    public boolean isHoldingItem() {
        return !this.getHeldItem().isEmpty();
    }

    public boolean isInspecting() {
        return this.dataTracker.get(INSPECTING);
    }

    public void setInspecting(boolean inspecting) {
        this.dataTracker.set(INSPECTING, inspecting);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isOf(ModItems.GALEBERRIES_COOKIE)) {
            if (!this.isTamed()) {
                if (!this.getWorld().isClient) {
                    if (!player.getAbilities().creativeMode) stack.decrement(1);
                    if (this.random.nextInt(3) == 0) {
                        this.setOwner(player);
                        this.setSitting(false);
                        this.getWorld().sendEntityStatus(this, (byte) 7);
                    } else {
                        this.getWorld().sendEntityStatus(this, (byte) 6);
                    }
                }
                return this.getWorld().isClient ? ActionResult.SUCCESS : ActionResult.CONSUME;
            }
            return ActionResult.PASS;
        }
        if (this.isTamed() && this.isOwner(player)) {
            if (stack.getItem() instanceof HexFocusItem) {
                if (!this.getWorld().isClient) {
                    HexFocusItem.attuneToEntity(stack, this.getUuid());
                    player.sendMessage(
                            Text.translatable("message.hexalia.cacofey.attuned", this.getName()), true);
                }
                return this.getWorld().isClient ? ActionResult.SUCCESS : ActionResult.CONSUME;
            }
            if (!this.getWorld().isClient) {
                CacofeyMode next = getMode().next();
                setMode(next);
                player.sendMessage(switch (next) {
                    case STAY   -> Text.translatable("message.hexalia.cacofey.stay");
                    case FOLLOW -> Text.translatable("message.hexalia.cacofey.follow");
                    case WANDER -> Text.translatable("message.hexalia.cacofey.wander");
                }, true);
            }
            return this.getWorld().isClient ? ActionResult.SUCCESS : ActionResult.CONSUME;
        }
        return super.interactMob(player, hand);
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(ModItems.CACOFEY_SPAWN_EGG);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity other) {
        return null;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (this.getMode() == CacofeyMode.STAY) {
            this.getNavigation().stop();
            this.setVelocity(this.getVelocity().multiply(0.0, 0.6, 0.0));
        } else if (!this.isOnGround() && this.getVelocity().y < 0) {
            this.setVelocity(this.getVelocity().multiply(1, 0.6, 1));
        }

        if (!this.getWorld().isClient && this.stealCooldown > 0) {
            this.stealCooldown--;
        }

        if (this.getWorld().isClient && this.age % 3 == 0) {
            Vec3d motion = this.getVelocity();
            if (motion.horizontalLengthSquared() > 0.001 || Math.abs(motion.y) > 0.001) {
                double trailX = this.getX() - motion.x * 0.5 + (this.random.nextDouble() - 0.5) * 0.15;
                double trailY = this.getY() + 0.3 + (this.random.nextDouble() - 0.5) * 0.1;
                double trailZ = this.getZ() - motion.z * 0.5 + (this.random.nextDouble() - 0.5) * 0.15;
                net.minecraft.client.MinecraftClient.getInstance().world.addParticle(
                        net.astralya.hexalia.particle.ModParticleType.CACOFEY_DUST,
                        trailX, trailY, trailZ,
                        0.0, 0.003, 0.0
                );
            }
        }
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
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos pos) {}

    @Override
    public boolean handleFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putInt(TAG_STEAL_COOLDOWN, this.stealCooldown);
        tag.putByte(TAG_MODE, (byte) getMode().ordinal());
        if (this.isHoldingItem()) {
            tag.put(TAG_HELD_ITEM, this.getHeldItem().encode(this.getRegistryManager()));
        }
        if (anchorPos != null) {
            tag.putInt(TAG_ANCHOR_X, anchorPos.getX());
            tag.putInt(TAG_ANCHOR_Y, anchorPos.getY());
            tag.putInt(TAG_ANCHOR_Z, anchorPos.getZ());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        if (tag.contains(TAG_STEAL_COOLDOWN)) this.stealCooldown = tag.getInt(TAG_STEAL_COOLDOWN);
        if (tag.contains(TAG_MODE)) setMode(CacofeyMode.values()[tag.getByte(TAG_MODE)]);
        if (tag.contains(TAG_HELD_ITEM)) {
            this.setHeldItem(ItemStack.fromNbt(this.getRegistryManager(), tag.getCompound(TAG_HELD_ITEM)).orElse(ItemStack.EMPTY));
        }
        if (tag.contains(TAG_ANCHOR_X)) {
            anchorPos = new BlockPos(tag.getInt(TAG_ANCHOR_X), tag.getInt(TAG_ANCHOR_Y), tag.getInt(TAG_ANCHOR_Z));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        RawAnimation anim;
        if (this.isInspecting()) {
            anim = RawAnimation.begin().then("animation.cacofey.inspecting", Animation.LoopType.LOOP);
        } else if (this.isOnGround()) {
            anim = RawAnimation.begin().then("animation.cacofey.idle", Animation.LoopType.LOOP);
        } else {
            anim = RawAnimation.begin().then("animation.cacofey.flying", Animation.LoopType.LOOP);
        }
        state.getController().setAnimation(anim);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}