package fr.traqueur.crates.api.models.crates;

import fr.traqueur.structura.annotations.Polymorphic;
import fr.traqueur.structura.api.Loadable;
import org.bukkit.entity.Player;

/**
 * Represents a key required to open a crate.
 *
 * <p>Keys use polymorphic deserialization based on the "type" field in YAML.
 * Two key types are available:</p>
 * <ul>
 *     <li>{@code VIRTUAL} - Stored in database, no physical item</li>
 *     <li>{@code PHYSIC} - Physical item in player's inventory</li>
 * </ul>
 *
 * <p><b>Example YAML configurations:</b></p>
 * <pre>{@code
 * # Virtual key (database-stored)
 * key:
 *   type: VIRTUAL
 *   name: "legendary-key"
 *
 * # Physical key (item)
 * key:
 *   type: PHYSIC
 *   name: "legendary-key"
 *   item:
 *     material: TRIPWIRE_HOOK
 *     name: "<gold>Legendary Key"
 *     lore:
 *       - "<gray>Right-click on a crate"
 *     glow: true
 * }</pre>
 *
 * @see Crate
 */
@Polymorphic
public interface Key extends Loadable {

    /**
     * Gets the unique name of this key.
     *
     * <p>For virtual keys, this is the database key identifier.
     * For physical keys, this is used for matching.</p>
     *
     * @return the key name
     */
    String name();

    /**
     * Checks if a player has this key.
     *
     * <p>For virtual keys, checks the database.
     * For physical keys, searches the player's inventory.</p>
     *
     * @param player the player to check
     * @return true if the player has at least one key
     */
    boolean has(Player player);

    /**
     * Removes one key from the player.
     *
     * <p>For virtual keys, decrements the database count.
     * For physical keys, removes one matching item from inventory.</p>
     *
     * @param player the player to remove the key from
     */
    void remove(Player player);

    /**
     * Gives one key to the player.
     *
     * <p>For virtual keys, increments the database count.
     * For physical keys, adds the key item to inventory.</p>
     *
     * @param player the player to give the key to
     */
    void give(Player player);

    /**
     * Counts how many keys the player has.
     *
     * <p>For virtual keys, retrieves the count from the database.
     * For physical keys, counts matching items in inventory.</p>
     *
     * @param player the player to count keys for
     * @return the number of keys the player has
     */
    int count(Player player);

}
