package net.fathommod.effect;

import net.fathommod.init.FathommodModDamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class BleedEffect extends MobEffect {
    public BleedEffect() {
        super(MobEffectCategory.HARMFUL, 0x000000);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        entity.invulnerableTime = 0;
        entity.hurt(new DamageSource(entity.level().holderOrThrow(FathommodModDamageTypes.BLEED_EFFECT)), 1 + amplifier);
        entity.invulnerableTime = 0;
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
