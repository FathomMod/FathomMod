package net.fathommod.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fathommod.network.FathommodModVariables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinPush { // Prevents summons from being pushed
    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    public void onPush(Entity entity, CallbackInfo ci) {
        Entity instance = (Entity) (Object) this;
        if (instance.getData(FathommodModVariables.ENTITY_VARIABLES).isSummon)
            ci.cancel();
    }

    @ModifyReturnValue(method = "isPushable", at = @At("RETURN"))
    public boolean isPushable(boolean original) {
        return !((Entity) (Object) this).getData(FathommodModVariables.ENTITY_VARIABLES).isSummon && original;
    }

    @Inject(method = "doPush", at = @At("HEAD"), cancellable = true)
    public void onDoPush(Entity entity, CallbackInfo ci) {
        Entity instance = (Entity) (Object) this;
        if (instance.getData(FathommodModVariables.ENTITY_VARIABLES).isSummon)
            ci.cancel();
    }
}
