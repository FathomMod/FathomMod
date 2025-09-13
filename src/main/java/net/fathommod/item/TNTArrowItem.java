
package net.fathommod.item;

import net.fathommod.DamageClasses;
import net.fathommod.DevUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TNTArrowItem extends Item {
	public TNTArrowItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> components, @NotNull TooltipFlag tooltipFlag) {
		components.add(Component.translatable("tooltip.fathommod.tnt_arrow.first_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
		components.add(Component.translatable("tooltip.fathommod.generic_damage", 18).withColor(DevUtils.DAMAGE_TOOLTIPS_HEX).append(Component.translatable(DamageClasses.RANGED.getComponent()).withColor(DamageClasses.RANGED.getColor())));
		super.appendHoverText(stack, context, components, tooltipFlag);
	}
}
