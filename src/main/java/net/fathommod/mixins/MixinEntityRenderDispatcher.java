package net.fathommod.mixins;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {
    @Shadow public abstract <T extends Entity> EntityRenderer<? super T> getRenderer(T p_114383_);

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public <E extends Entity> void shouldRender(E p_114398_, Frustum p_114399_, double p_114400_, double p_114401_, double p_114402_, CallbackInfoReturnable<Boolean> cir) {
        if (getRenderer(p_114398_) == null)
            cir.setReturnValue(false);
    }
}
