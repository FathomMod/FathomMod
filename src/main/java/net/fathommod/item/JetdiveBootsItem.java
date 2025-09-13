
package net.fathommod.item;

import net.fathommod.FathommodMod;
import net.fathommod.init.FathommodModKeyMappings;
import net.fathommod.trinket.AttributeTrinket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JetdiveBootsItem extends Item implements AttributeTrinket {
	public JetdiveBootsItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> components, @NotNull TooltipFlag tooltipFlag) {
		components.add(Component.translatable("tooltip.fathommod.achelous_boots.first_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.GRAY)));
		components.add(Component.translatable("tooltip.fathommod.achelous_boots.second_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
		try {
			components.add(Component.translatable("tooltip.fathommod.generic_trinket.keybind", FathommodModKeyMappings.TRINKETERY_OPEN.getKey().getDisplayName()).setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)));
		} catch (RuntimeException ignored) {}

		super.appendHoverText(stack, context, components, tooltipFlag);
	}

	@Override
	public Holder<Attribute> getAttribute() {
		return NeoForgeMod.SWIM_SPEED;
	}

	@Override
	public AttributeModifier getModifier() {
		return new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "water_crack"), 1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
	}
}
