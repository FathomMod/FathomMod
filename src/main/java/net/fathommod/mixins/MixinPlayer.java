package net.fathommod.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fathommod.DevUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class MixinPlayer { // fixes minecraft's code for using entity reach (makes it use eye level to hitbox part that has been hit distance rather than feet to feet distance)
    @ModifyReturnValue(method = "canInteractWithEntity(Lnet/minecraft/world/phys/AABB;D)Z", at = @At("RETURN"))
    private boolean canInteractWithAABB(boolean originalValue, AABB targetBox, double distance) {
        Player instance = (Player) (Object) this;
        return DevUtils.performPreciseRaycast(instance, instance.level(), instance.getEyePosition(), instance.getLookAngle().normalize(), instance.entityInteractionRange()) != null || originalValue;
    }
}
