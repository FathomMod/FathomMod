
package net.fathommod.item;

import net.fathommod.DamageClasses;
import net.fathommod.DamageTypedWeapon;
import net.fathommod.FathommodMod;
import net.fathommod.TwoHandedItem;
import net.fathommod.item.types.ScytheItem;
import net.fathommod.procedures.PaltnScytheRightclickedProcedure;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class PaltnScytheItem extends ScytheItem implements DamageTypedWeapon, TwoHandedItem, GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	@Override
	public boolean shouldDisplayItemInOffhand() {
		return false;
	}

	public PaltnScytheItem() {
		super(new Properties());
	}

    @Override
    public float attackSpeed() {
        return 0.6f;
    }

    @Override
    public float getDamage() {
        return 22;
    }

    @Override
	public float getDestroySpeed(@NotNull ItemStack itemstack, @NotNull BlockState blockstate) {
		return 1;
	}

	@Override
	public boolean mineBlock(ItemStack itemstack, @NotNull Level world, @NotNull BlockState blockstate, @NotNull BlockPos pos, @NotNull LivingEntity entity) {
		itemstack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
		return true;
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, @NotNull LivingEntity entity, @NotNull LivingEntity sourceentity) {
		itemstack.hurtAndBreak(2, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
		return true;
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player entity, @NotNull InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		PaltnScytheRightclickedProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity);
		return ar;
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}

    @Override
    public List<Component> descriptions() {
        return List.of(Component.translatable("tooltip.fathommod.paltn_scythe.first_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public List<Component> activeAbilities() {
        return List.of(Component.translatable("tooltip.fathommod.paltn_scythe.second_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
    }

    public static final class PaltnScytheModel extends GeoModel<PaltnScytheItem> {
        @Override
        public ResourceLocation getModelResource(PaltnScytheItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "geo/paltn_scythe.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(PaltnScytheItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "textures/item/paltn_scythe.png");
        }

        @Override
        public ResourceLocation getAnimationResource(PaltnScytheItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "animations/ted_animation_set.json");
        }
    }

    public static final class PaltnScytheRenderer extends GeoItemRenderer<PaltnScytheItem> {
        public PaltnScytheRenderer(GeoModel<PaltnScytheItem> model) {
            super(model);
        }
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private PaltnScytheRenderer renderer = null;

            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null)
                    renderer = new PaltnScytheRenderer(new PaltnScytheModel());
                return renderer;
            }
        });
    }

	@Override
	public DamageClasses getDamageClass() {
		return DamageClasses.MELEE;
	}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
