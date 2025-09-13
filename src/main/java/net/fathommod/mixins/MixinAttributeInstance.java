package net.fathommod.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fathommod.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Predicate;

@Mixin(AttributeInstance.class)
@SuppressWarnings("unused")
public abstract class MixinAttributeInstance { // Adds acceleration and a rule that only allows range trinkets to give 25% of their reach to assasin weapons

    @ModifyReturnValue(method = "getValue", at = @At("RETURN"))
    @SuppressWarnings("deprecation")
    public double getValue(double original) {
        Minecraft mc = Minecraft.getInstance();
        List<KeyMapping> list = List.of(mc.options.keyUp, mc.options.keyDown, mc.options.keyLeft, mc.options.keyRight);
        AttributeInstance instance = (AttributeInstance) (Object) this;
        if (instance.getAttribute().is(Attributes.ENTITY_INTERACTION_RANGE) && mc.player != null && mc.player.getMainHandItem().getItem() instanceof DamageTypedWeapon weapon && weapon.getDamageClass().equals(DamageClasses.ASSASSIN)) {
            double newVal = original;
            for (AttributeModifier modifier : instance.getModifiers()) {
                if (modifier.id().getNamespace().equals(FathommodMod.MOD_ID) && modifier.id().getPath().contains("trinket"))
                    newVal -= modifier.amount() * .75; // only keep 25%
            }
            return newVal;
        }
        if (!fathomMod$isAttributeInstanceLocalPlayerAndMovementSpeed(instance))
            return original;
        double multiplier = 1;
        KeyMapping key = fathomMod$getFirstMatch(list, KeyMapping::isDown);
        if (key == mc.options.keyDown)
            multiplier = .5;
        else if (key == mc.options.keyLeft || key == mc.options.keyRight)
            multiplier = .75;
        return original * (original >= .20 ? DevUtils.inverseLerp(-33, 67, ClientVars.movementHeldTimeTicks) : 1) * multiplier;
    }

    @Unique
    private boolean fathomMod$isAttributeInstanceLocalPlayerAndMovementSpeed(AttributeInstance instance) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
            return false;

        return mc.player.getAttribute(Attributes.MOVEMENT_SPEED) == instance;
    }

    @Unique
    private static <T> T fathomMod$getFirstMatch(List<T> list, Predicate<T> condition) {
        for (T obj : list) {
            if (condition.test(obj))
                return obj;
        }
        return null;
    }
}
