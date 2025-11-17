package fr.traqueur.crates.api.models.placedcrates;

import org.bukkit.Location;

public interface CrateDisplay<T> {

    void spawn();

    void remove();

    boolean matches(T element);

    Location getLocation();
}