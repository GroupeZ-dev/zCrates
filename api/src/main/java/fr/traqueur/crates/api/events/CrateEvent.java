package fr.traqueur.crates.api.events;

import fr.traqueur.crates.api.models.crates.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

/**
 * Base class for all crate-related events.
 */
public abstract class CrateEvent extends PlayerEvent {

    /** The crate involved in this event. */
    protected final Crate crate;

    /**
     * Constructs a CrateEvent with the specified player and crate.
     *
     * @param player the player involved in the event
     * @param crate  the crate involved in the event
     */
    public CrateEvent(Player player, Crate crate) {
        super(player);
        this.crate = crate;
    }

    /**
     * Constructs a CrateEvent with the specified player, crate, and async flag.
     *
     * @param player the player involved in the event
     * @param crate  the crate involved in the event
     * @param async  whether the event is asynchronous
     */
    public CrateEvent(Player player, Crate crate, boolean async) {
        super(player, async);
        this.crate = crate;
    }

    /**
     * Gets the crate involved in this event.
     *
     * @return the crate
     */
    public Crate getCrate() {
        return crate;
    }
}