package fr.traqueur.crates.api.models.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface ItemsProvider {

    ItemStack item(Player player, String itemId);
}
