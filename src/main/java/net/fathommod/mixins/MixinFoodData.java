package net.fathommod.mixins;

import net.fathommod.network.FathommodModVariables;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fathommod.network.FathommodModVariables.MapVariables;

@Mixin(FoodData.class)
public abstract class MixinFoodData { // responsible for slowing down vanilla's natural regeneration if masochist mode is on
    @Shadow private int lastFoodLevel;

    @Shadow private int foodLevel;

    @Shadow private float exhaustionLevel;

    @Shadow private float saturationLevel;

    @Shadow public abstract void addExhaustion(float p_38704_);

    @Shadow private int tickTimer;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(Player player, CallbackInfo callbackInfo) {
        if (player.level().isClientSide())
            return;
        callbackInfo.cancel();
        Difficulty difficulty = player.level().getDifficulty();
        lastFoodLevel = foodLevel;
        if (exhaustionLevel > 4.0F) {
            exhaustionLevel -= 4.0F;
            if (saturationLevel > 0.0F) {
                saturationLevel = Math.max(saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                foodLevel = Math.max(foodLevel - 1, 0);
            }
        }

        MapVariables vars = MapVariables.get(player.level());
        boolean flag = player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
        if (flag && saturationLevel > 0.0F && player.isHurt() && foodLevel >= 20) {
            tickTimer++;
            if (tickTimer >= (vars.isMasochistModeEnabled() ? 12 : 10)) {
                float f = Math.min(saturationLevel, 6.0F) / 6f;
                if (player.getHealth() + f / 6f >= player.getData(FathommodModVariables.ENTITY_VARIABLES).getPosionAffectedCap(player.getMaxHealth()))
                    f -= player.getData(FathommodModVariables.ENTITY_VARIABLES).getPosionAffectedCap(player.getMaxHealth()) - player.getHealth();
                player.heal(f);
                addExhaustion(f * 6);
                tickTimer = 0;
            }
        } else if (flag && foodLevel >= 18 && player.isHurt()) {
            tickTimer++;
            if (tickTimer >= (vars.isMasochistModeEnabled() ? 96 : 80)) {
                if (player.getHealth() + 1 >= player.getData(FathommodModVariables.ENTITY_VARIABLES).getPosionAffectedCap(player.getMaxHealth())) {
                    player.heal(player.getData(FathommodModVariables.ENTITY_VARIABLES).getPosionAffectedCap(player.getMaxHealth()) - player.getHealth());
                } else {
                    player.heal(1.0F);
                    addExhaustion(6.0F);
                    tickTimer = 0;
                }
            }
        } else if (foodLevel <= 0) {
            tickTimer++;
            if (tickTimer >= 80) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.hurt(player.damageSources().starve(), 1.0F);
                }

                tickTimer = 0;
            }
        } else {
            tickTimer = 0;
        }
    }
}