package net.fathommod.item;

import net.fathommod.FathommodMod;
import net.fathommod.Trinkets;
import net.fathommod.init.FathommodModAttributes;
import net.fathommod.init.FathommodModKeyMappings;
import net.fathommod.trinket.MultiAttributeTrinket;
import net.fathommod.trinket.OneTimeEffectTrinket;
import net.fathommod.trinket.TickTrinket;
import net.fathommod.trinket.Trinket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "DataFlowIssue"})
public class ThunderTreadsItem extends net.minecraft.world.item.Item implements MultiAttributeTrinket, TickTrinket, OneTimeEffectTrinket {
    public ThunderTreadsItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        components.add(Component.translatable("tooltip.fathommod.zeus_boots.first_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.GRAY)));
        components.add(Component.translatable("tooltip.fathommod.zeus_boots.second_line").setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0x58a7bf)));
        try {
            components.add(Component.translatable("tooltip.fathommod.generic_trinket.keybind", FathommodModKeyMappings.TRINKETERY_OPEN.getKey().getDisplayName()).setStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)));
        } catch (RuntimeException ignored) {}

        super.appendHoverText(stack, context, components, tooltipFlag);
    }

    @Override
    public List<Holder<Attribute>> getAttributes() {
        return List.of(Attributes.STEP_HEIGHT, NeoForgeMod.SWIM_SPEED, FathommodModAttributes.STEP_DOWN_HEIGHT.getDelegate());
    }

    @Override
    public List<AttributeModifier> getModifiers() {
        return List.of(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "boot_trinket_step_height"), .5, AttributeModifier.Operation.ADD_VALUE), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "water_crack"), 4, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "hermes_step_down_height"), 1, AttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public void applyEffect(Player player, ItemStack stack) {}

    @Override
    public void removeEffect(Player player, ItemStack stack) {
        player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "crack"));
    }

    @Override
    public void tick(Player entity) { // this is done because depth strider makes movement speed also affect swim speed for some reason??????
        if (!entity.isInWater())
            entity.getAttribute(Attributes.MOVEMENT_SPEED).addOrUpdateTransientModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "crack"), 2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        else
            entity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "crack"));
    }



    @Override
    public ArrayList<Trinket> incompatibleTrinkets() {
        return new ArrayList<>(List.of((Trinket) Trinkets.CRACK, (Trinket) Trinkets.CRACK_BUT_FOR_WATER));
    }
}
