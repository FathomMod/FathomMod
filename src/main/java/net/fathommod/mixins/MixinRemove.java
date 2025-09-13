package net.fathommod.mixins;

import net.fathommod.entity.ted.TedEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinRemove {

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    private void onRemove(Entity.RemovalReason reason, CallbackInfo ci) {
        if (((LivingEntity) (Object) this) instanceof TedEntity && reason != Entity.RemovalReason.DISCARDED) {
            ci.cancel();
            ((LivingEntity) (Object) this).discard();
        }
    }
}
