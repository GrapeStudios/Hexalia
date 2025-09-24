package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.item.client.BoggedBootsRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.UUID;
import java.util.function.Consumer;

public class BoggedBootsItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final UUID SWIM_SPEED_MODIFIER_UUID = UUID.fromString("a0a7d558-2a6e-4d27-8a18-0c4f2d0a2f6f");

    public BoggedBootsItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
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
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        if (entity instanceof Player player) {
            boolean wearing = player.getInventory().getArmor(0).is(this);
            handleSwimSpeed(player, wearing);
        }
        super.inventoryTick(stack, level, entity, slot, selected);
    }

    private void handleSwimSpeed(Player player, boolean add) {
        AttributeInstance inst = player.getAttribute(ForgeMod.SWIM_SPEED.get());
        if (inst == null) return;

        AttributeModifier mod = new AttributeModifier(
                SWIM_SPEED_MODIFIER_UUID, "bogged_boots_swim_boost", 0.3, AttributeModifier.Operation.MULTIPLY_TOTAL
        );

        if (add) {
            if (inst.getModifier(SWIM_SPEED_MODIFIER_UUID) == null) {
                inst.addPermanentModifier(mod);
            }
        } else {
            if (inst.getModifier(SWIM_SPEED_MODIFIER_UUID) != null) {
                inst.removeModifier(SWIM_SPEED_MODIFIER_UUID);
            }
        }
    }
}
