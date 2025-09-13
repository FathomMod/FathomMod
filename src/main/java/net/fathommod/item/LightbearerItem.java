
package net.fathommod.item;

import net.fathommod.DamageClasses;
import net.fathommod.DamageTypedWeapon;
import net.fathommod.item.types.FMMeleeWeapon;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LightbearerItem extends FMMeleeWeapon implements DamageTypedWeapon {
	public LightbearerItem() {
		super(new Item.Properties().fireResistant());
	}

    @Override
    public List<Component> descriptions() {
        return List.of();
    }

    @Override
    public float getDamage() {
        return 12978;
    }

    @Override
    public float attackSpeed() {
        return .5f;
    }

    @Override
    protected boolean canSweep() {
        return true;
    }

    @Override
	public boolean hasCraftingRemainingItem(@NotNull ItemStack stack) {
		return true;
	}

	@Override
	public @NotNull ItemStack getCraftingRemainingItem(@NotNull ItemStack itemstack) {
		return new ItemStack(this);
	}

	@Override
	public DamageClasses getDamageClass() {
		return DamageClasses.MELEE;
	}

    @Override
    public SweetSpotRange getSweetSpotRange() {
        return new SweetSpotRange(.55, .75, 1.2);
    }
}
