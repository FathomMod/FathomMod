package net.fathommod.item.types;

import net.fathommod.ClientDevUtils;
import net.fathommod.DamageTypedWeapon;
import net.fathommod.DevUtils;
import net.fathommod.Trinkets;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
public abstract class FMWeapon extends Item implements DamageTypedWeapon {
    public abstract float getDamage();

    public FMWeapon(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    public abstract List<Component> descriptions();

    public List<Component> passiveAbilities() {
        return List.of();
    }

    public List<Component> activeAbilities() {
        return List.of();
    }

    public @NotNull Component aoeType() {
        return null;
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        components.addAll(descriptions());
        super.appendHoverText(stack, context, components, flag);
        if (aoeType() != null)
            components.add(aoeType());
        components.addAll(passiveAbilities());
        components.addAll(activeAbilities());
        components.add(Component.empty());
        double calculatedDamage = ClientDevUtils.calculateAttackDamage(getDamage());
        if (this instanceof FMMeleeWeapon)
            components.add(Component.translatable("tooltip.fathommod.generic_damage", DevUtils.formatNumberAsProperString(calculatedDamage + (ClientDevUtils.hasTrinket(Trinkets.RING_OF_POWER) ? 2 : 0))).withColor(DevUtils.DAMAGE_TOOLTIPS_HEX).append(getDamageClass().createComponent()));
        else
            components.add(Component.translatable("tooltip.fathommod.generic_damage", DevUtils.formatNumberAsProperString(getDamage() + (ClientDevUtils.hasTrinket(Trinkets.RING_OF_POWER) ? 2 : 0))).withColor(DevUtils.DAMAGE_TOOLTIPS_HEX).append(getDamageClass().createComponent()));
    }
}