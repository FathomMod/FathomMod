package net.fathommod.item;

@SuppressWarnings("unused")
public class SweetSpotRange {
    public final double startPercent;
    public final double endPercent;
    public final float damageMultiplier;

    public SweetSpotRange(double startPercent, double endPercent) {
        this(startPercent, endPercent, 1.3f);
    }

    public SweetSpotRange(double startPercent, double endPercent, float damage) {
        this.startPercent = startPercent;
        this.endPercent = endPercent;
        this.damageMultiplier = damage;
    }

    public SweetSpotRange(double startPercent, double endPercent, double damage) {
        this(startPercent, endPercent, (float) damage);
    }

    public boolean isInRange(double value, double max) {
        double lowerBound = startPercent * max;
        double upperBound = endPercent * max;
        return value >= lowerBound && value <= upperBound;
    }
}
