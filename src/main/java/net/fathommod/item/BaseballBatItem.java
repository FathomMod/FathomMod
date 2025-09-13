
package net.fathommod.item;

import net.fathommod.DamageTypedWeapon;
import net.fathommod.item.types.BluntWeaponItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.List;

public class BaseballBatItem extends BluntWeaponItem implements DamageTypedWeapon {
	public BaseballBatItem() {
		super(new Item.Properties());
	}

    @Override
    public float attackSpeed() {
        return 1.2f;
    }

    @Override
    public float getDamage() {
        return 5;
    }

    @Override
    public List<Component> descriptions() {
        return List.of(Component.translatable("tooltip.fathommod.wood_bat.first_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }
}
