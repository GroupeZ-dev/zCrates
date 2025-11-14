package fr.traqueur.crates.api.managers;

import fr.traqueur.crates.api.models.Crate;
import fr.traqueur.crates.api.models.animations.Animation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public non-sealed interface AnimationsManager extends Manager {
    void startAnimation(Player player, Crate crate, Animation animation, Inventory inventory);
}
