package net.fathommod.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public abstract class TrinketEvent extends Event {
    public final Player player;
    public final ItemStack stack;
    public TrinketEvent(Player player, ItemStack stack) {
        this.player = player;
        this.stack = stack;
    }

    public static class TrinketAddedEvent extends TrinketEvent {
        public boolean isFromJoining = false;
        public TrinketAddedEvent(Player player, ItemStack stack) {
            super(player, stack);
        }
    }

    public static class TrinketRemovedEvent extends TrinketEvent {
        public TrinketRemovedEvent(Player player, ItemStack stack) {
            super(player, stack);
        }
    }
}