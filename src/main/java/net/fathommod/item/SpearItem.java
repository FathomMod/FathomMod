
package net.fathommod.item;

import net.fathommod.DamageClasses;
import net.fathommod.DamageTypedWeapon;
import net.fathommod.DevUtils;
import net.fathommod.FathommodMod;
import net.fathommod.item.types.AbstractSpearItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpearItem extends AbstractSpearItem implements DamageTypedWeapon, GeoItem {
    AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public SpearItem() {
		super(new Item.Properties().attributes(new ItemAttributeModifiers(List.of(new ItemAttributeModifiers.Entry(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "basic_spear_modifier"), 3.5, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)), false)));
	}

    @Override
    public float attackSpeed() {
        return 1.1f;
    }

    @Override
    public float getDamage() {
        return 8f;
    }

    @Override
    public List<Component> descriptions() {
        return List.of(Component.translatable("tooltip.fathommod.basic_spear.first_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public List<Component> passiveAbilities() {
        ArrayList<Component> list = new ArrayList<>(super.passiveAbilities());
        list.add(Component.translatable("tooltip.fathommod.basic_spear.second_line").withColor(DevUtils.INFO_TOOLTIPS_HEX));
        return list;
    }

    @Override
	public boolean hasCraftingRemainingItem(@NotNull ItemStack stack) {
		return true;
	}

	@Override
	public @NotNull ItemStack getCraftingRemainingItem(ItemStack itemstack) {
		ItemStack retval = new ItemStack(this);
		retval.setDamageValue(itemstack.getDamageValue() + 1);
		if (retval.getDamageValue() >= retval.getMaxDamage()) {
			return ItemStack.EMPTY;
		}
		return retval;
	}

	@Override
	public boolean isRepairable(@NotNull ItemStack itemstack) {
		return false;
	}

	@Override
	public DamageClasses getDamageClass() {
		return DamageClasses.MELEE;
	}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    private static class SpearRenderer extends GeoItemRenderer<SpearItem> {
        private static class SpearModel extends GeoModel<SpearItem> {
            @Override
            public ResourceLocation getModelResource(SpearItem animatable) {
                return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "geo/spear.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(SpearItem animatable) {
                return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "textures/item/spear.png");
            }

            @Override
            public ResourceLocation getAnimationResource(SpearItem animatable) {
                return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "animations/ted_animation_set.json");
            }
        }
        public SpearRenderer() {
            super(new SpearModel());
        }
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            final SpearRenderer renderer = new SpearRenderer();

            @Override
            public @Nullable BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
