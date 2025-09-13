package net.fathommod.mixins;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangedAttribute.class)
public class MixinAttributes {
    @Mutable
    @Shadow @Final private double maxValue;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(String name, double defaultValue, double minValue, double maxValue, CallbackInfo ci) {
        if (name.equals("attribute.name.generic.max_health")) {
            this.maxValue = Double.POSITIVE_INFINITY;
        } else if (name.equals("attribute.name.generic.attack_damage")) {
            ((RangedAttribute) (Object) this).setSyncable(true);
        }
    }
}