package net.fathommod.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fathommod.network.FathommodModVariables;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class MixinGodMode { // Makes god mode players literally invulnerable
//    @Inject(method = "hurt", at = @At("RETURN"), cancellable = true)
//    public void hurt(DamageSource p_9037_, float p_9038_, CallbackInfoReturnable<Boolean> cir) {
//        Player instance = (Player) (Object) this;
//        if (instance.getData(FathommodModVariables.ENTITY_VARIABLES).isGodMode) {
//            cir.setReturnValue(false);
//            cir.cancel();
//        }
//    }
    @ModifyReturnValue(method = "hurt", at = @At("RETURN"))
    public boolean hurt(boolean original, DamageSource p_9037_, float p_9038_) {
        return !((Player) (Object) this).getData(FathommodModVariables.ENTITY_VARIABLES).isGodMode && original;
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void die(CallbackInfo ci) {
        Player instance = (Player) (Object) this;
        if (instance.getData(FathommodModVariables.ENTITY_VARIABLES).isGodMode)
            ci.cancel();
    }
}