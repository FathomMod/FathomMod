package net.fathommod.mixins;

import net.fathommod.FathommodMod;
import net.fathommod.init.FathommodModMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEffectInstance.class)
public class MixinMobEffectInstance {
    @Shadow
    @Final
    private Holder<MobEffect> effect;

    @Shadow
    private boolean ambient;

    @Shadow
    private boolean visible;

    @Shadow
    private boolean showIcon;

    @Inject(method = "<init>(Lnet/minecraft/core/Holder;IIZZZ)V", at = @At("TAIL"))
    @SuppressWarnings("DataFlowIssue")
    private void init(CallbackInfo ci) {
        if (effect.getKey().location().getNamespace().equals(FathommodMod.MOD_ID)) {
            visible = false;
            ambient = false;
            if (effect == FathommodModMobEffects.INTERNAL_FALL_DAMAGE_IMMUNITY.getDelegate())
                showIcon = false;
        }
    }
}