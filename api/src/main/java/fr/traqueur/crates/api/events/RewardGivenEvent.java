package fr.traqueur.crates.api.events;

import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.crates.Reward;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a reward is given to a player (when they close the inventory).
 * This event fires after the reward has been given.
 */
public class RewardGivenEvent extends CrateEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Reward reward;

    public RewardGivenEvent(Player player, Crate crate, Reward reward) {
        super(player, crate);
        this.reward = reward;
    }

    /**
     * Gets the reward that was given to the player.
     *
     * @return the reward
     */
    public Reward getReward() {
        return reward;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}