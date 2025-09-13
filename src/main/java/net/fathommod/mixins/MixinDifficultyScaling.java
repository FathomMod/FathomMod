package net.fathommod.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fathommod.BossEntity;
import net.fathommod.network.FathommodModVariables;
import net.minecraft.world.damagesource.DamageScaling;
import net.neoforged.neoforge.common.damagesource.IScalingFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DamageScaling.class)
public class MixinDifficultyScaling {
    @ModifyReturnValue(method = "getScalingFunction", at = @At("RETURN"))
    private IScalingFunction getScalingFunction(IScalingFunction original) {
        if (original == IScalingFunction.DEFAULT)
            return (source, target, amount, difficulty) -> {
                FathommodModVariables.MapVariables vars = FathommodModVariables.MapVariables.get(target.level());
                if (vars.isMasochistModeEnabled() && source.getEntity() instanceof BossEntity)
                    return amount * 1.5f;
                else if (source.getEntity() instanceof BossEntity)
                    return amount;
                //noinspection deprecation
                if (source.scalesWithDifficulty())
                    return switch (target.level().getDifficulty()) {
                            case PEACEFUL -> source.getEntity() instanceof BossEntity ? amount : 0.0F;
                            case EASY -> {
                                float multiplier = .5f;
                                if (vars.isMasochistModeEnabled())
                                    multiplier *= 1.35f;
                                yield Math.min(amount * multiplier + 1, amount);
                            }
                            case NORMAL -> amount * (vars.isMasochistModeEnabled() ? 1.35f : 1);
                            case HARD -> {
                                float multiplier = source.getEntity() instanceof BossEntity ? 1.25f : 1.5f;
                                if (vars.isMasochistModeEnabled()) {
                                    multiplier *= 1.35f;
                                }
                                yield amount * multiplier;
                            }
                        };
                return amount;
            };
        return original;
    }
}
