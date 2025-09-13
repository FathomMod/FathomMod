
package net.fathommod.item;

import net.fathommod.FathommodMod;
import net.fathommod.init.FathommodModAttributes;
import net.fathommod.init.FathommodModKeyMappings;
import net.fathommod.init.FathommodModMobEffects;
import net.fathommod.trinket.OneTimeEffectTrinket;
import net.fathommod.trinket.TickTrinket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SeasGiftItem extends Item implements TickTrinket, OneTimeEffectTrinket {
	public SeasGiftItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(10).saturationModifier(10f).alwaysEdible().effect(() -> new MobEffectInstance(FathommodModMobEffects.WASTED_TRINKET, 10101, 0, false, false, true), 1).build()));
	}

	@Override
	public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemstack) {
		return UseAnim.EAT;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> components, @NotNull TooltipFlag tooltipFlag) {
		components.add(Component.translatable("tooltip.fathommod.seas_gift.first_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.GRAY)));
		components.add(Component.translatable("tooltip.fathommod.seas_gift.second_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
		try {
			components.add(Component.translatable("tooltip.fathommod.generic_trinket.keybind", FathommodModKeyMappings.TRINKETERY_OPEN.getKey().getDisplayName()).setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)));
		} catch (RuntimeException ignored) {}
		super.appendHoverText(stack, context, components, tooltipFlag);
	}

	@SuppressWarnings("DataFlowIssue")
    @Override
	public void tick(Player entity) {
		if (entity.isUnderWater()) {
			entity.getAttribute(Attributes.OXYGEN_BONUS).addOrUpdateTransientModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "seas_gift_trinket_oxygen_bonus"), .6667, AttributeModifier.Operation.ADD_VALUE));
			entity.getAttribute(FathommodModAttributes.ARMOR_DEFENSE.getDelegate()).addOrUpdateTransientModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "seas_gift_trinket_modifier"), 6, AttributeModifier.Operation.ADD_VALUE));
		} else if (entity.isInRain()) {
			entity.getAttribute(Attributes.OXYGEN_BONUS).removeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "seas_gift_trinket_oxygen_bonus"));
			entity.getAttribute(FathommodModAttributes.ARMOR_DEFENSE.getDelegate()).addOrUpdateTransientModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "seas_gift_trinket_modifier"), 3, AttributeModifier.Operation.ADD_VALUE));
		} else {
			entity.getAttribute(Attributes.OXYGEN_BONUS).removeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "seas_gift_trinket_oxygen_bonus"));
			entity.getAttribute(FathommodModAttributes.ARMOR_DEFENSE.getDelegate()).removeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "seas_gift_trinket_modifier"));
		}
	}

	@Override
	public void applyEffect(Player player, ItemStack stack) {}

	@Override
	@SuppressWarnings("DataFlowIssue")
	public void removeEffect(Player entity, ItemStack stack) {
		entity.getAttribute(Attributes.OXYGEN_BONUS).removeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "seas_gift_trinket_oxygen_bonus"));
		entity.getAttribute(FathommodModAttributes.ARMOR_DEFENSE.getDelegate()).removeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "seas_gift_trinket_modifier"));
	}
}
