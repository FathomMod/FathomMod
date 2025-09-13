
package net.fathommod.item;

import net.fathommod.FathommodMod;
import net.fathommod.init.FathommodModKeyMappings;
import net.fathommod.trinket.MultiAttributeTrinket;
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

public class BalloonItem extends Item implements MultiAttributeTrinket {
	public BalloonItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> components, @NotNull TooltipFlag tooltipFlag) {
		components.add(Component.translatable("tooltip.fathommod.balloon.first_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.GRAY)));
		components.add(Component.translatable("tooltip.fathommod.balloon.second_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
		components.add(Component.translatable("tooltip.fathommod.balloon.third_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
		try {
			components.add(Component.translatable("tooltip.fathommod.generic_trinket.keybind", FathommodModKeyMappings.TRINKETERY_OPEN.getKey().getDisplayName()).setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)));
		} catch (RuntimeException ignored) {}
		super.appendHoverText(stack, context, components, tooltipFlag);
	}

	@Override
	public ArrayList<Holder<Attribute>> getAttributes() {
		return new ArrayList<>(List.of(Attributes.JUMP_STRENGTH, Attributes.GRAVITY, Attributes.FALL_DAMAGE_MULTIPLIER));
	}

	@Override
	public List<AttributeModifier> getModifiers() {
		return new ArrayList<>(List.of(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "balloon_jump_modifier"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "balloon"), -.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "balloon_fall_dmg_modifier"), -999, AttributeModifier.Operation.ADD_VALUE)));
	}
}
