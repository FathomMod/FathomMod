package net.fathommod.item;

import net.fathommod.DamageClasses;
import net.fathommod.DamageTypedWeapon;
import net.fathommod.item.types.FMMeleeWeapon;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class TedClawsItem extends FMMeleeWeapon implements DamageTypedWeapon {
    public TedClawsItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public float getDamage() {
        return 5;
    }

    @Override
    public List<Component> descriptions() {
        return List.of(Component.translatable("tooltip.fathommod.ted_claws.first_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
    }

    @Override
    public List<Component> activeAbilities() {
        return List.of(Component.translatable("tooltip.fathommod.ted_claws.second_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
    }

    @Override
    public float attackSpeed() {
        return 1;
    }

    @Override
    public DamageClasses getDamageClass() {
        return DamageClasses.MELEE;
    }

    @Override
    public SweetSpotRange getSweetSpotRange() {
        return null;
    }
}
