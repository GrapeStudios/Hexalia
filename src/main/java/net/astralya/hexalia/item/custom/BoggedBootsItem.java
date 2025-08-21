package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.item.client.BoggedBootsRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BoggedBootsItem extends ArmorItem implements GeoItem {

    private int dolphinsGraceTimer = 0;
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public BoggedBootsItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private BoggedBootsRenderer renderer;

            @Override
            public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                                        EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
                if (this.renderer == null)
                    this.renderer = new BoggedBootsRenderer();

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<BoggedBootsItem> animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() && entity instanceof PlayerEntity player) {
            if (player.getInventory().getArmorStack(0).isOf(this)) {
                if (player.isSwimming()) {
                    applyDolphinGraceEffect(player);
                }
                removePoisonEffect(player);
                resetTimers();
            } else {
                handleEffectRemoval(player);
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private void removePoisonEffect(PlayerEntity player) {
        if (player.hasStatusEffect(StatusEffects.POISON)) {
            player.removeStatusEffect(StatusEffects.POISON);
            player.heal(0.1f);
        }
    }

    private void applyDolphinGraceEffect(PlayerEntity player) {
        int duration = 300;

        StatusEffectInstance waterBreathingEffect = player.getStatusEffect(StatusEffects.WATER_BREATHING);
        if (!player.hasStatusEffect(StatusEffects.DOLPHINS_GRACE) ||
                (waterBreathingEffect != null && waterBreathingEffect.getDuration() <= 200)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, duration, 0, false, false, false));
        }
    }

    private void handleEffectRemoval(PlayerEntity player) {
        if (dolphinsGraceTimer < 500) {
            dolphinsGraceTimer++;
        } else {
            player.removeStatusEffect(StatusEffects.DOLPHINS_GRACE);
        }
    }

    private void resetTimers() {
        dolphinsGraceTimer = 0;
    }
}
