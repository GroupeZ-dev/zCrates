package fr.traqueur.crates.models;

import fr.traqueur.crates.api.models.Wrapper;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Wrapper for Inventory that exposes only safe methods for animations.
 * This controls what animations can do with the crate inventory.
 */
public class InventoryWrapper extends Wrapper<Inventory> {

    public InventoryWrapper(Inventory handle) {
        super(handle);
    }

    /**
     * Clears the entire inventory.
     */
    public void clear() {
        handle.clear();
    }

    /**
     * Clears a specific slot.
     *
     * @param slot the slot index
     */
    public void clear(int slot) {
        if (slot >= 0 && slot < handle.getSize()) {
            handle.setItem(slot, null);
        }
    }

    /**
     * Sets an item at a specific slot.
     *
     * @param slot the slot index
     * @param item the item to set
     */
    public void setItem(int slot, ItemStack item) {
        if (slot >= 0 && slot < handle.getSize()) {
            handle.setItem(slot, item);
        }
    }

    /**
     * Sets a random item at the specified slot.
     * Implementation should be provided by the plugin.
     *
     * @param slot the slot index
     */
    public void setRandomItem(int slot) {
        if (slot >= 0 && slot < handle.getSize()) {
            // This will be implemented by the actual crate system
            // For now, just a placeholder
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
        ItemStack lastItem = handle.getItem(slots[slots.length - 1]);

        // Shift all items to the right
        for (int i = slots.length - 1; i > 0; i--) {
            ItemStack item = handle.getItem(slots[i - 1]);
            handle.setItem(slots[i], item);
        }

        // Put the last item at the first position
        handle.setItem(slots[0], lastItem);
    }

    /**
     * Sets the winning item at the specified slot.
     *
     * @param slot   the slot index
     * @param reward the reward object
     */
    public void setWinningItem(int slot, Object reward) {
        if (slot >= 0 && slot < handle.getSize()) {
            // This will be implemented by the actual crate system
            // The reward object should be cast to the appropriate type
        }
    }

    /**
     * Highlights a slot with a specific material (typically glass panes).
     *
     * @param slot     the slot index
     * @param material the material name (e.g., "YELLOW_STAINED_GLASS_PANE")
     */
    public void highlightSlot(int slot, String material) {
        if (slot >= 0 && slot < handle.getSize()) {
            try {
                Material mat = Material.valueOf(material);
                ItemStack highlightItem = new ItemStack(mat);
                handle.setItem(slot, highlightItem);
            } catch (IllegalArgumentException e) {
                // Invalid material name, ignore
            }
        }
    }

    /**
     * Gets the inventory size.
     *
     * @return the number of slots
     */
    public int getSize() {
        return handle.getSize();
    }

    /**
     * Gets an item at a specific slot.
     *
     * @param slot the slot index
     * @return the item, or null if empty
     */
    public ItemStack getItem(int slot) {
        if (slot >= 0 && slot < handle.getSize()) {
            return handle.getItem(slot);
        }
        return null;
    }
}