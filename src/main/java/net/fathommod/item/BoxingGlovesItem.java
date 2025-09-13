
package net.fathommod.item;

import net.fathommod.DamageClasses;
import net.fathommod.DamageTypedWeapon;
import net.fathommod.FathommodMod;
import net.fathommod.TwoHandedItem;
import net.fathommod.item.types.FMMeleeWeapon;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

public class BoxingGlovesItem extends FMMeleeWeapon implements DamageTypedWeapon, TwoHandedItem {
	public BoxingGlovesItem() {
		super(new Item.Properties().attributes(new ItemAttributeModifiers(
                List.of(new ItemAttributeModifiers.Entry(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "boxing_gloves_modifier"), -1.5, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)), false
        )));
	}

    @Override
    public float attackSpeed() {
        return 69;
    }

    @Override
    public float getDamage() {
        return 4;
    }

    @Override
    public List<Component> descriptions() {
        return List.of(Component.translatable("tooltip.fathommod.boxing_gloves.first_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public List<Component> passiveAbilities() {
        return List.of(Component.translatable("tooltip.fathommod.boxing_gloves.second_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
    }

    @Override
	public DamageClasses getDamageClass() {
		return DamageClasses.ASSASSIN;
	}

	@Override
	public int getIFrames() {
		return 4;
	}

	@Override
	public boolean shouldDisplayItemInOffhand() {
		return true;
	}

    @Override
    public SweetSpotRange getSweetSpotRange() {
        return null;
    }
}
