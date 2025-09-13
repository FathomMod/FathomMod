package net.fathommod.init;

import net.fathommod.FathommodMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public class FathommodModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, FathommodMod.MOD_ID);
	public static final DeferredHolder<SoundEvent, SoundEvent> PEPSIMAN = REGISTRY.register("pepsiman", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "pepsiman")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SMACK = REGISTRY.register("smack", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "smack")));
	public static final DeferredHolder<SoundEvent, SoundEvent> BONK = REGISTRY.register("bonk", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "bonk")));
	public static final DeferredHolder<SoundEvent, SoundEvent> TED_BOSS_MUSIC = REGISTRY.register("ted_boss_music", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_boss_music")));
}
