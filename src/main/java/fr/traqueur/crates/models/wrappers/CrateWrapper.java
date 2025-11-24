package fr.traqueur.crates.models.wrappers;

import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.Wrapper;
import fr.traqueur.crates.api.models.crates.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.IntSupplier;

/**
 * Wrapper for Crate that exposes only safe information for animations.
 * This prevents animations from modifying crate data.
 */
public class CrateWrapper extends Wrapper<Crate> {

    private final Player player;
    private final Reward reward;
    private final IntSupplier rerollsRemainingSupplier;

    public CrateWrapper(Crate delegate, Player player, Reward reward, IntSupplier rerollsRemainingSupplier) {
        super(delegate);
        this.player = player;
        this.reward = reward;
        this.rerollsRemainingSupplier = rerollsRemainingSupplier;
    }

    public ItemStack getReward() {
        return reward.displayItem().build(player);
    }

    public String displayName() {
        return delegate.displayName();
    }

    public String id() {
        return delegate.id();
    }

    /**
     * Returns the number of rerolls remaining for this crate opening.
     * @return number of rerolls remaining
     */
    public int rerollsRemaining() {
        return rerollsRemainingSupplier.getAsInt();
    }

    /**
     * Returns whether the player has rerolls available.
     * @return true if rerolls are available
     */
    public boolean hasRerolls() {
        return rerollsRemainingSupplier.getAsInt() > 0;
    }

}