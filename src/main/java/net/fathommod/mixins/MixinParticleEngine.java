package net.fathommod.mixins;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public abstract class MixinParticleEngine { // Increases particle render distance depending on particle settings

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "addParticleInternal(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"), cancellable = true)
    private void onParticleAdd(ParticleOptions p_109805_, boolean p_109806_, boolean p_109807_, double p_109808_, double p_109809_, double p_109810_, double p_109811_, double p_109812_, double p_109813_, CallbackInfoReturnable<Particle> cir) {
        Camera camera = minecraft.gameRenderer.getMainCamera();
        ParticleStatus particlestatus = Minecraft.getInstance().options.particles().get();
        double renderDistance = switch (particlestatus) {
            case ALL -> Minecraft.getInstance().options.renderDistance().get() * 16; // render distance
            case DECREASED -> 64;
            case MINIMAL -> 32; // default vanilla distance
        };
        double distance = Math.sqrt(camera.getPosition().distanceToSqr(p_109808_, p_109809_, p_109810_));
        cir.setReturnValue(distance < renderDistance ? this.minecraft.particleEngine.createParticle(p_109805_, p_109808_, p_109809_, p_109810_, p_109811_, p_109812_, p_109813_) : null);
    }
}
