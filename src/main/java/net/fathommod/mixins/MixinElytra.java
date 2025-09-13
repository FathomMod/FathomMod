package net.fathommod.mixins;

import net.fathommod.init.FathommodModMobEffects;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class MixinElytra { // Stops the player from elytra flying when being movement stunned

    @Inject(method = "startFallFlying", at = @At("HEAD"), cancellable = true)
    public void onStartFallFlying(CallbackInfo ci) {
        Player instance = ((Player) (Object) this);

        if (instance.hasEffect(FathommodModMobEffects.MOVEMENT_STUN))
            ci.cancel();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        Player instance = ((Player) (Object) this);

        if (instance.hasEffect(FathommodModMobEffects.MOVEMENT_STUN))
            instance.stopFallFlying();
    }
}
