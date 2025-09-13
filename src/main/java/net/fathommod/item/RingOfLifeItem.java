
package net.fathommod.item;

import net.fathommod.init.FathommodModKeyMappings;
import net.fathommod.network.FathommodModVariables;
import net.fathommod.trinket.OneTimeEffectTrinket;
import net.fathommod.trinket.TickTrinket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RingOfLifeItem extends Item implements OneTimeEffectTrinket, TickTrinket {
	public RingOfLifeItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> components, @NotNull TooltipFlag tooltipFlag) {
		components.add(Component.translatable("tooltip.fathommod.ring_of_life.second_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
		try {
			components.add(Component.translatable("tooltip.fathommod.generic_trinket.keybind", FathommodModKeyMappings.TRINKETERY_OPEN.getKey().getDisplayName()).setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)));
		} catch (RuntimeException ignored) {}

		super.appendHoverText(stack, context, components, tooltipFlag);
	}

	@Override
	public void removeEffect(Player entity, ItemStack stack) {
		FathommodModVariables.EntityVariables vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
		vars.ringOfLifeRegenCooldown = 60;
		vars.syncPlayerVariables(entity);
	}

	@Override
	public void applyEffect(Player player, ItemStack stack) {}

	@Override
	public void tick(Player entity) {
		FathommodModVariables.EntityVariables vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
		if (vars.ringOfLifeRegenCooldown <= 0) {
			entity.heal(1F);
			vars.ringOfLifeRegenCooldown = 60;
		} else {
			vars.ringOfLifeRegenCooldown -= 1;
		}
		vars.syncPlayerVariables(entity);
	}
}
