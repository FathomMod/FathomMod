package net.fathommod;

public interface DamageTypedWeapon {
    DamageClasses getDamageClass();

    default int getIFrames() {
        return 0;
    }
}
