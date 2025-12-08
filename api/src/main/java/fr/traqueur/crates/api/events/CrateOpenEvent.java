package fr.traqueur.crates.api.events;

import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.models.crates.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player opens a crate (after key is consumed, menu is about to open).
 * This event is not cancellable.
 */
public class CrateOpenEvent extends CrateEvent {

    /** The handler list for this event. */
    private static final HandlerList HANDLERS = new HandlerList();
    /** The animation that will be played when the crate is opened. */
    private final Animation animation;

    /**
     * Constructs a CrateOpenEvent with the specified player, crate, and animation.
     *
     * @param player    the player opening the crate
     * @param crate     the crate being opened
     * @param animation the animation that will be played
     */
    public CrateOpenEvent(Player player, Crate crate, Animation animation) {
        super(player, crate);
        this.animation = animation;
    }

    /**
     * Gets the animation that will be played.
     *
     * @return the animation
     */
    public Animation getAnimation() {
        return animation;
    }

    /**
     * Gets the handler list for this event.
     *
     * @return the handler list
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the static handler list for this event.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}