
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

public class HandleExtensionItem extends Item implements MultiAttributeTrinket {
	public HandleExtensionItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> components, @NotNull TooltipFlag tooltipFlag) {
		components.add(Component.translatable("tooltip.fathommod.handle_extension.second_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
		try {
			components.add(Component.translatable("tooltip.fathommod.generic_trinket.keybind", FathommodModKeyMappings.TRINKETERY_OPEN.getKey().getDisplayName()).setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)));
		} catch (RuntimeException ignored) {}

		super.appendHoverText(stack, context, components, tooltipFlag);
	}

	@Override
	public ArrayList<Holder<Attribute>> getAttributes() {
		return new ArrayList<>(List.of(Attributes.ENTITY_INTERACTION_RANGE, Attributes.BLOCK_INTERACTION_RANGE));
	}

	@Override
	public List<AttributeModifier> getModifiers() {
		return new ArrayList<>(List.of(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "handle_extension_trinket_entity_reach_modifier"), 1, AttributeModifier.Operation.ADD_VALUE), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "handle_extension_block_modifier"), 1, AttributeModifier.Operation.ADD_VALUE)));
	}
}
