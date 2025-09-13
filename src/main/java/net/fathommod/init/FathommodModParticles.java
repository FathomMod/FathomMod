package net.fathommod.init;

import net.fathommod.particle.DubGrenadeParticle;
import net.fathommod.particle.PaltnParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FathommodModParticles {
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(FathommodModParticleTypes.DUB_GRENADE.get(), spriteSet -> new ParticleProvider<>() {
            @Override
            public @NotNull Particle createParticle(@NotNull SimpleParticleType p_107421_, @NotNull ClientLevel world, double x, double y, double z, double deltaX, double deltaY, double deltaZ) {
                return new DubGrenadeParticle(world, x, y, z, deltaX, deltaY, deltaZ, spriteSet);
            }
        });

        event.registerSpriteSet(FathommodModParticleTypes.PALTN_PARTICLE.get(), spriteSet -> new ParticleProvider<>() {
            @Override
            public @NotNull Particle createParticle(@NotNull SimpleParticleType p_107421_, @NotNull ClientLevel world, double x, double y, double z, double deltaX, double deltaY, double deltaZ) {
                return new PaltnParticle(world, x, y, z, deltaX, deltaY, deltaZ, spriteSet);
            }
        });
    }
}