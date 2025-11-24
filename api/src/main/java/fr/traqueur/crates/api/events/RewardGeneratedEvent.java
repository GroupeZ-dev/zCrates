package fr.traqueur.crates.api.events;

import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.crates.Reward;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a reward is generated for a player (after animation starts).
 * This can be used to track reward distribution or modify logging.
 */
public class RewardGeneratedEvent extends CrateEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Reward reward;
    private final boolean isReroll;

    public RewardGeneratedEvent(Player player, Crate crate, Reward reward, boolean isReroll) {
        super(player, crate);
        this.reward = reward;
        this.isReroll = isReroll;
    }

    /**
     * Gets the reward that was generated.
     *
     * @return the reward
     */
    public Reward getReward() {
        return reward;
    }

    /**
     * Whether this reward was generated as a result of a reroll.
     *
     * @return true if this is a reroll
     */
    public boolean isReroll() {
        return isReroll;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}