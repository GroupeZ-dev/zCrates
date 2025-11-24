package fr.traqueur.crates.api.events;

import fr.traqueur.crates.api.models.crates.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

/**
 * Base class for all crate-related events.
 */
public abstract class CrateEvent extends PlayerEvent {
    
    protected final Crate crate;

    public CrateEvent(Player player, Crate crate) {
        super(player);
        this.crate = crate;
    }

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