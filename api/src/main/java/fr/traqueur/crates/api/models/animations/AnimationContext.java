package fr.traqueur.crates.api.models.animations;

import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.Wrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Context information for crate opening animations.
 *
 * @param player    The player involved in the animation.
 * @param inventory The inventory associated with the animation, or {@code null} for an
 *                  animation that runs without inventory ({@link Animation#usesInventory()}).
 * @param crate     The crate being opened in the animation.
 * @param hooks     The Java-side context given to hook action factories; never exposed to scripts.
 */
public record AnimationContext(Wrapper<Player> player, Wrapper<Inventory> inventory, Wrapper<Crate> crate, HookContext hooks) { }
