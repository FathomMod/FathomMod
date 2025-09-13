package net.fathommod.item.types;

import net.fathommod.ClientDevUtils;
import net.fathommod.DamageClasses;
import net.fathommod.DevUtils;
import net.fathommod.item.SweetSpotItem;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DataFlowIssue")
public abstract class FMMeleeWeapon extends FMWeapon implements SweetSpotItem {
    public FMMeleeWeapon(Properties properties) {
        super(properties);
        ArrayList<ItemAttributeModifiers.Entry> modifiers = new ArrayList<>(this.components.get(DataComponents.ATTRIBUTE_MODIFIERS).modifiers());
        modifiers.add(new ItemAttributeModifiers.Entry(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, this.getDamage() - 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
        modifiers.add(new ItemAttributeModifiers.Entry(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -(4 - this.attackSpeed()), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
        this.components = DataComponentMap.builder().addAll(this.components).set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(
                modifiers, false
        )).build();
    }

    protected boolean canSweep() {
        return false;
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility itemAbility) {
        if (itemAbility != ItemAbilities.SWORD_SWEEP)
            return super.canPerformAction(stack, itemAbility);
        return canSweep();
    }

    public abstract float attackSpeed();

    @Override
    public DamageClasses getDamageClass() {
        return DamageClasses.MELEE;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, components, flag);
        double calculatedAttackSpd = ClientDevUtils.calculateAttackSpeed(attackSpeed());
        components.add(Component.translatable("tooltip.fathommod.generic_attack_speed", calculatedAttackSpd < 20 ? DevUtils.formatNumberAsProperString(1 / calculatedAttackSpd) + "s" : "Instant").withColor(DevUtils.DAMAGE_TOOLTIPS_HEX));
    }
}