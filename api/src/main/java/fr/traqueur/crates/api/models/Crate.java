package fr.traqueur.crates.api.models;

import fr.traqueur.crates.api.models.animations.Animation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public interface Crate extends InventoryHolder {

    String id();

    Animation animation();

    String title();

    int size();

    Inventory inventory(Player player);

}
