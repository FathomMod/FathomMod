package net.fathommod.mixins;

import net.minecraft.world.effect.PoisonMobEffect;
import net.minecraft.world.effect.WitherMobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PoisonMobEffect.class, WitherMobEffect.class})
public class MixinPoison {
    @Inject(method = "applyEffectTick", at = @At("HEAD"))
    private void removeIFrames(LivingEntity p_296276_, int p_296233_, CallbackInfoReturnable<Boolean> cir) {
        p_296276_.invulnerableTime = 0;
    }
    @Inject(method = "applyEffectTick", at = @At("TAIL"))
    private void removeIFrames2(LivingEntity p_296276_, int p_296233_, CallbackInfoReturnable<Boolean> cir) {
        p_296276_.invulnerableTime = 0;
    }
}