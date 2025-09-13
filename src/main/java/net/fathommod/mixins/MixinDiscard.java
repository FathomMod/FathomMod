package net.fathommod.mixins;

import net.fathommod.entity.ted.TedEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinDiscard { // Makes ted delete his boss bar upon being despawned

    @Inject(method = "discard", at = @At("HEAD"))
    public void onDiscard(CallbackInfo ci) {
        Entity instance = (Entity) (Object) this;

        if (instance instanceof TedEntity) {
            ((TedEntity) instance).bossBar.removeAllPlayers();
        }
    }
}
