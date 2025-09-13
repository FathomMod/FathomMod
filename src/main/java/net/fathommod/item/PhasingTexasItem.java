
package net.fathommod.item;

import net.fathommod.DamageClasses;
import net.fathommod.DamageTypedWeapon;
import net.fathommod.DevUtils;
import net.fathommod.item.types.FMMeleeWeapon;
import net.fathommod.procedures.PhasingTexasLivingEntityIsHitWithToolProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PhasingTexasItem extends FMMeleeWeapon implements DamageTypedWeapon {
	public PhasingTexasItem() {
		super(new Item.Properties().attributes(SwordItem.createAttributes(DevUtils.EMPTY_TIER, 350f, -2.4f)).fireResistant());
	}

    @Override
    protected boolean canSweep() {
        return true;
    }

    @Override
    public float getDamage() {
        return 351;
    }

    @Override
    public float attackSpeed() {
        return 1.6f;
    }

    @Override
    public List<Component> descriptions() {
        return List.of();
    }

	@Override
	public boolean hurtEnemy(@NotNull ItemStack itemstack, @NotNull LivingEntity entity, @NotNull LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		PhasingTexasLivingEntityIsHitWithToolProcedure.execute(sourceentity);
		return retval;
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
