package net.astralya.hexalia.item.custom.armor;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.client.renderer.item.BogshadeBootsRenderer;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.function.Consumer;

public class BogshadeBootsItem extends ArmorItem implements GeoItem {
    private static final double NO_SLOW_BOOST = 0.25D;

    private static final ResourceLocation SWIM_SPEED_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "bogshade_swim_speed");

    private static final ResourceLocation SLOW_BLOCK_MOVE_SPEED_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "bogshade_slow_block_move_speed");

    private static final AttributeModifier SWIM_SPEED_MODIFIER =
            new AttributeModifier(SWIM_SPEED_MODIFIER_ID, 0.3D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    private static final AttributeModifier SLOW_BLOCK_MOVE_SPEED_MODIFIER =
            new AttributeModifier(SLOW_BLOCK_MOVE_SPEED_MODIFIER_ID, NO_SLOW_BOOST, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public BogshadeBootsItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
                    T livingEntity,
                    ItemStack stack,
                    EquipmentSlot slot,
                    HumanoidModel<T> original
            ) {
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
            state.getController().setAnimation(
                    RawAnimation.begin().then("idle", software.bernie.geckolib.animation.Animation.LoopType.LOOP)
            );
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        if (!(entity instanceof Player player) || level.isClientSide) {
            super.inventoryTick(stack, level, entity, slot, selected);
            return;
        }

        boolean wearing = player.getItemBySlot(EquipmentSlot.FEET).is(this);
        if (!wearing) {
            removeAll(player);
            super.inventoryTick(stack, level, entity, slot, selected);
            return;
        }

        boolean inWater = player.isInWaterOrBubble();
        if (inWater) {
            applySwimSpeed(player);
        } else {
            removeSwimSpeed(player);
        }

        boolean onNoSlow = !inWater && player.onGround() && isOnNoSlowBlock(player);
        if (onNoSlow) {
            applySlowBlockMoveSpeed(player);
        } else {
            removeSlowBlockMoveSpeed(player);
        }

        super.inventoryTick(stack, level, entity, slot, selected);
    }

    private static void applySwimSpeed(Player player) {
        var attribute = player.getAttribute(NeoForgeMod.SWIM_SPEED);
        if (attribute == null) return;

        if (!attribute.hasModifier(SWIM_SPEED_MODIFIER_ID)) {
            attribute.addTransientModifier(SWIM_SPEED_MODIFIER);
        }
    }

    private static void removeSwimSpeed(Player player) {
        var attribute = player.getAttribute(NeoForgeMod.SWIM_SPEED);
        if (attribute == null) return;

        if (attribute.hasModifier(SWIM_SPEED_MODIFIER_ID)) {
            attribute.removeModifier(SWIM_SPEED_MODIFIER_ID);
        }
    }

    private static void applySlowBlockMoveSpeed(Player player) {
        var movement = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
        if (movement == null) return;

        if (!movement.hasModifier(SLOW_BLOCK_MOVE_SPEED_MODIFIER_ID)) {
            movement.addTransientModifier(SLOW_BLOCK_MOVE_SPEED_MODIFIER);
        }
    }

    private static void removeSlowBlockMoveSpeed(Player player) {
        var movement = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
        if (movement == null) return;

        if (movement.hasModifier(SLOW_BLOCK_MOVE_SPEED_MODIFIER_ID)) {
            movement.removeModifier(SLOW_BLOCK_MOVE_SPEED_MODIFIER_ID);
        }
    }

    private static boolean isOnNoSlowBlock(Player player) {
        BlockPos below = player.blockPosition().below();
        var state = player.level().getBlockState(below);
        return state.is(ModTags.Blocks.BOGSHADE_NO_SLOW);
    }

    private static void removeAll(Player player) {
        removeSwimSpeed(player);
        removeSlowBlockMoveSpeed(player);
    }
}