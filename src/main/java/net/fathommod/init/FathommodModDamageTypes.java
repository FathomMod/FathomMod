package net.fathommod.init;

import net.fathommod.FathommodMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class FathommodModDamageTypes {
    public static final ResourceKey<DamageType> TED_INSTA_KILL = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_insta_kill"));
    public static final ResourceKey<DamageType> TED_ROCK = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_rock"));
    public static final ResourceKey<DamageType> TED_SWIPE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_swipe"));
    public static final ResourceKey<DamageType> SKILL_ISSUE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "skill_issue"));
    public static final ResourceKey<DamageType> TED_WEAPON_COMBO = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_weapon_combo"));
    public static final ResourceKey<DamageType> FAKE_EXPLOSION = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "fake_explosion"));
    public static final ResourceKey<DamageType> FATAL_POISON_PALTN_1 = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "fatal_poison_one"));
    public static final ResourceKey<DamageType> FATAL_POISON_PALTN_2 = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "fatal_poison_two"));
    public static final ResourceKey<DamageType> BLEED_EFFECT = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "bleed_effect"));
    public static final ResourceKey<DamageType> BUTCHERS_CLEAVE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "butchers_cleave"));
}
