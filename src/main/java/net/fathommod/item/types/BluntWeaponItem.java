package net.fathommod.item.types;

import net.fathommod.DevUtils;
import net.fathommod.item.SweetSpotItem;
import net.fathommod.item.SweetSpotRange;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class BluntWeaponItem extends FMMeleeWeapon implements SweetSpotItem {
    public BluntWeaponItem(Properties properties) {
        super(properties);
    }

    @Override
    public List<Component> passiveAbilities() {
        return List.of(Component.translatable("tooltip.fathommod.blunt_weapon_armor_piercing_passive").withColor(DevUtils.INFO_TOOLTIPS_HEX));
    }

    @Override
    public SweetSpotRange getSweetSpotRange() {
        return new SweetSpotRange(.7, .85, 1.2);
    }
}
