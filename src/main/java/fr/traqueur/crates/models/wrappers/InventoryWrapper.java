package fr.traqueur.crates.models.wrappers;

import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.Wrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Wrapper for Inventory that exposes only safe methods for animations.
 * This controls what animations can do with the crate inventory.
 */
public class InventoryWrapper extends Wrapper<Inventory> {

    private final CratesPlugin plugin;
    private final Player player;
    private final Crate crate;

    public InventoryWrapper(CratesPlugin plugin, Player player, Crate crate, Inventory handle) {
        super(handle);
        this.plugin = plugin;
        this.player = player;
        this.crate = crate;
    }

    public void close(int delayTicks) {
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> player.closeInventory(), delayTicks);
    }

    public void closeImmediately() {
        player.closeInventory();
    }

    /**
     * Clears the entire inventory.
     */
    public void clear() {
        delegate.clear();
    }

    /**
     * Clears a specific slot.
     *
     * @param slot the slot index
     */
    public void clear(int slot) {
        if (slot >= 0 && slot < delegate.getSize()) {
            delegate.setItem(slot, null);
        }
    }

    /**
     * Sets an item at a specific slot.
     *
     * @param slot the slot index
     * @param item the item to set
     */
    public void setItem(int slot, ItemStack item) {
        if (slot >= 0 && slot < delegate.getSize()) {
            delegate.setItem(slot, item);
        }
    }

    /**
     * Sets a random item at the specified slot.
     * Implementation should be provided by the plugin.
     *
     * @param slot the slot index
     */
    public void setRandomItem(int slot) {
        if (slot >= 0 && slot < delegate.getSize()) {
            ItemStack randomItem = crate.randomDisplay().build(player).clone();
            delegate.setItem(slot, randomItem);
        }
    }

    /**
     * Rotates items through the specified slots (shifts them to the right).
     *
     * @param slots the slot indices to rotate
     */
    public void rotateItems(int[] slots) {
        if (slots == null || slots.length < 2) {
            return;
        }

        // Save the last item
        ItemStack lastItem = delegate.getItem(slots[slots.length - 1]);

        // Shift all items to the right
        for (int i = slots.length - 1; i > 0; i--) {
            ItemStack item = delegate.getItem(slots[i - 1]);
            delegate.setItem(slots[i], item);
        }

        // Put the last item at the first position
        delegate.setItem(slots[0], lastItem);
    }

    /**
     * Sets the winning item at the specified slot.
     *
     * @param slot   the slot index
     * @param reward the reward object
     */
    public void setWinningItem(int slot, ItemStack reward) {
        if (slot >= 0 && slot < delegate.getSize()) {
            this.delegate.setItem(slot, reward);
        }
    }

    /**
     * Highlights a slot with a specific material (typically glass panes).
     *
     * @param slot     the slot index
     * @param material the material name (e.g., "YELLOW_STAINED_GLASS_PANE")
     */
    public void highlightSlot(int slot, String material) {
        if (slot >= 0 && slot < delegate.getSize()) {
            try {
                Material mat = Material.valueOf(material);
                ItemStack highlightItem = new ItemStack(mat);
                delegate.setItem(slot, highlightItem);
            } catch (IllegalArgumentException e) {
                Logger.debug("Invalid material for highlight: " + material);
            }
        }
    }

    /**
     * Gets the inventory size.
     *
     * @return the number of slots
     */
    public int getSize() {
        return delegate.getSize();
    }

    /**
     * Gets an item at a specific slot.
     *
     * @param slot the slot index
     * @return the item, or null if empty
     */
    public ItemStack getItem(int slot) {
        if (slot >= 0 && slot < delegate.getSize()) {
            return delegate.getItem(slot);
        }
        return null;
    }
}