
package net.fathommod.item;

import net.fathommod.FathommodMod;
import net.fathommod.init.FathommodModAttributes;
import net.fathommod.init.FathommodModKeyMappings;
import net.fathommod.trinket.AttributeTrinket;
import net.fathommod.trinket.OneTimeEffectTrinket;
import net.fathommod.trinket.TickTrinket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ArmorPolishItem extends Item implements AttributeTrinket, OneTimeEffectTrinket, TickTrinket {
	public ArmorPolishItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> components, @NotNull TooltipFlag tooltipFlag) {
		components.add(Component.translatable("tooltip.fathommod.armor_polish.second_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
		try {
			components.add(Component.translatable("tooltip.fathommod.generic_trinket.keybind", FathommodModKeyMappings.TRINKETERY_OPEN.getKey().getDisplayName()).setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)));
		} catch (RuntimeException ignored) {}

		super.appendHoverText(stack, context, components, tooltipFlag);
	}

	@Override
	public AttributeModifier getModifier() {
		return new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "armor_polish_trinket_modifier"), 2, AttributeModifier.Operation.ADD_VALUE);
	}

	@Override
	public Holder<Attribute> getAttribute() {
		return FathommodModAttributes.ARMOR_DEFENSE;
	}

	@Override
	public void applyEffect(Player player, ItemStack stack) {}

	@Override
	public void removeEffect(Player player, ItemStack stack) {
		ArmorPolishItem.clearArmorPolishModifier(player);
	}

	@SuppressWarnings("DataFlowIssue")
	private static void clearArmorPolishModifier(Player player) {
		ArrayList<AttributeModifier> modifiersToRemove = new ArrayList<>();
		player.getAttribute(FathommodModAttributes.ARMOR_DEFENSE.getDelegate()).getModifiers().forEach(modifier -> {
			if (modifier.id().getPath().contains("armor_polish") && !modifier.id().getPath().contains("trinket"))
				modifiersToRemove.add(modifier);
		});
		modifiersToRemove.forEach(modifier -> player.getAttribute(FathommodModAttributes.ARMOR_DEFENSE.getDelegate()).removeModifier(modifier));
	}

	@Override
	@SuppressWarnings("DataFlowIssue")
	public void tick(Player player) {
		clearArmorPolishModifier(player);
		EquipmentSlot[] armorSlots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
		for (EquipmentSlot slot : armorSlots) {
			AtomicReference<Double> amount = new AtomicReference<>((double) 0);
			player.getItemBySlot(slot).getAttributeModifiers().modifiers().stream().filter(entry -> entry.attribute() == FathommodModAttributes.ARMOR_DEFENSE.getDelegate()).forEach(entry -> amount.set(amount.get() + (entry.modifier().amount() / 4)));
			player.getAttribute(FathommodModAttributes.ARMOR_DEFENSE.getDelegate()).addOrUpdateTransientModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "armor_polish_" + slot.name().toLowerCase())
					, amount.get()
					, AttributeModifier.Operation.ADD_VALUE));
		}
	}
}
