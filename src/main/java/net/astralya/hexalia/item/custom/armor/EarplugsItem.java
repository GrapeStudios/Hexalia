package net.astralya.hexalia.item.custom.armor;

import net.astralya.hexalia.client.renderer.item.EarplugsRenderer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
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

public class EarplugsItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public EarplugsItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
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
                    renderer = new EarplugsRenderer();
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
        if (!(entity instanceof PlayerEntity player)) {
            super.inventoryTick(stack, world, entity, slot, selected);
            return;
        }
        boolean wearingThis = player.getEquippedStack(EquipmentSlot.CHEST).equals(stack);
        if (!world.isClient && wearingThis) {
            if (player.isSneaking()) {
                StatusEffectInstance cur = player.getStatusEffect(StatusEffects.INVISIBILITY);
                if (cur == null || cur.getDuration() <= 10) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 20, 0, false, false, false));
                }
                NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);
                NbtCompound tag = data != null ? data.copyNbt() : new NbtCompound();
                tag.putBoolean("HexaliaInvisFromItem", true);
                stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
                if (stack.isDamageable() && world.getTime() % 20L == 0L && !player.getAbilities().creativeMode) {
                    stack.damage(1, player, EquipmentSlot.CHEST);
                    if (stack.isEmpty()) {
                        player.equipStack(EquipmentSlot.CHEST, ItemStack.EMPTY);
                    }
                }
            } else {
                boolean fromItem = false;
                NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);
                if (data != null) {
                    NbtCompound tag = data.copyNbt();
                    fromItem = tag.getBoolean("HexaliaInvisFromItem");
                    tag.remove("HexaliaInvisFromItem");
                    if (tag.isEmpty()) {
                        stack.remove(DataComponentTypes.CUSTOM_DATA);
                    } else {
                        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
                    }
                }
                if (fromItem && player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                    player.removeStatusEffect(StatusEffects.INVISIBILITY);
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}