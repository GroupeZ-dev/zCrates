package fr.traqueur.crates.api.models.placedcrates;

import org.bukkit.Location;

/**
 * Represents a display for a crate in the game world.
 *
 * @param <T> The type of element that the crate display represents.
 */
public interface CrateDisplay<T> {

    /** Spawns the crate display in the game world. */
    void spawn();

    /** Removes the crate display from the game world. */
    void remove();

    /**
     * Checks if the given element matches the crate display.
     *
     * @param element The element to check.
     * @return true if the element matches, false otherwise.
     */
    boolean matches(T element);

    /**
     * Gets the location of the crate display in the game world.
     *
     * @return The location of the crate display.
     */
    Location getLocation();
}