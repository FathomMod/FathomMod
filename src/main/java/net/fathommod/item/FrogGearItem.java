
package net.fathommod.item;

import net.fathommod.FathommodMod;
import net.fathommod.Trinkets;
import net.fathommod.init.FathommodModKeyMappings;
import net.fathommod.trinket.MultiAttributeTrinket;
import net.fathommod.trinket.Trinket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FrogGearItem extends Item implements MultiAttributeTrinket {
	public FrogGearItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> components, @NotNull TooltipFlag tooltipFlag) {
		components.add(Component.translatable("tooltip.fathommod.frog_gear.first_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
		components.add(Component.translatable("tooltip.fathommod.frog_gear.second_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
		try {
			components.add(Component.translatable("tooltip.fathommod.generic_trinket.keybind", FathommodModKeyMappings.TRINKETERY_OPEN.getKey().getDisplayName()).setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)));
		} catch (RuntimeException ignored) {}

		super.appendHoverText(stack, context, components, tooltipFlag);
	}


	@Override
	public ArrayList<Holder<Attribute>> getAttributes() {
		return new ArrayList<>(List.of(Attributes.JUMP_STRENGTH, Attributes.SAFE_FALL_DISTANCE));
	}

	@Override
	public List<AttributeModifier> getModifiers() {
		return new ArrayList<>(List.of(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "jump_trinkets_jump_power_modifier"), 0.9, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "jump_trinkets_safe_fall_dist_mod"), 4, AttributeModifier.Operation.ADD_VALUE)));
	}

	@Override
	public ArrayList<Trinket> incompatibleTrinkets() {
		return new ArrayList<>(List.of((Trinket) Trinkets.JUMP_HEIGHT));
	}
}
