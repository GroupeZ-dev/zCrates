package fr.traqueur.crates.api.events;

import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.crates.Reward;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player attempts to reroll their reward.
 * This event is cancellable - if cancelled, the reroll will not occur.
 */
public class CrateRerollEvent extends CrateEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;
    private final Reward currentReward;
    private final int rerollsRemaining;

    public CrateRerollEvent(Player player, Crate crate, Reward currentReward, int rerollsRemaining) {
        super(player, crate);
        this.currentReward = currentReward;
        this.rerollsRemaining = rerollsRemaining;
    }

    /**
     * Gets the current reward that will be replaced if reroll succeeds.
     *
     * @return the current reward
     */
    public Reward getCurrentReward() {
        return currentReward;
    }

    /**
     * Gets the number of rerolls remaining after this reroll (if it succeeds).
     *
     * @return rerolls remaining after this one
     */
    public int getRerollsRemaining() {
        return rerollsRemaining;
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

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}