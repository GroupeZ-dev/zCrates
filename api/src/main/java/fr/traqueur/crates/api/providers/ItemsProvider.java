package fr.traqueur.crates.api.providers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Functional interface for providing ItemStack instances based on a player and item ID.
 */
@FunctionalInterface
public interface ItemsProvider {

    /**
     * Retrieves an ItemStack for the given player and item ID.
     *
     * @param player the player for whom the item is being retrieved
     * @param itemId the identifier of the item
     * @return the corresponding ItemStack
     */
    ItemStack item(Player player, String itemId);
}
