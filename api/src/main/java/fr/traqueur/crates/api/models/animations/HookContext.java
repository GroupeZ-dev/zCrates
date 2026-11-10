package fr.traqueur.crates.api.models.animations;

import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.crates.Reward;
import org.bukkit.entity.Player;

/**
 * Java-side context handed to hook action factories registered in
 * {@link fr.traqueur.crates.api.registries.HookActionsRegistry}.
 *
 * <p>Unlike {@link AnimationContext}, whose wrappers are what scripts see, this context
 * gives trusted Java code the raw objects of the running animation. It never reaches
 * JavaScript: scripts only get the object the factory returns.</p>
 *
 * @param player   the player opening the crate
 * @param crate    the crate being opened
 * @param reward   the reward already drawn for this opening
 * @param complete ends the running animation as if its phases had all elapsed: the current
 *                 phase and the animation complete, then the opening goes on as usual (for an
 *                 animation without inventory, the reward is given). No-op once the animation
 *                 is over.
 */
public record HookContext(Player player, Crate crate, Reward reward, Runnable complete) { }
