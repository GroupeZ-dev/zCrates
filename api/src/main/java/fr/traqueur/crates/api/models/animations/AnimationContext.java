package fr.traqueur.crates.api.models.animations;

import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.Wrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Context information for crate opening animations.
 *
 * @param player   The player involved in the animation.
 * @param inventory The inventory associated with the animation.
 * @param crate    The crate being opened in the animation.
 */
public record AnimationContext(Wrapper<Player> player, Wrapper<Inventory> inventory, Wrapper<Crate> crate) { }
