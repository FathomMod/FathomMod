
package net.fathommod.item;

import net.fathommod.DamageClasses;
import net.fathommod.DamageTypedWeapon;
import net.fathommod.item.types.BluntWeaponItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.List;

@SuppressWarnings("unused")
public class MetalBatItem extends BluntWeaponItem implements DamageTypedWeapon {
	public MetalBatItem() {
		super(new Item.Properties());
	}

    @Override
    public float attackSpeed() {
        return 1;
    }

    @Override
    public float getDamage() {
        return 12;
    }

    @Override
    public List<Component> descriptions() {
        return List.of(Component.translatable("tooltip.fathommod.metal_bat.first_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

	@Override
	public DamageClasses getDamageClass() {
		return DamageClasses.MELEE;
	}
}
