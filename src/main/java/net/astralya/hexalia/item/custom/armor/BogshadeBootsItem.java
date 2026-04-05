package net.astralya.hexalia.item.custom.armor;

import net.astralya.hexalia.client.renderer.item.BogshadeBootsRenderer;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BogshadeBootsItem extends ArmorItem implements GeoItem {

    private static final double NO_SLOW_BOOST = 0.25D;
    private static final double WATER_SPEED_MULTIPLIER = 1.15D;
    private static final UUID SLOW_BLOCK_MOVE_SPEED_MODIFIER_UUID = UUID.fromString("b3341177-5f7d-4ea3-99db-8c7bce180e54");
    private static final EntityAttributeModifier SLOW_BLOCK_MOVE_SPEED_MODIFIER =
            new EntityAttributeModifier(SLOW_BLOCK_MOVE_SPEED_MODIFIER_UUID, "bogshade_slow_block_move_speed", NO_SLOW_BOOST, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public BogshadeBootsItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack stack, EquipmentSlot slot, BipedEntityModel<LivingEntity> original) {
                if (this.renderer == null) {
                    this.renderer = new BogshadeBootsRenderer();
                }
                this.renderer.prepForRender(livingEntity, stack, slot, original);
                return (BipedEntityModel<LivingEntity>) this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, 0, state -> {
            state.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof PlayerEntity player) || world.isClient) {
            super.inventoryTick(stack, world, entity, slot, selected);
            return;
        }

        if (!player.getEquippedStack(EquipmentSlot.FEET).isOf(this)) {
            removeAll(player);
            super.inventoryTick(stack, world, entity, slot, selected);
            return;
        }

        boolean inWater = player.isTouchingWater();
        if (inWater) {
            applySwimSpeed(player);
        }

        boolean onNoSlow = !inWater && player.isOnGround() && isOnNoSlowBlock(player);
        if (onNoSlow) {
            applySlowBlockMoveSpeed(player);
        } else {
            removeSlowBlockMoveSpeed(player);
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private static void applySwimSpeed(PlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        double horizontalScale = player.isSprinting() ? WATER_SPEED_MULTIPLIER : 1.08D;
        player.setVelocity(velocity.x * horizontalScale, velocity.y, velocity.z * horizontalScale);
        player.velocityModified = true;
    }

    private static void applySlowBlockMoveSpeed(PlayerEntity player) {
        EntityAttributeInstance movement = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (movement == null) {
            return;
        }

        if (movement.getModifier(SLOW_BLOCK_MOVE_SPEED_MODIFIER_UUID) == null) {
            movement.addTemporaryModifier(SLOW_BLOCK_MOVE_SPEED_MODIFIER);
        }
    }

    private static void removeSlowBlockMoveSpeed(PlayerEntity player) {
        EntityAttributeInstance movement = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (movement == null) {
            return;
        }

        if (movement.getModifier(SLOW_BLOCK_MOVE_SPEED_MODIFIER_UUID) != null) {
            movement.removeModifier(SLOW_BLOCK_MOVE_SPEED_MODIFIER_UUID);
        }
    }

    private static boolean isOnNoSlowBlock(PlayerEntity player) {
        BlockPos below = player.getBlockPos().down();
        return player.getWorld().getBlockState(below).isIn(ModTags.Blocks.BOGSHADE_NO_SLOW);
    }

    private static void removeAll(PlayerEntity player) {
        removeSlowBlockMoveSpeed(player);
    }
}