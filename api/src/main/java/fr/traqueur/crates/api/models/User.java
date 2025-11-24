package fr.traqueur.crates.api.models;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a player's crate-related data including virtual keys and opening history.
 *
 * <p>User data is persisted in the database and cached in memory for performance.
 * The {@link fr.traqueur.crates.api.managers.UsersManager} handles loading and saving.</p>
 *
 * <p><b>Usage example:</b></p>
 * <pre>{@code
 * UsersManager usersManager = plugin.getManager(UsersManager.class);
 * User user = usersManager.getUser(player.getUniqueId());
 *
 * // Check and use keys
 * if (user.hasKey("legendary-key")) {
 *     user.removeKeys("legendary-key", 1);
 * }
 *
 * // Give keys
 * user.addKeys("common-key", 5);
 * }</pre>
 *
 * @see fr.traqueur.crates.api.managers.UsersManager
 * @see CrateOpening
 */
public interface User {

    /**
     * Gets the UUID of this user.
     *
     * @return the player's unique identifier
     */
    UUID uuid();

    /**
     * Gets the number of keys a user has for a specific key type.
     *
     * @param keyName the key name
     * @return the number of keys (0 if none)
     */
    int getKeyCount(String keyName);

    /**
     * Adds keys to this user's balance.
     *
     * @param keyName the key name
     * @param amount the amount to add (must be positive)
     */
    void addKeys(String keyName, int amount);

    /**
     * Removes keys from this user's balance.
     *
     * <p>The balance will not go below 0.</p>
     *
     * @param keyName the key name
     * @param amount the amount to remove (must be positive)
     */
    void removeKeys(String keyName, int amount);

    /**
     * Checks if this user has at least one key of the specified type.
     *
     * @param keyName the key name
     * @return true if the user has at least one key
     */
    boolean hasKey(String keyName);

    /**
     * Gets all keys and their counts for this user.
     *
     * @return an unmodifiable map of key names to counts
     */
    Map<String, Integer> getAllKeys();

    /**
     * Gets all crate openings for this user.
     *
     * <p>Used by algorithms that need player history (e.g., pity systems).</p>
     *
     * @return the list of crate openings, most recent first
     */
    List<CrateOpening> getCrateOpenings();

    /**
     * Adds a crate opening to this user's history.
     *
     * <p>Called automatically when a reward is given. The opening is persisted
     * to the database asynchronously.</p>
     *
     * @param crateId the crate ID
     * @param rewardId the reward ID
     * @return the created CrateOpening record
     */
    CrateOpening addCrateOpening(String crateId, String rewardId);

}
