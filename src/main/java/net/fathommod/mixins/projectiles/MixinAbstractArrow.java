package net.fathommod.mixins.projectiles;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractArrow.class)
public class MixinAbstractArrow { // Decreases AbstractArrow's resistance
    @ModifyReturnValue(method = "getWaterInertia", at = @At("RETURN"))
    private float getWaterInertia(float original) {
        return 0.825f;
    }
}