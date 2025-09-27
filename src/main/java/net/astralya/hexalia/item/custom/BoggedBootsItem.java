package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.item.custom.client.BoggedBootsRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
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
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class BoggedBootsItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final Identifier SWIM_SPEED_MODIFIER_ID = Identifier.of(HexaliaMod.MODID, "bogged_boots_swim_boost");

    public BoggedBootsItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity>
            BipedEntityModel<?> getGeoArmorRenderer(
                    T livingEntity,
                    ItemStack stack,
                    EquipmentSlot slot,
                    BipedEntityModel<T> original
            ) {
                if (renderer == null) {
                    renderer = new BoggedBootsRenderer();
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
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            boolean wearing = player.getEquippedStack(EquipmentSlot.FEET).isOf(this);

            boolean inWater = player.isSwimming();

            boolean shouldBoost = wearing && inWater;
            handleSwimSpeed(player, shouldBoost);
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private void handleSwimSpeed(PlayerEntity player, boolean add) {
        var inst = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (inst == null) return;

        var modifier = new EntityAttributeModifier(
                SWIM_SPEED_MODIFIER_ID,
                0.30,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );

        if (add) {
            if (!inst.hasModifier(SWIM_SPEED_MODIFIER_ID)) {
                inst.addPersistentModifier(modifier);
            }
        } else {
            if (inst.hasModifier(SWIM_SPEED_MODIFIER_ID)) {
                inst.removeModifier(SWIM_SPEED_MODIFIER_ID);
            }
        }
    }
}