
package net.fathommod.entity;

import net.fathommod.FathommodMod;
import net.fathommod.init.FathommodModDamageTypes;
import net.fathommod.network.FathommodModPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.GeckoLibUtil;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class TNTArrowEntity extends ThrowableProjectile implements GeoEntity {

	public TNTArrowEntity(EntityType<? extends TNTArrowEntity> type, Level world, float damage) {
		super(type, world);
		this.damage = damage + 18;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	public static class TNTArrowModel extends GeoModel<TNTArrowEntity> {
		@Override
		public ResourceLocation getModelResource(TNTArrowEntity animatable) {
			return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "geo/tnt_arrow.geo.json");
		}

		@Override
		public ResourceLocation getTextureResource(TNTArrowEntity animatable) {
			return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "textures/entity/youmdf.png");
		}

		@Override
		public ResourceLocation getAnimationResource(TNTArrowEntity animatable) {
			return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "animations/ted_animation_set.json");
		}
	}

	public float damage;

	@Override
	protected void onHit(@NotNull HitResult result) {
		super.onHit(result);
		if (this.level() instanceof ServerLevel world) {
            this.discard();
			this.level().playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()),
					SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 4, (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F);
			for (int i = 0; i < 80; i++)
				PacketDistributor.sendToAllPlayers(new FathommodModPackets.UpdateParticleScale(2f), new FathommodModPackets.SpawnParticle((float) (this.getX() + ((Math.random() - .5) * 6)), (float) this.getY(), (float) (this.getZ() + ((Math.random() - .5) * 6)), (float) ((Math.random() - .5f) / 3), (float) (Math.random() / 2.5), (float) ((Math.random() - .5f) / 3)));
			for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, new AABB(this.getX() - 2.5, this.getY() - 3, this.getZ() - 2.5, this.getX() + 2.5, this.getY() + 3, this.getZ() + 2.5))) {
				entity.hurt(new DamageSource(this.level().holderOrThrow(FathommodModDamageTypes.FAKE_EXPLOSION), this.getOwner()), damage);
			}
		}
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.@NotNull Builder p_326003_) {}
}
