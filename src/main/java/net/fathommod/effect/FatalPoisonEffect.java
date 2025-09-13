package net.fathommod.effect;

import net.fathommod.init.FathommodModDamageTypes;
import net.fathommod.network.FathommodModVariables;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class FatalPoisonEffect extends MobEffect {
    public FatalPoisonEffect(MobEffectCategory p_19451_, int p_19452_) {
        super(p_19451_, p_19452_);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        entity.invulnerableTime = 0;
        entity.hurt(new DamageSource(entity.level().holderOrThrow(entity.getData(FathommodModVariables.ENTITY_VARIABLES).isPaltnPoisoned ? (Math.random() > .5 ? FathommodModDamageTypes.FATAL_POISON_PALTN_1 : FathommodModDamageTypes.FATAL_POISON_PALTN_2) : DamageTypes.MAGIC)), entity instanceof Player && entity.getData(FathommodModVariables.ENTITY_VARIABLES).isPaltnPoisoned ? Math.clamp(entity.getHealth() - Mth.lerp(.6f, 0, entity.getMaxHealth()), 0, 2) : 2);
        entity.invulnerableTime = 0;
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 10 == 0;
    }
}
