package fr.traqueur.crates.api.services;

import fr.traqueur.crates.api.PlatformType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating and modifying ItemStacks with Paper/Spigot compatibility.
 * Works with Adventure Components throughout the code and handles conversion only at the final step.
 */
public class ItemsService {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    /*
     * Private constructor to prevent instantiation
     */
    private ItemsService() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Sets the display name of an ItemStack using a Component.
     * Handles Paper (native) vs Spigot (legacy conversion) automatically.
     *
     * @param itemStack   The ItemStack to modify
     * @param displayName The display name as a Component
     */
    public static void setDisplayName(ItemStack itemStack, Component displayName) {
        if (itemStack == null || displayName == null) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        Component processedDisplayName = displayName.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);


        if (PlatformType.isPaper()) {
            // Use Paper's native Adventure API
            meta.displayName(processedDisplayName);
        } else {
            // Convert Component to legacy format for Spigot
            String legacy = LEGACY_SERIALIZER.serialize(processedDisplayName);
            meta.setDisplayName(legacy);
        }

        itemStack.setItemMeta(meta);
    }

    /**
     * Sets the lore of an ItemStack using a list of Components.
     * Handles Paper (native) vs Spigot (legacy conversion) automatically.
     * Automatically disables italic decoration for all lore lines.
     *
     * @param itemStack The ItemStack to modify
     * @param lore      The lore lines as Components
     */
    public static void setLore(ItemStack itemStack, List<Component> lore) {
        if (itemStack == null || lore == null) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        // Disable italic decoration for all lore lines
        List<Component> processedLore = new ArrayList<>();
        for (Component line : lore) {
            processedLore.add(line.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }

        if (PlatformType.isPaper()) {
            // Use Paper's native Adventure API
            meta.lore(processedLore);
        } else {
            // Convert Components to legacy format for Spigot
            List<String> legacyLore = new ArrayList<>();
            for (Component line : processedLore) {
                String legacy = LEGACY_SERIALIZER.serialize(line);
                legacyLore.add(legacy);
            }
            meta.setLore(legacyLore);
        }

        itemStack.setItemMeta(meta);
    }

    /**
     * Creates a new ItemStack with the specified material, amount, display name, and lore.
     *
     * @param material    The material of the item
     * @param amount      The number of items in the stack
     * @param displayName The display name as a Component
     * @param lore        The lore lines as Components
     * @param itemName    The item name as a Component
     * @return The created ItemStack
     */
    public static ItemStack createItem(Material material, int amount, Component displayName, List<Component> lore, Component itemName) {
        ItemStack itemStack = ItemStack.of(material, amount);

        if (displayName != null) {
            setDisplayName(itemStack, displayName);
        }

        if (lore != null && !lore.isEmpty()) {
            setLore(itemStack, lore);
        }

        if (itemName != null) {
            setItemName(itemStack, itemName);
        }

        return itemStack;
    }

    /**
     * Sets the item name of an ItemStack using a Component.
     * Handles Paper (native) vs Spigot (legacy conversion) automatically.
     *
     * @param itemStack The ItemStack to modify
     * @param itemName  The item name as a Component
     */
    public static void setItemName(ItemStack itemStack, Component itemName) {
        if (itemStack == null || itemName == null) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        Component processedItemName = itemName.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);


        if (PlatformType.isPaper()) {
            // Use Paper's native Adventure API
            meta.itemName(processedItemName);
        } else {
            // Convert Component to legacy format for Spigot
            String legacy = LEGACY_SERIALIZER.serialize(processedItemName);
            meta.setItemName(legacy);
        }

        itemStack.setItemMeta(meta);
    }

    public static void addLoreLine(ItemStack itemStack, Component line) {
        if (itemStack == null || line == null) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        List<Component> currentLore;
        if (PlatformType.isPaper()) {
            currentLore = meta.lore();
        } else {
            List<String> legacyLore = meta.getLore();
            currentLore = new ArrayList<>();
            if (legacyLore != null) {
                for (String legacyLine : legacyLore) {
                    currentLore.add(LEGACY_SERIALIZER.deserialize(legacyLine));
                }
            }
        }

        if (currentLore == null) {
            currentLore = new ArrayList<>();
        }

        currentLore.add(line.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        setLore(itemStack, currentLore);
    }
}