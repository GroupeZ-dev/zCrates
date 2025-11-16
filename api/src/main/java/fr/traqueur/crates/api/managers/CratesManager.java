package fr.traqueur.crates.api.managers;

import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.models.crates.Reward;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public non-sealed interface CratesManager extends Manager {

    void openCrate(Player player, Crate crate, Animation animation);

    void startAnimation(Player player, Inventory inventory);

    void stopAllOpening();

    void closeCrate(Player player);

    void ensureInventoriesExist();
}
