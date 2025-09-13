package net.fathommod.mixins.projectiles;

import net.fathommod.entity.ted.ROCK;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrowableProjectile.class)
public abstract class MixinThrowableProjectile { // Makes water resistance much less
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        ci.cancel();
        ThrowableProjectile instance = (ThrowableProjectile) (Object) this; 
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(instance, this::fathomMod$canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS && !net.neoforged.neoforge.event.EventHooks.onProjectileImpact(instance, hitresult)) {
            instance.hitTargetOrDeflectSelf(hitresult);
        }

        instance.checkInsideBlocks();
        Vec3 vec3 = instance.getDeltaMovement();
        double d0 = instance.getX() + vec3.x;
        double d1 = instance.getY() + vec3.y;
        double d2 = instance.getZ() + vec3.z;
        instance.updateRotation();
        float f;
        if (instance.isInWater()) {
            for (int i = 0; i < 4; i++) {
                instance.level().addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * 0.25, d1 - vec3.y * 0.25, d2 - vec3.z * 0.25, vec3.x, vec3.y, vec3.z);
            }

            f = 0.915F;
        } else {
            f = instance instanceof ROCK ? 1 : .99f;
        }

        instance.setDeltaMovement(vec3.scale(f));
        instance.applyGravity();
        instance.setPos(d0, d1, d2);
    }

    @Unique
    private boolean fathomMod$canHitEntity(Entity entity) {
        ThrowableProjectile instance = (ThrowableProjectile) (Object) this;
        if (!entity.canBeHitByProjectile()) {
            return false;
        } else {
            Entity owner = instance.getOwner();
            return owner == null || instance.leftOwner || !entity.isPassengerOfSameVehicle(owner);
        }
    }
}