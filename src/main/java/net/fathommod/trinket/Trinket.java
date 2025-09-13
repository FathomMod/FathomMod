package net.fathommod.trinket;

import java.util.ArrayList;

public interface Trinket {
    default ArrayList<Trinket> incompatibleTrinkets() { return new ArrayList<>(); }
}