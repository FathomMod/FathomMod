package net.fathommod.mixins.sweetspots;

import net.fathommod.item.SweetSpotItem;
import net.fathommod.item.SweetSpotRange;
import net.minecraft.world.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("unused")
@Mixin(SwordItem.class)
public class SwordMixin implements SweetSpotItem { // Adds sweet spot
    @Unique
    public SweetSpotRange getSweetSpotRange() {
        return new SweetSpotRange(.55, .75, 1.2);
    }
}
