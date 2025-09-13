
package net.fathommod.entity;

import net.fathommod.FathommodMod;
import net.fathommod.init.FathommodModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.GeckoLibUtil;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class ThrowingKnivesEntity extends AbstractArrow implements ItemSupplier, GeoEntity {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(FathommodModItems.THROWING_KNIVES.get());

	public ThrowingKnivesEntity(EntityType<? extends ThrowingKnivesEntity> type, Level world) {
		super(type, world);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	public static class ThrowingKnivesModel extends GeoModel<ThrowingKnivesEntity> {
		@Override
		public ResourceLocation getModelResource(ThrowingKnivesEntity animatable) {
			return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "geo/throwing_knife.geo.json");
		}

		@Override
		public ResourceLocation getTextureResource(ThrowingKnivesEntity animatable) {
			return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "textures/entity/entityknifetexture.png");
		}

		@Override
		public ResourceLocation getAnimationResource(ThrowingKnivesEntity animatable) {
			return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "animations/ted_animation_set.json");
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public @NotNull ItemStack getItem() {
		return PROJECTILE_ITEM;
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return new ItemStack(FathommodModItems.THROWING_KNIVES.get());
	}

	@Override
	protected void doPostHurtEffects(LivingEntity entity) {
		super.doPostHurtEffects(entity);
		entity.setArrowCount(entity.getArrowCount() - 1);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.inGround)
			this.discard();
	}
}
