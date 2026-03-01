package net.astralya.hexalia.item.custom.armor;

import net.astralya.hexalia.client.renderer.item.EarplugsRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.function.Consumer;

public class EarplugsItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public EarplugsItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity>
            HumanoidModel<?> getGeoArmorRenderer(
                    T livingEntity,
                    ItemStack stack,
                    EquipmentSlot slot,
                    HumanoidModel<T> original
            ) {
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
            state.getController().setAnimation(
                    RawAnimation.begin().then("idle", Animation.LoopType.LOOP)
            );
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof Player player)) {
            super.inventoryTick(stack, world, entity, slot, selected);
            return;
        }

        boolean wearingThis = player.getItemBySlot(EquipmentSlot.CHEST).equals(stack);

        if (!world.isClientSide && wearingThis) {
            if (player.isCrouching()) {
                MobEffectInstance cur = player.getEffect(MobEffects.INVISIBILITY);
                if (cur == null || cur.getDuration() <= 10) {
                    player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20, 0, false, false, false));
                }

                CustomData data = stack.get(DataComponents.CUSTOM_DATA);
                CompoundTag tag = data != null ? data.copyTag() : new CompoundTag();
                tag.putBoolean("HexaliaInvisFromItem", true);
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

                if (stack.isDamageableItem() && world.getGameTime() % 20L == 0L && !player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, EquipmentSlot.CHEST);
                    if (stack.isEmpty()) {
                        player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
                    }
                }
            } else {
                boolean fromItem = false;
                CustomData data = stack.get(DataComponents.CUSTOM_DATA);
                if (data != null) {
                    CompoundTag tag = data.copyTag();
                    fromItem = tag.getBoolean("HexaliaInvisFromItem");
                    tag.remove("HexaliaInvisFromItem");
                    if (tag.isEmpty()) {
                        stack.remove(DataComponents.CUSTOM_DATA);
                    } else {
                        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                    }
                }
                if (fromItem && player.hasEffect(MobEffects.INVISIBILITY)) {
                    player.removeEffect(MobEffects.INVISIBILITY);
                }
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
