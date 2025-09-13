
package net.fathommod.item;

import net.fathommod.init.FathommodModKeyMappings;
import net.fathommod.trinket.OneTimeEffectTrinket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.Unbreakable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlackSmithsWillItem extends Item implements OneTimeEffectTrinket {
	public BlackSmithsWillItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack p_41421_, @NotNull TooltipContext p_339594_, @NotNull List<Component> components, @NotNull TooltipFlag p_41424_) {
		components.add(Component.translatable("tooltip.fathommod.black_smiths_will.first_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.GRAY)));
		components.add(Component.translatable("tooltip.fathommod.black_smiths_will.second_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
		try {
			components.add(Component.translatable("tooltip.fathommod.generic_trinket.keybind", FathommodModKeyMappings.TRINKETERY_OPEN.getKey().getDisplayName()).setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)));
		} catch (RuntimeException ignored) {}

		super.appendHoverText(p_41421_, p_339594_, components, p_41424_);
	}

	@Override
	public void applyEffect(Player player, ItemStack trinket) {
		ItemStack feet;
		ItemStack legs;
		ItemStack chest;
		ItemStack head;
		feet = player.getItemBySlot(EquipmentSlot.FEET);
		legs = player.getItemBySlot(EquipmentSlot.LEGS);
		chest = player.getItemBySlot(EquipmentSlot.CHEST);
		head = player.getItemBySlot(EquipmentSlot.HEAD);
		for (ItemStack stack : new ItemStack[] {feet, legs, chest, head}) {
			if (stack.isEmpty())
				continue;
			CustomData data = stack.get(DataComponents.CUSTOM_DATA);
			if (data == null) {
				CompoundTag tag = new CompoundTag();
				stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
			}
			if (!stack.has(DataComponents.UNBREAKABLE)) {
				CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean("__fathommod__unbreakable_by_trinket", true));
				stack.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
			}
		}
	}

	@Override
	public void removeEffect(Player player, ItemStack trinket) {
		ItemStack feet;
		ItemStack legs;
		ItemStack chest;
		ItemStack head;
		feet = player.getItemBySlot(EquipmentSlot.FEET);
		legs = player.getItemBySlot(EquipmentSlot.LEGS);
		chest = player.getItemBySlot(EquipmentSlot.CHEST);
		head = player.getItemBySlot(EquipmentSlot.HEAD);
		for (ItemStack stack : new ItemStack[] {feet, legs, chest, head}) {
			if (stack.isEmpty())
				continue;
			CustomData data = stack.get(DataComponents.CUSTOM_DATA);
			if (data == null) {
				CompoundTag tag = new CompoundTag();
				data = CustomData.of(tag);
				stack.set(DataComponents.CUSTOM_DATA, data);
			}
			if (data.copyTag().getBoolean("__fathommod__unbreakable_by_trinket")) {
				CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean("__fathommod__unbreakable_by_trinket", false));
				stack.remove(DataComponents.UNBREAKABLE);
			}
		}
	}
}
