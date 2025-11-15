package fr.traqueur.crates.models.wrappers;

import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.Wrapper;
import fr.traqueur.crates.api.models.crates.Reward;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Wrapper for Crate that exposes only safe information for animations.
 * This prevents animations from modifying crate data.
 */
public class CrateWrapper extends Wrapper<Crate> {

    private final Player player;
    private final Reward reward;

    public CrateWrapper(Crate delegate, Player player, Reward reward) {
        super(delegate);
        this.player = player;
        this.reward = reward;
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

    public int size() {
        return delegate.size();
    }

}