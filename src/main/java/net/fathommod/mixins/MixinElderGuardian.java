package net.fathommod.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.monster.ElderGuardian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ElderGuardian.class)
public class MixinElderGuardian {
    @ModifyReturnValue(method = "getAttackDuration", at = @At("RETURN"))
    private int getAttackDuration(int original) {
        return 20;
    }
}