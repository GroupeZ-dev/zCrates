package fr.traqueur.crates.api.events;

import fr.traqueur.crates.api.models.crates.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called before the preview menu of a crate is shown to a player.
 * This event is cancellable - if cancelled, the preview menu is not opened,
 * which lets another plugin show its own preview instead.
 */
public class CratePreviewEvent extends CrateEvent implements Cancellable {

    /** The handler list for this event. */
    private static final HandlerList HANDLERS = new HandlerList();
    /** Indicates whether the event has been cancelled. */
    private boolean cancelled = false;

    /**
     * Constructs a CratePreviewEvent with the specified player and crate.
     *
     * @param player the player about to preview the crate
     * @param crate  the crate being previewed
     */
    public CratePreviewEvent(Player player, Crate crate) {
        super(player, crate);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

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
