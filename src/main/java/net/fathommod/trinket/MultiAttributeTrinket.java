package net.fathommod.trinket;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;

public interface MultiAttributeTrinket extends Trinket {
    List<Holder<Attribute>> getAttributes();
    List<AttributeModifier> getModifiers();
}
