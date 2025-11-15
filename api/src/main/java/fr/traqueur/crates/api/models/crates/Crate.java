package fr.traqueur.crates.api.models.crates;

import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.settings.models.ItemStackWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

public interface Crate extends InventoryHolder {

    String id();

    Animation animation();

    String title();

    int size();

    Inventory inventory(Player player);

    List<Reward> rewards();

    ItemStackWrapper randomDisplay();

    Reward generateReward();
}
