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

    private static final HandlerList HANDLERS = new HandlerList();
    private final Animation animation;

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

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}