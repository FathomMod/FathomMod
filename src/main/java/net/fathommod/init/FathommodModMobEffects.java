package net.fathommod.init;

import net.fathommod.FathommodMod;
import net.fathommod.effect.BleedEffect;
import net.fathommod.effect.FatalPoisonEffect;
import net.fathommod.effect.MovementStunEffect;
import net.fathommod.effect.NoBuildEffect;
import net.fathommod.network.FathommodModVariables;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber
public class FathommodModMobEffects {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, FathommodMod.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> ZERO_BUILD = REGISTRY.register("no_build", () -> new NoBuildEffect(MobEffectCategory.HARMFUL, 0x000000)
            .addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "no_build_effect"), -238, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> MOVEMENT_STUN = REGISTRY.register("movement_stun", () -> new MovementStunEffect(MobEffectCategory.HARMFUL, 0x000000)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "movement_stun_effect_modifier_walk"), -238, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "movement_stun_effect_modifier_jump"), -238, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(NeoForgeMod.SWIM_SPEED, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "movement_stun_effect_modifier_fly"), -238, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "movement_stun_damage_nerf"), -.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(NeoForgeMod.CREATIVE_FLIGHT, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "movement_stun"), -999999, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> FATAL_POISON = REGISTRY.register("fatal_poison", () -> new FatalPoisonEffect(MobEffectCategory.HARMFUL, 0xFF0000));
    public static final DeferredHolder<MobEffect, MobEffect> WASTED_TRINKET = REGISTRY.register("wasted_trinket", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0) {
    });
    @SuppressWarnings("unused")
    public static final DeferredHolder<MobEffect, MobEffect> BLEED = REGISTRY.register("bleed", BleedEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> INTERNAL_FALL_DAMAGE_IMMUNITY = REGISTRY.register("internal_fall_damage_immunity", () -> (new MobEffect(MobEffectCategory.BENEFICIAL, 0) {}).addAttributeModifier(Attributes.FALL_DAMAGE_MULTIPLIER, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "internal_fall_dmg_immunity"), -100000, AttributeModifier.Operation.ADD_VALUE));
    @SuppressWarnings({"unused", "RedundantCast"})
    public static void expireEffects(MobEffectInstance effect, LivingEntity entity) {
        if (effect.is(MobEffects.POISON)) {
            FathommodMod.queueServerWork(40, () -> {
                FathommodModVariables.EntityVariables vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
                vars.takenPoisonDamage = 0;
            }, (ServerLevel) entity.level());
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance != null) {
            expireEffects(effectInstance, event.getEntity());
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance != null) {
            expireEffects(effectInstance, event.getEntity());
        }
    }
}
