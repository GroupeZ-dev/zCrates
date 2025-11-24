package fr.traqueur.crates.api.models.placedcrates;

import org.bukkit.Location;

import java.util.List;

/**
 * Factory interface for creating crate displays.
 *
 * @param <T> The type of element that the crate display represents.
 */
public interface CrateDisplayFactory<T> {

    /**
     * Creates a crate display at the specified location with the given value and yaw.
     *
     * @param location the location to create the display
     * @param value    the value representing the display content
     * @param yaw      the yaw orientation of the display
     * @return the created crate display
     */
    CrateDisplay<T> create(Location location, String value, float yaw);

    /**
     * Validates if the given value is valid for this display type.
     *
     * @param value the value to validate
     * @return true if valid, false otherwise
     */
    boolean isValidValue(String value);

    /**
     * Returns a list of suggested values for tab completion.
     *
     * @return list of suggested values
     */
    List<String> getSuggestions();
}