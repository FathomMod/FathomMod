
package net.fathommod.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RiotShieldItem extends ShieldItem {
	public RiotShieldItem() {
		super(new Item.Properties().fireResistant().stacksTo(1));
	}

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack p_43105_) {
        return UseAnim.BLOCK;
    }

    @Override
	public void appendHoverText(@NotNull ItemStack p_43094_, @NotNull TooltipContext p_339613_, @NotNull List<Component> p_43096_, @NotNull TooltipFlag p_43097_) {
		p_43096_.add(Component.translatable("tooltip.fathommod.riot_shield.first_line").withStyle(ChatFormatting.GRAY));
	}
}
