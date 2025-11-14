package fr.traqueur.crates.models.wrappers;

import fr.traqueur.crates.api.models.Crate;
import fr.traqueur.crates.api.models.Wrapper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Wrapper for Crate that exposes only safe information for animations.
 * This prevents animations from modifying crate data.
 */
public class CrateWrapper extends Wrapper<Crate> {

    public CrateWrapper(Crate delegate) {
        super(delegate);
    }

    public ItemStack getReward() {
        return ItemStack.of(Material.STONE, 1);
    }

    public String id() {
        return delegate.id();
    }

    public int size() {
        return delegate.size();
    }

}