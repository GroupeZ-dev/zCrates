package fr.traqueur.crates.api.models.placedcrates;

import org.bukkit.Location;

import java.util.List;

public interface CrateDisplayFactory<T> {

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