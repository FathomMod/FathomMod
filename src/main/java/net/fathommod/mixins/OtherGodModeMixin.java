package net.fathommod.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fathommod.network.FathommodModVariables;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class OtherGodModeMixin {
    @ModifyReturnValue(method = "hurt", at = @At("RETURN"))
    public boolean hurt(boolean original, DamageSource p_9037_, float p_9038_) {
        return !((Player) (Object) this).getData(FathommodModVariables.ENTITY_VARIABLES).isGodMode && original;
    }
}
