package fr.traqueur.crates.api.managers;

import fr.traqueur.crates.api.models.CrateOpening;
import fr.traqueur.crates.api.models.User;

import java.util.UUID;

/**
 * Manager responsible for player data including virtual keys and opening history.
 *
 * <p>This manager handles the lifecycle of user data:</p>
 * <ul>
 *     <li>Loading user data when a player joins</li>
 *     <li>Caching user data in memory for performance</li>
 *     <li>Persisting changes to the database</li>
 *     <li>Unloading user data when a player leaves</li>
 * </ul>
 *
 * <p><b>Usage example:</b></p>
 * <pre>{@code
 * UsersManager usersManager = plugin.getManager(UsersManager.class);
 * User user = usersManager.getUser(player.getUniqueId());
 *
 * // Modify user data
 * user.addKeys("legendary-key", 5);
 *
 * // Changes are automatically persisted
 * }</pre>
 *
 * @see User
 * @see CrateOpening
 */
public non-sealed interface UsersManager extends Manager {

    /**
     * Loads a user's data from the database into cache.
     *
     * <p>This is called automatically when a player joins the server.
     * The operation is asynchronous and loads both keys and opening history.</p>
     *
     * @param uuid the player's UUID
     */
    void loadUser(UUID uuid);

    /**
     * Unloads a user's data from cache and saves any pending changes.
     *
     * <p>This is called automatically when a player leaves the server.</p>
     *
     * @param uuid the player's UUID
     */
    void unloadUser(UUID uuid);

    /**
     * Gets a user from the cache.
     *
     * <p>The user must have been loaded first via {@link #loadUser(UUID)}.
     * Returns null if the user is not in cache.</p>
     *
     * @param uuid the player's UUID
     * @return the cached User object, or null if not loaded
     */
    User getUser(UUID uuid);

    /**
     * Persists a crate opening record to the database.
     *
     * <p>This is called internally when a reward is given to a player.
     * The operation is asynchronous.</p>
     *
     * @param opening the crate opening record to persist
     */
    void persistCrateOpening(CrateOpening opening);
}
