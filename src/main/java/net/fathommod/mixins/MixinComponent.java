package net.fathommod.mixins;

import net.fathommod.DamageTypedWeapon;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class MixinComponent { // Adds the damage type to an item's tooltip
    @Inject(method = "addModifierTooltip", at = @At("HEAD"), cancellable = true)
    private void onTranslatable(Consumer<Component> consumer, Player player, Holder<Attribute> attribute, AttributeModifier modifier, CallbackInfo ci) {
        ItemStack instance = (ItemStack) (Object) this;

        if (instance.getItem() instanceof DamageTypedWeapon weapon) {
            ci.cancel();
//            consumer.accept(Component.translatableWithFallback(weapon.getDamageClass().getComponent(), ""));
            double d0 = modifier.amount();
            boolean flag = false;
            if (player != null) {
                if (modifier.is(Item.BASE_ATTACK_DAMAGE_ID)) {
                    d0 += player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                    flag = true;
                } else if (modifier.is(Item.BASE_ATTACK_SPEED_ID)) {
                    d0 += player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                    flag = true;
                }
            }

            double d1;
            if (modifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && modifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                if (attribute.is(Attributes.KNOCKBACK_RESISTANCE)) {
                    d1 = d0 * 10.0;
                } else {
                    d1 = d0;
                }
            } else {
                d1 = d0 * 100.0;
            }

            if (flag) {
                if (modifier.is(Item.BASE_ATTACK_DAMAGE_ID))
                    consumer.accept((CommonComponents.space().append(Component.translatable("attribute.modifier.equals." + modifier.operation().id(), new Object[]{ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(((Attribute)attribute.value()).getDescriptionId())})).withStyle(ChatFormatting.DARK_GREEN)).append(Component.literal(" ")).append(Component.translatable(weapon.getDamageClass().getComponent()).withColor(weapon.getDamageClass().getColor())));
                else
                    consumer.accept(CommonComponents.space().append(Component.translatable("attribute.modifier.equals." + modifier.operation().id(), new Object[]{ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(((Attribute)attribute.value()).getDescriptionId())})).withStyle(ChatFormatting.DARK_GREEN));
            } else if (d0 > 0.0) {
                consumer.accept(Component.translatable("attribute.modifier.plus." + modifier.operation().id(), new Object[]{ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(((Attribute)attribute.value()).getDescriptionId())}).withStyle(((Attribute)attribute.value()).getStyle(true)));
            } else if (d0 < 0.0) {
                consumer.accept(Component.translatable("attribute.modifier.take." + modifier.operation().id(), new Object[]{ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(-d1), Component.translatable(((Attribute)attribute.value()).getDescriptionId())}).withStyle(((Attribute)attribute.value()).getStyle(false)));
            }
        }
    }
}
