package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.item.custom.client.BoggedBootsRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
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

public class BoggedBootsItem extends ArmorItem implements GeoItem {

    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("a0a7d558-2a6e-4d27-8a18-0c4f2d0a2f6f");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public BoggedBootsItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private GeoArmorRenderer<?> renderer;
            @Override
            public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack stack, EquipmentSlot slot, BipedEntityModel<LivingEntity> original) {
                if (renderer == null) renderer = new BoggedBootsRenderer();
                renderer.prepForRender(livingEntity, stack, slot, original);
                return renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
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
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player) {
            boolean wearing = player.getEquippedStack(EquipmentSlot.FEET).getItem() == this;
            handleSwimSpeed(player, wearing && player.isSubmergedInWater());
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private void handleSwimSpeed(PlayerEntity player, boolean add) {
        EntityAttributeInstance inst = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (inst == null) return;
        EntityAttributeModifier mod = new EntityAttributeModifier(SPEED_MODIFIER_UUID, "bogged_boots_swim_boost", 0.3, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        if (add) {
            if (inst.getModifier(SPEED_MODIFIER_UUID) == null) inst.addPersistentModifier(mod);
        } else {
            if (inst.getModifier(SPEED_MODIFIER_UUID) != null) inst.removeModifier(SPEED_MODIFIER_UUID);
        }
    }
}