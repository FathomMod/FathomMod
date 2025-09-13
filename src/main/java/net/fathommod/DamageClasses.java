package net.fathommod;

import net.minecraft.network.chat.Component;

public enum DamageClasses {
    MELEE(0xff0000),
    ASSASSIN(0x9d00ff),
    RANGED(0x8cff00),
    SUMMON(0x00f2ff);

    private final int color;

    DamageClasses(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    public String getComponent() {
        return switch (this) {
            case MELEE
                    -> "fathommod.damage_classes.melee";
            case ASSASSIN
                    -> "fathommod.damage_classes.assassin";
            case RANGED
                    -> "fathommod.damage_classes.ranged";
            case SUMMON
                    -> "fathommod.damage_classes.summon";
        };
    }

    public Component createComponent() {
        return Component.translatable(getComponent()).withColor(getColor());
    }
}
