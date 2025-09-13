package net.fathommod.init;

import net.fathommod.FathommodMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FathommodModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(Registries.PARTICLE_TYPE, FathommodMod.MOD_ID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DUB_GRENADE = REGISTRY.register("dub_grenade", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PALTN_PARTICLE = REGISTRY.register("paltn_particle", () -> new SimpleParticleType(false));
}