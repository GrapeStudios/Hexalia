package net.astralya.hexalia.item.custom.armor;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.client.renderer.item.BogshadeBootsRenderer;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.function.Consumer;

public class BogshadeBootsItem extends ArmorItem implements GeoItem {

    private static final double NO_SLOW_BOOST = 0.25D;
    private static final Identifier SWIM_SPEED_MODIFIER_ID =
            Identifier.of(HexaliaMod.MODID, "bogshade_swim_speed");
    private static final Identifier SLOW_BLOCK_MOVE_SPEED_MODIFIER_ID =
            Identifier.of(HexaliaMod.MODID, "bogshade_slow_block_move_speed");
    private static final EntityAttributeModifier SWIM_SPEED_MODIFIER =
            new EntityAttributeModifier(SWIM_SPEED_MODIFIER_ID, 0.3D, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    private static final EntityAttributeModifier SLOW_BLOCK_MOVE_SPEED_MODIFIER =
            new EntityAttributeModifier(SLOW_BLOCK_MOVE_SPEED_MODIFIER_ID, NO_SLOW_BOOST, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public BogshadeBootsItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity> net.minecraft.client.render.entity.model.BipedEntityModel<?> getGeoArmorRenderer(
                    T livingEntity, ItemStack stack, EquipmentSlot slot,
                    net.minecraft.client.render.entity.model.BipedEntityModel<T> original) {
                if (renderer == null) {
                    renderer = new BogshadeBootsRenderer();
                }
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            state.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof PlayerEntity player) || world.isClient) {
            super.inventoryTick(stack, world, entity, slot, selected);
            return;
        }
        boolean wearing = player.getEquippedStack(EquipmentSlot.FEET).isOf(this);
        if (!wearing) {
            removeAll(player);
            super.inventoryTick(stack, world, entity, slot, selected);
            return;
        }
        boolean inWater = player.isTouchingWaterOrRain();
        if (inWater) {
            applySwimSpeed(player);
        } else {
            removeSwimSpeed(player);
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
        var attribute = player.getAttributeInstance(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY);
        if (attribute == null) return;
        if (attribute.getModifier(SWIM_SPEED_MODIFIER_ID) == null) {
            attribute.addTemporaryModifier(SWIM_SPEED_MODIFIER);
        }
    }

    private static void removeSwimSpeed(PlayerEntity player) {
        var attribute = player.getAttributeInstance(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY);
        if (attribute == null) return;
        if (attribute.getModifier(SWIM_SPEED_MODIFIER_ID) != null) {
            attribute.removeModifier(SWIM_SPEED_MODIFIER_ID);
        }
    }

    private static void applySlowBlockMoveSpeed(PlayerEntity player) {
        var movement = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (movement == null) return;
        if (movement.getModifier(SLOW_BLOCK_MOVE_SPEED_MODIFIER_ID) == null) {
            movement.addTemporaryModifier(SLOW_BLOCK_MOVE_SPEED_MODIFIER);
        }
    }

    private static void removeSlowBlockMoveSpeed(PlayerEntity player) {
        var movement = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (movement == null) return;
        if (movement.getModifier(SLOW_BLOCK_MOVE_SPEED_MODIFIER_ID) != null) {
            movement.removeModifier(SLOW_BLOCK_MOVE_SPEED_MODIFIER_ID);
        }
    }

    private static boolean isOnNoSlowBlock(PlayerEntity player) {
        BlockPos below = player.getBlockPos().down();
        return player.getWorld().getBlockState(below).isIn(ModTags.Blocks.BOGSHADE_NO_SLOW);
    }

    private static void removeAll(PlayerEntity player) {
        removeSwimSpeed(player);
        removeSlowBlockMoveSpeed(player);
    }
}