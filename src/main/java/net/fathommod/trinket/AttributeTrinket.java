package net.fathommod.trinket;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface AttributeTrinket extends Trinket {
    AttributeModifier getModifier();
    Holder<Attribute> getAttribute();
}
