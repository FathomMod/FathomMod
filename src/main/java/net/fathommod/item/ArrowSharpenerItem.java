
package net.fathommod.item;

import net.fathommod.init.FathommodModKeyMappings;
import net.fathommod.trinket.Trinket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArrowSharpenerItem extends Item implements Trinket {
	public ArrowSharpenerItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack p_41421_, @NotNull TooltipContext p_339594_, List<Component> components, @NotNull TooltipFlag p_41424_) {
		components.add(Component.translatable("tooltip.fathommod.arrow_sharpener.first_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
		try {
			components.add(Component.translatable("tooltip.fathommod.generic_trinket.keybind", FathommodModKeyMappings.TRINKETERY_OPEN.getKey().getDisplayName()).setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)));
		} catch (RuntimeException ignored) {}
		super.appendHoverText(p_41421_, p_339594_, components, p_41424_);
	}
}
