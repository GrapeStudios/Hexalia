package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.item.client.BoggedBootsRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.function.Consumer;

public class BoggedBootsItem extends ArmorItem implements GeoItem {

    private int dolphinsGraceTimer = 0;

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public BoggedBootsItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BoggedBootsRenderer renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                                   EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null)
                    this.renderer = new BoggedBootsRenderer();

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide() && pEntity instanceof Player pPlayer) {
            if (pPlayer.getInventory().getArmor(0).is(this)) {
                if (pPlayer.isSwimming()) {
                    applyDolphinGraceEffect(pPlayer);
                }
                removePoisonEffect(pPlayer);
                resetTimers();
            } else {
                handleEffectRemoval(pPlayer);
            }
        }
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    private void removePoisonEffect(Player pPlayer) {
        if (pPlayer.hasEffect(MobEffects.POISON)) {
            pPlayer.removeEffect(MobEffects.POISON);
            pPlayer.heal(0.1f);
        }
    }

    private void applyDolphinGraceEffect(Player pPlayer) {
        int duration = 300;

        MobEffectInstance waterBreathingEffect = pPlayer.getEffect(MobEffects.WATER_BREATHING);
        if (!pPlayer.hasEffect(MobEffects.DOLPHINS_GRACE) ||
                (waterBreathingEffect != null && waterBreathingEffect.getDuration() <= 200)) {
            pPlayer.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, duration, 0, false, false, false));
        }
    }

    private void handleEffectRemoval(Player player) {
        if (dolphinsGraceTimer < 500) {
            dolphinsGraceTimer++;
        } else {
            player.removeEffect(MobEffects.DOLPHINS_GRACE);
        }
    }

    private void resetTimers() {
        dolphinsGraceTimer = 0;
    }
}
