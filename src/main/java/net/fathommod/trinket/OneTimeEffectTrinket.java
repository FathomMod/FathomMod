package net.fathommod.trinket;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface OneTimeEffectTrinket extends Trinket {
    void applyEffect(Player player, ItemStack stack);
    void removeEffect(Player player, ItemStack stack);
}
