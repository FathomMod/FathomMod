package net.fathommod.entity;

import net.fathommod.init.FathommodModDamageTypes;
import net.fathommod.init.FathommodModEntities;
import net.fathommod.init.FathommodModMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ButchersCleaveProjectile extends ThrowableProjectile implements GeoEntity {
    public static final RawAnimation SPIN = RawAnimation.begin().thenLoop("cleave_speen");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public ButchersCleaveProjectile(Level p_37249_) {
        super(FathommodModEntities.BUTCHERS_CLEAVE_PROJECTILE.get(), p_37249_);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "persistent", this::predicate));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> geoAnimatableAnimationState) {
        geoAnimatableAnimationState.setAnimation(SPIN);
        return PlayState.CONTINUE;
    }

    public float damage = 8;

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putFloat("projDmg", damage);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        damage = nbt.getFloat("projDmg");
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult p_37258_) {
        super.onHitBlock(p_37258_);
        this.discard();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        result.getEntity().hurt(new DamageSource(result.getEntity().level().holderOrThrow(FathommodModDamageTypes.BUTCHERS_CLEAVE), this, this.getOwner()), ((result.getEntity().getBoundingBox().maxY - result.getEntity().getBoundingBox().minY) * .8 + result.getEntity().getBoundingBox().minY) <= this.getY() ? damage * 3 : damage);
        if (result.getEntity() instanceof LivingEntity entity) {
            entity.addEffect(new MobEffectInstance(FathommodModMobEffects.BLEED, 65, 0, false, false, false));
        }
        this.discard();
    }
}
