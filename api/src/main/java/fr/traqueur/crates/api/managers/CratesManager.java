package fr.traqueur.crates.api.managers;

import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.models.crates.Reward;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public non-sealed interface CratesManager extends Manager {

    void openCrate(Player player, Crate crate, Reward reward, Animation animation, Inventory inventory);

    void stopAllOpening();
}
