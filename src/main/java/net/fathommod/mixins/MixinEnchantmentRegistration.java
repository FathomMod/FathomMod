package net.fathommod.mixins;

import net.fathommod.TempVars;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.*;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Mixin(Enchantments.class)
@SuppressWarnings("deprecation")
public class MixinEnchantmentRegistration {
    @Inject(method = "bootstrap", at = @At("HEAD"), cancellable = true)
    private static void bootstrap(BootstrapContext<Enchantment> p_345935_, CallbackInfo ci) {
        ci.cancel();
        HolderGetter<Enchantment> holdergetter1 = p_345935_.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> holdergetter2 = p_345935_.lookup(Registries.ITEM);
        HolderGetter<Block> holdergetter3 = p_345935_.lookup(Registries.BLOCK);
        TempVars.blockHolderGetter = holdergetter3;
        Enchantments.register(
                p_345935_,
                Enchantments.PROTECTION,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                                        10,
                                        4,
                                        Enchantment.dynamicCost(1, 11),
                                        Enchantment.dynamicCost(12, 11),
                                        1,
                                        EquipmentSlotGroup.ARMOR
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
        );
        Enchantments.register(
                p_345935_,
                Enchantments.FIRE_PROTECTION,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                                        5,
                                        4,
                                        Enchantment.dynamicCost(10, 8),
                                        Enchantment.dynamicCost(18, 8),
                                        2,
                                        EquipmentSlotGroup.ARMOR
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.withDefaultNamespace("enchantment.fire_protection"),
                                        Attributes.BURNING_TIME,
                                        LevelBasedValue.perLevel(-0.15F),
                                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.FEATHER_FALLING,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                                        5,
                                        4,
                                        Enchantment.dynamicCost(5, 6),
                                        Enchantment.dynamicCost(11, 6),
                                        2,
                                        EquipmentSlotGroup.ARMOR
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.DAMAGE_PROTECTION,
                                new AddValue(LevelBasedValue.perLevel(3.0F)),
                                DamageSourceCondition.hasDamageSource(
                                        DamageSourcePredicate.Builder.damageType()
                                                .tag(TagPredicate.is(DamageTypeTags.IS_FALL))
                                                .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.BLAST_PROTECTION,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                                        2,
                                        4,
                                        Enchantment.dynamicCost(5, 8),
                                        Enchantment.dynamicCost(13, 8),
                                        4,
                                        EquipmentSlotGroup.ARMOR
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.withDefaultNamespace("enchantment.blast_protection"),
                                        Attributes.EXPLOSION_KNOCKBACK_RESISTANCE,
                                        LevelBasedValue.perLevel(0.15F),
                                        AttributeModifier.Operation.ADD_VALUE
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.PROJECTILE_PROTECTION,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                                        5,
                                        4,
                                        Enchantment.dynamicCost(3, 6),
                                        Enchantment.dynamicCost(9, 6),
                                        2,
                                        EquipmentSlotGroup.ARMOR
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
        );
        Enchantments.register(
                p_345935_,
                Enchantments.RESPIRATION,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                                        2,
                                        3,
                                        Enchantment.dynamicCost(10, 10),
                                        Enchantment.dynamicCost(40, 10),
                                        4,
                                        EquipmentSlotGroup.HEAD
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.withDefaultNamespace("enchantment.respiration"),
                                        Attributes.OXYGEN_BONUS,
                                        LevelBasedValue.perLevel(1.0F),
                                        AttributeModifier.Operation.ADD_VALUE
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.AQUA_AFFINITY,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                                        2,
                                        1,
                                        Enchantment.constantCost(1),
                                        Enchantment.constantCost(41),
                                        4,
                                        EquipmentSlotGroup.HEAD
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.withDefaultNamespace("enchantment.aqua_affinity"),
                                        Attributes.SUBMERGED_MINING_SPEED,
                                        LevelBasedValue.perLevel(4.0F),
                                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.THORNS,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                                        holdergetter2.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
                                        1,
                                        3,
                                        Enchantment.dynamicCost(10, 20),
                                        Enchantment.dynamicCost(60, 20),
                                        8,
                                        EquipmentSlotGroup.ANY
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.POST_ATTACK,
                                EnchantmentTarget.VICTIM,
                                EnchantmentTarget.ATTACKER,
                                AllOf.entityEffects()
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.DEPTH_STRIDER,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                                        2,
                                        3,
                                        Enchantment.dynamicCost(10, 10),
                                        Enchantment.dynamicCost(25, 10),
                                        4,
                                        EquipmentSlotGroup.FEET
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.BOOTS_EXCLUSIVE))
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.withDefaultNamespace("enchantment.depth_strider"),
                                        Attributes.WATER_MOVEMENT_EFFICIENCY,
                                        LevelBasedValue.perLevel(0.33333334F),
                                        AttributeModifier.Operation.ADD_VALUE
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.FROST_WALKER,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                                        2,
                                        2,
                                        Enchantment.dynamicCost(10, 10),
                                        Enchantment.dynamicCost(25, 10),
                                        4,
                                        EquipmentSlotGroup.FEET
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.BOOTS_EXCLUSIVE))
                        .withEffect(
                                EnchantmentEffectComponents.DAMAGE_IMMUNITY,
                                DamageImmunity.INSTANCE,
                                DamageSourceCondition.hasDamageSource(
                                        DamageSourcePredicate.Builder.damageType()
                                                .tag(TagPredicate.is(DamageTypeTags.BURN_FROM_STEPPING))
                                                .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.LOCATION_CHANGED,
                                new ReplaceDisk(
                                        new LevelBasedValue.Clamped(LevelBasedValue.perLevel(3.0F, 1.0F), 0.0F, 16.0F),
                                        LevelBasedValue.constant(1.0F),
                                        new Vec3i(0, -1, 0),
                                        Optional.of(
                                                BlockPredicate.allOf(
                                                        BlockPredicate.matchesTag(new Vec3i(0, 1, 0), BlockTags.AIR),
                                                        BlockPredicate.matchesBlocks(Blocks.WATER),
                                                        BlockPredicate.matchesFluids(Fluids.WATER),
                                                        BlockPredicate.unobstructed()
                                                )
                                        ),
                                        BlockStateProvider.simple(Blocks.FROSTED_ICE),
                                        Optional.of(GameEvent.BLOCK_PLACE)
                                ),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnGround(true))
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.BINDING_CURSE,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.EQUIPPABLE_ENCHANTABLE),
                                        1,
                                        1,
                                        Enchantment.constantCost(25),
                                        Enchantment.constantCost(50),
                                        8,
                                        EquipmentSlotGroup.ARMOR
                                )
                        )
                        .withEffect(EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)
        );
        EntityPredicate.Builder entitypredicate$builder = EntityPredicate.Builder.entity()
                .periodicTick(5)
                .flags(EntityFlagsPredicate.Builder.flags().setIsFlying(false).setOnGround(true))
                .moving(MovementPredicate.horizontalSpeed(MinMaxBounds.Doubles.atLeast(1.0E-5F)))
                .movementAffectedBy(
                        LocationPredicate.Builder.location()
                                .setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(BlockTags.SOUL_SPEED_BLOCKS))
                );
        Enchantments.register(
                p_345935_,
                Enchantments.SOUL_SPEED,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                                        1,
                                        3,
                                        Enchantment.dynamicCost(10, 10),
                                        Enchantment.dynamicCost(25, 10),
                                        8,
                                        EquipmentSlotGroup.FEET
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.LOCATION_CHANGED,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.withDefaultNamespace("enchantment.soul_speed"),
                                        Attributes.MOVEMENT_SPEED,
                                        LevelBasedValue.perLevel(0.0405F, 0.0105F),
                                        AttributeModifier.Operation.ADD_VALUE
                                ),
                                AllOfCondition.allOf(
                                        InvertedLootItemCondition.invert(
                                                LootItemEntityPropertyCondition.hasProperties(
                                                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().vehicle(EntityPredicate.Builder.entity())
                                                )
                                        ),
                                        AnyOfCondition.anyOf(
                                                AllOfCondition.allOf(
                                                        EnchantmentActiveCheck.enchantmentActiveCheck(),
                                                        LootItemEntityPropertyCondition.hasProperties(
                                                                LootContext.EntityTarget.THIS,
                                                                EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setIsFlying(false))
                                                        ),
                                                        AnyOfCondition.anyOf(
                                                                LootItemEntityPropertyCondition.hasProperties(
                                                                        LootContext.EntityTarget.THIS,
                                                                        EntityPredicate.Builder.entity()
                                                                                .movementAffectedBy(
                                                                                        LocationPredicate.Builder.location()
                                                                                                .setBlock(
                                                                                                        net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(BlockTags.SOUL_SPEED_BLOCKS)
                                                                                                )
                                                                                )
                                                                ),
                                                                LootItemEntityPropertyCondition.hasProperties(
                                                                        LootContext.EntityTarget.THIS,
                                                                        EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnGround(false)).build()
                                                                )
                                                        )
                                                ),
                                                AllOfCondition.allOf(
                                                        EnchantmentActiveCheck.enchantmentInactiveCheck(),
                                                        LootItemEntityPropertyCondition.hasProperties(
                                                                LootContext.EntityTarget.THIS,
                                                                EntityPredicate.Builder.entity()
                                                                        .movementAffectedBy(
                                                                                LocationPredicate.Builder.location()
                                                                                        .setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(BlockTags.SOUL_SPEED_BLOCKS))
                                                                        )
                                                                        .flags(EntityFlagsPredicate.Builder.flags().setIsFlying(false))
                                                        )
                                                )
                                        )
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.LOCATION_CHANGED,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.withDefaultNamespace("enchantment.soul_speed"),
                                        Attributes.MOVEMENT_EFFICIENCY,
                                        LevelBasedValue.constant(1.0F),
                                        AttributeModifier.Operation.ADD_VALUE
                                ),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity()
                                                .movementAffectedBy(
                                                        LocationPredicate.Builder.location()
                                                                .setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(BlockTags.SOUL_SPEED_BLOCKS))
                                                )
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.LOCATION_CHANGED,
                                new DamageItem(LevelBasedValue.constant(1.0F)),
                                AllOfCondition.allOf(
                                        LootItemRandomChanceCondition.randomChance(EnchantmentLevelProvider.forEnchantmentLevel(LevelBasedValue.constant(0.04F))),
                                        LootItemEntityPropertyCondition.hasProperties(
                                                LootContext.EntityTarget.THIS,
                                                EntityPredicate.Builder.entity()
                                                        .flags(EntityFlagsPredicate.Builder.flags().setOnGround(true))
                                                        .movementAffectedBy(
                                                                LocationPredicate.Builder.location()
                                                                        .setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(BlockTags.SOUL_SPEED_BLOCKS))
                                                        )
                                        )
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.TICK,
                                new SpawnParticlesEffect(
                                        ParticleTypes.SOUL,
                                        SpawnParticlesEffect.inBoundingBox(),
                                        SpawnParticlesEffect.offsetFromEntityPosition(0.1F),
                                        SpawnParticlesEffect.movementScaled(-0.2F),
                                        SpawnParticlesEffect.fixedVelocity(ConstantFloat.of(0.1F)),
                                        ConstantFloat.of(1.0F)
                                ),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, entitypredicate$builder)
                        )
                        .withEffect(
                                EnchantmentEffectComponents.TICK,
                                new PlaySoundEffect(SoundEvents.SOUL_ESCAPE, ConstantFloat.of(0.6F), UniformFloat.of(0.6F, 1.0F)),
                                AllOfCondition.allOf(
                                        LootItemRandomChanceCondition.randomChance(0.35F),
                                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, entitypredicate$builder)
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.SWIFT_SNEAK,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.LEG_ARMOR_ENCHANTABLE),
                                        1,
                                        3,
                                        Enchantment.dynamicCost(25, 25),
                                        Enchantment.dynamicCost(75, 25),
                                        8,
                                        EquipmentSlotGroup.LEGS
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.withDefaultNamespace("enchantment.swift_sneak"),
                                        Attributes.SNEAKING_SPEED,
                                        LevelBasedValue.perLevel(0.15F),
                                        AttributeModifier.Operation.ADD_VALUE
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.SHARPNESS,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.SHARP_WEAPON_ENCHANTABLE),
                                        holdergetter2.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                                        10,
                                        5,
                                        Enchantment.dynamicCost(1, 11),
                                        Enchantment.dynamicCost(21, 11),
                                        1,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                        .withEffect(EnchantmentEffectComponents.DAMAGE, new AddValue(LevelBasedValue.perLevel(1.0F, 0.5F)))
        );
        Enchantments.register(
                p_345935_,
                Enchantments.SMITE,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                        holdergetter2.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                                        5,
                                        5,
                                        Enchantment.dynamicCost(5, 8),
                                        Enchantment.dynamicCost(25, 8),
                                        2,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                        .withEffect(
                                EnchantmentEffectComponents.DAMAGE,
                                new AddValue(LevelBasedValue.perLevel(2.5F)),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(EntityTypeTags.SENSITIVE_TO_SMITE))
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.BANE_OF_ARTHROPODS,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                        holdergetter2.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                                        5,
                                        5,
                                        Enchantment.dynamicCost(5, 8),
                                        Enchantment.dynamicCost(25, 8),
                                        2,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                        .withEffect(
                                EnchantmentEffectComponents.DAMAGE,
                                new AddValue(LevelBasedValue.perLevel(2.5F)),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS))
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.POST_ATTACK,
                                EnchantmentTarget.ATTACKER,
                                EnchantmentTarget.VICTIM,
                                new ApplyMobEffect(
                                        HolderSet.direct(MobEffects.MOVEMENT_SLOWDOWN),
                                        LevelBasedValue.constant(1.5F),
                                        LevelBasedValue.perLevel(1.5F, 0.5F),
                                        LevelBasedValue.constant(3.0F),
                                        LevelBasedValue.constant(3.0F)
                                ),
                                LootItemEntityPropertyCondition.hasProperties(
                                                LootContext.EntityTarget.THIS,
                                                EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS))
                                        )
                                        .and(DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true)))
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.KNOCKBACK,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                                        5,
                                        2,
                                        Enchantment.dynamicCost(5, 20),
                                        Enchantment.dynamicCost(55, 20),
                                        2,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .withEffect(EnchantmentEffectComponents.KNOCKBACK, new AddValue(LevelBasedValue.perLevel(1.0F)))
        );
        Enchantments.register(
                p_345935_,
                Enchantments.FIRE_ASPECT,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.FIRE_ASPECT_ENCHANTABLE),
                                        holdergetter2.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                                        2,
                                        2,
                                        Enchantment.dynamicCost(10, 20),
                                        Enchantment.dynamicCost(60, 20),
                                        4,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.POST_ATTACK,
                                EnchantmentTarget.ATTACKER,
                                EnchantmentTarget.VICTIM,
                                new Ignite(LevelBasedValue.perLevel(4.0F)),
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true))
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.LOOTING,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                                        2,
                                        3,
                                        Enchantment.dynamicCost(15, 9),
                                        Enchantment.dynamicCost(65, 9),
                                        4,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.EQUIPMENT_DROPS,
                                EnchantmentTarget.ATTACKER,
                                EnchantmentTarget.VICTIM,
                                new AddValue(LevelBasedValue.perLevel(0.01F)),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(EntityType.PLAYER))
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.SWEEPING_EDGE,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                                        2,
                                        3,
                                        Enchantment.dynamicCost(5, 9),
                                        Enchantment.dynamicCost(20, 9),
                                        4,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.withDefaultNamespace("enchantment.sweeping_edge"),
                                        Attributes.SWEEPING_DAMAGE_RATIO,
                                        new LevelBasedValue.Fraction(LevelBasedValue.perLevel(1.0F), LevelBasedValue.perLevel(2.0F, 1.0F)),
                                        AttributeModifier.Operation.ADD_VALUE
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.EFFICIENCY,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.MINING_ENCHANTABLE),
                                        10,
                                        5,
                                        Enchantment.dynamicCost(1, 10),
                                        Enchantment.dynamicCost(51, 10),
                                        1,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.withDefaultNamespace("enchantment.efficiency"),
                                        Attributes.MINING_EFFICIENCY,
                                        new LevelBasedValue.LevelsSquared(1.0F),
                                        AttributeModifier.Operation.ADD_VALUE
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.SILK_TOUCH,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.MINING_LOOT_ENCHANTABLE),
                                        1,
                                        1,
                                        Enchantment.constantCost(15),
                                        Enchantment.constantCost(65),
                                        8,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.MINING_EXCLUSIVE))
                        .withEffect(EnchantmentEffectComponents.BLOCK_EXPERIENCE, new SetValue(LevelBasedValue.constant(0.0F)))
        );
        Enchantments.register(
                p_345935_,
                Enchantments.UNBREAKING,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                                        5,
                                        3,
                                        Enchantment.dynamicCost(5, 8),
                                        Enchantment.dynamicCost(55, 8),
                                        2,
                                        EquipmentSlotGroup.ANY
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.FORTUNE,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.MINING_LOOT_ENCHANTABLE),
                                        2,
                                        3,
                                        Enchantment.dynamicCost(15, 9),
                                        Enchantment.dynamicCost(65, 9),
                                        4,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.MINING_EXCLUSIVE))
        );
        Enchantments.register(
                p_345935_,
                Enchantments.POWER,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                                        10,
                                        5,
                                        Enchantment.dynamicCost(1, 10),
                                        Enchantment.dynamicCost(16, 10),
                                        1,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.DAMAGE,
                                new AddValue(LevelBasedValue.perLevel(0.5F)),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS).build()
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.PUNCH,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                                        2,
                                        2,
                                        Enchantment.dynamicCost(12, 20),
                                        Enchantment.dynamicCost(37, 20),
                                        4,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.KNOCKBACK,
                                new AddValue(LevelBasedValue.perLevel(1.0F)),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS).build()
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.FLAME,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                                        2,
                                        1,
                                        Enchantment.constantCost(20),
                                        Enchantment.constantCost(50),
                                        4,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .withEffect(EnchantmentEffectComponents.PROJECTILE_SPAWNED, new Ignite(LevelBasedValue.constant(100.0F)))
        );
        Enchantments.register(
                p_345935_,
                Enchantments.INFINITY,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                                        1,
                                        1,
                                        Enchantment.constantCost(20),
                                        Enchantment.constantCost(50),
                                        8,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.BOW_EXCLUSIVE))
                        .withEffect(
                                EnchantmentEffectComponents.AMMO_USE,
                                new SetValue(LevelBasedValue.constant(0.0F)),
                                MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.ARROW))
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.LUCK_OF_THE_SEA,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.FISHING_ENCHANTABLE),
                                        2,
                                        3,
                                        Enchantment.dynamicCost(15, 9),
                                        Enchantment.dynamicCost(65, 9),
                                        4,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .withEffect(EnchantmentEffectComponents.FISHING_LUCK_BONUS, new AddValue(LevelBasedValue.perLevel(1.0F)))
        );
        Enchantments.register(
                p_345935_,
                Enchantments.LURE,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.FISHING_ENCHANTABLE),
                                        2,
                                        3,
                                        Enchantment.dynamicCost(15, 9),
                                        Enchantment.dynamicCost(65, 9),
                                        4,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .withEffect(EnchantmentEffectComponents.FISHING_TIME_REDUCTION, new AddValue(LevelBasedValue.perLevel(5.0F)))
        );
        Enchantments.register(
                p_345935_,
                Enchantments.LOYALTY,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
                                        5,
                                        3,
                                        Enchantment.dynamicCost(12, 7),
                                        Enchantment.constantCost(50),
                                        2,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .withEffect(EnchantmentEffectComponents.TRIDENT_RETURN_ACCELERATION, new AddValue(LevelBasedValue.perLevel(1.0F)))
        );
        Enchantments.register(
                p_345935_,
                Enchantments.IMPALING,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
                                        2,
                                        5,
                                        Enchantment.dynamicCost(1, 8),
                                        Enchantment.dynamicCost(21, 8),
                                        4,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                        .withEffect(
                                EnchantmentEffectComponents.DAMAGE,
                                new AddValue(LevelBasedValue.perLevel(2.5F)),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(EntityTypeTags.SENSITIVE_TO_IMPALING)).build()
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.RIPTIDE,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
                                        2,
                                        3,
                                        Enchantment.dynamicCost(17, 7),
                                        Enchantment.constantCost(50),
                                        4,
                                        EquipmentSlotGroup.HAND
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.RIPTIDE_EXCLUSIVE))
                        .withSpecialEffect(EnchantmentEffectComponents.TRIDENT_SPIN_ATTACK_STRENGTH, new AddValue(LevelBasedValue.perLevel(1.5F, 0.75F)))
                        .withSpecialEffect(
                                EnchantmentEffectComponents.TRIDENT_SOUND,
                                List.of(SoundEvents.TRIDENT_RIPTIDE_1, SoundEvents.TRIDENT_RIPTIDE_2, SoundEvents.TRIDENT_RIPTIDE_3)
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.CHANNELING,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
                                        1,
                                        1,
                                        Enchantment.constantCost(25),
                                        Enchantment.constantCost(50),
                                        8,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.POST_ATTACK,
                                EnchantmentTarget.ATTACKER,
                                EnchantmentTarget.VICTIM,
                                AllOf.entityEffects(
                                        new SummonEntityEffect(HolderSet.direct(EntityType.LIGHTNING_BOLT.builtInRegistryHolder()), false),
                                        new PlaySoundEffect(SoundEvents.TRIDENT_THUNDER, ConstantFloat.of(5.0F), ConstantFloat.of(1.0F))
                                ),
                                AllOfCondition.allOf(
                                        WeatherCheck.weather().setThundering(true),
                                        LootItemEntityPropertyCondition.hasProperties(
                                                LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().located(LocationPredicate.Builder.location().setCanSeeSky(true))
                                        ),
                                        LootItemEntityPropertyCondition.hasProperties(
                                                LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().of(EntityType.TRIDENT)
                                        )
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.HIT_BLOCK,
                                AllOf.entityEffects(
                                        new SummonEntityEffect(HolderSet.direct(EntityType.LIGHTNING_BOLT.builtInRegistryHolder()), false),
                                        new PlaySoundEffect(SoundEvents.TRIDENT_THUNDER, ConstantFloat.of(5.0F), ConstantFloat.of(1.0F))
                                ),
                                AllOfCondition.allOf(
                                        WeatherCheck.weather().setThundering(true),
                                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().of(EntityType.TRIDENT)),
                                        LocationCheck.checkLocation(LocationPredicate.Builder.location().setCanSeeSky(true)),
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.LIGHTNING_ROD)
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.MULTISHOT,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                                        2,
                                        1,
                                        Enchantment.constantCost(20),
                                        Enchantment.constantCost(50),
                                        4,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.CROSSBOW_EXCLUSIVE))
                        .withEffect(EnchantmentEffectComponents.PROJECTILE_COUNT, new AddValue(LevelBasedValue.perLevel(2.0F)))
                        .withEffect(EnchantmentEffectComponents.PROJECTILE_SPREAD, new AddValue(LevelBasedValue.perLevel(10.0F)))
        );
        Enchantments.register(
                p_345935_,
                Enchantments.QUICK_CHARGE,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                                        5,
                                        3,
                                        Enchantment.dynamicCost(12, 20),
                                        Enchantment.constantCost(50),
                                        2,
                                        EquipmentSlotGroup.MAINHAND,
                                        EquipmentSlotGroup.OFFHAND
                                )
                        )
                        .withSpecialEffect(EnchantmentEffectComponents.CROSSBOW_CHARGE_TIME, new AddValue(LevelBasedValue.perLevel(-0.25F)))
                        .withSpecialEffect(
                                EnchantmentEffectComponents.CROSSBOW_CHARGING_SOUNDS,
                                List.of(
                                        new CrossbowItem.ChargingSounds(
                                                Optional.of(SoundEvents.CROSSBOW_QUICK_CHARGE_1), Optional.empty(), Optional.of(SoundEvents.CROSSBOW_LOADING_END)
                                        ),
                                        new CrossbowItem.ChargingSounds(
                                                Optional.of(SoundEvents.CROSSBOW_QUICK_CHARGE_2), Optional.empty(), Optional.of(SoundEvents.CROSSBOW_LOADING_END)
                                        ),
                                        new CrossbowItem.ChargingSounds(
                                                Optional.of(SoundEvents.CROSSBOW_QUICK_CHARGE_3), Optional.empty(), Optional.of(SoundEvents.CROSSBOW_LOADING_END)
                                        )
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.PIERCING,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                                        10,
                                        4,
                                        Enchantment.dynamicCost(1, 10),
                                        Enchantment.constantCost(50),
                                        1,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.CROSSBOW_EXCLUSIVE))
                        .withEffect(EnchantmentEffectComponents.PROJECTILE_PIERCING, new AddValue(LevelBasedValue.perLevel(1.0F)))
        );
        Enchantments.register(
                p_345935_,
                Enchantments.DENSITY,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.MACE_ENCHANTABLE),
                                        5,
                                        5,
                                        Enchantment.dynamicCost(5, 8),
                                        Enchantment.dynamicCost(25, 8),
                                        2,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                        .withEffect(EnchantmentEffectComponents.SMASH_DAMAGE_PER_FALLEN_BLOCK, new AddValue(LevelBasedValue.perLevel(0.375F)))
        );
        Enchantments.register(
                p_345935_,
                Enchantments.BREACH,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.MACE_ENCHANTABLE),
                                        2,
                                        4,
                                        Enchantment.dynamicCost(15, 9),
                                        Enchantment.dynamicCost(65, 9),
                                        4,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .exclusiveWith(holdergetter1.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                        .withEffect(EnchantmentEffectComponents.ARMOR_EFFECTIVENESS, new AddValue(LevelBasedValue.perLevel(-0.06F)))
        );
        Enchantments.register(
                p_345935_,
                Enchantments.WIND_BURST,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.MACE_ENCHANTABLE),
                                        2,
                                        3, // level
                                        Enchantment.dynamicCost(15, 9),
                                        Enchantment.dynamicCost(65, 9),
                                        4,
                                        EquipmentSlotGroup.MAINHAND
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.POST_ATTACK,
                                EnchantmentTarget.ATTACKER,
                                EnchantmentTarget.ATTACKER,
                                new ExplodeEffect(
                                        false,
                                        Optional.empty(),
                                        Optional.of(LevelBasedValue.lookup(List.of(1.2F, 1.75F, 2.2F), LevelBasedValue.perLevel(1.5F, 0.35F))),
                                        holdergetter3.get(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity()),
                                        Vec3.ZERO,
                                        LevelBasedValue.constant(3.5F),
                                        false,
                                        Level.ExplosionInteraction.TRIGGER,
                                        ParticleTypes.GUST_EMITTER_SMALL,
                                        ParticleTypes.GUST_EMITTER_LARGE,
                                        SoundEvents.WIND_CHARGE_BURST
                                ),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.DIRECT_ATTACKER,
                                        EntityPredicate.Builder.entity()
                                                .flags(EntityFlagsPredicate.Builder.flags().setIsFlying(false))
                                                .moving(MovementPredicate.fallDistance(MinMaxBounds.Doubles.atLeast(1.5)))
                                )
                        )
        );
        Enchantments.register(
                p_345935_,
                Enchantments.MENDING,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                                        2,
                                        1,
                                        Enchantment.dynamicCost(25, 25),
                                        Enchantment.dynamicCost(75, 25),
                                        4,
                                        EquipmentSlotGroup.ANY
                                )
                        )
                        .withEffect(EnchantmentEffectComponents.REPAIR_WITH_XP, new MultiplyValue(LevelBasedValue.constant(2.0F)))
        );
        Enchantments.register(
                p_345935_,
                Enchantments.VANISHING_CURSE,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        holdergetter2.getOrThrow(ItemTags.VANISHING_ENCHANTABLE),
                                        1,
                                        1,
                                        Enchantment.constantCost(25),
                                        Enchantment.constantCost(50),
                                        8,
                                        EquipmentSlotGroup.ANY
                                )
                        )
                        .withEffect(EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)
        );
    }
}
