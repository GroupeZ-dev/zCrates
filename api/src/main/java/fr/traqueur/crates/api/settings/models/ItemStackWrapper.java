package fr.traqueur.crates.api.settings.models;


import fr.traqueur.crates.api.providers.ItemsProvider;
import fr.traqueur.crates.api.providers.PlaceholderProvider;
import fr.traqueur.crates.api.registries.ItemsProvidersRegistry;
import fr.traqueur.crates.api.registries.Registry;
import fr.traqueur.crates.api.services.ItemsService;
import fr.traqueur.crates.api.services.MessagesService;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultInt;
import fr.traqueur.structura.api.Loadable;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class for configuring and building ItemStack instances.
 * Supports direct material specification or delegation to an ItemsProvider.
 * Allows customization of display name, lore, and item name with placeholder support.
 * @param material     the material of the item (optional if using copyFrom)
 * @param amount       the quantity of the item (default is 1)
 * @param copyFrom     delegate to an ItemsProvider to create the item (optional)
 * @param displayName  the display name of the item (optional)
 * @param itemName     the custom item name (optional)
 * @param lore         the lore of the item as a list of strings (optional)
 */
public record ItemStackWrapper(
        @Options(optional = true) Material material,

        @Options(optional = true) @DefaultInt(1) int amount,

        @Options(optional = true) Delegate copyFrom,

        @Options(optional = true) String displayName,

        @Options(optional = true) String itemName,

        @Options(optional = true) List<String> lore
) implements Loadable {

    /**
     * Delegate class for specifying an item via an ItemsProvider.
     *
     * @param pluginName the name of the plugin providing the ItemsProvider
     * @param itemId     the identifier of the item within the provider
     */
    public record Delegate(String pluginName, String itemId) implements Loadable {

        /**
         * Retrieves the ItemStack from the specified ItemsProvider.
         *
         * @param player the player for context (used when building custom items)
         * @return the ItemStack provided by the ItemsProvider
         * @throws IllegalArgumentException if no provider is found for the given plugin name
         */
        public ItemStack item(Player player) {
            ItemsProvider provider = Registry.get(ItemsProvidersRegistry.class).getById(pluginName);
            if (provider == null) {
                throw new IllegalArgumentException("No item provider found for plugin: " + pluginName);
            }
            return provider.item(player, itemId);
        }

    }

    /**
     * Validates the configuration after loading.
     *
     * @throws IllegalArgumentException if amount is less than 1 or if neither itemId nor material is specified
     */
    public ItemStackWrapper {
        if (amount < 1) {
            throw new IllegalArgumentException("Amount must be at least 1");
        }
        if(copyFrom == null && material == null) {
            throw new IllegalArgumentException("Either 'copy-from' or 'material' must be specified");
        }
    }

    /**
     * Builds an ItemStack from these settings.
     *
     * @param player the player for context (used when building custom items)
     * @return the created ItemStack
     * @throws IllegalStateException if neither itemId nor material is specified
     */
    public @NotNull ItemStack build(@Nullable Player player) {
        Component parsedDisplayName = null;
        if (displayName != null && !displayName.isEmpty()) {
            String parsedDisplayNameStr =  PlaceholderProvider.parsePlaceholders(player, displayName);
            parsedDisplayName = MessagesService.parseMessage(parsedDisplayNameStr);
        }

        List<Component> lore = new ArrayList<>();
        if (this.lore != null) {
            for (String loreLine : this.lore) {
                String parsedLoreLineStr = PlaceholderProvider.parsePlaceholders(player, loreLine);
                Component parsedLoreLine = MessagesService.parseMessage(parsedLoreLineStr);
                lore.add(parsedLoreLine);
            }
        }

        Component parsedItemName = null;
        if (itemName != null && !itemName.isEmpty()) {
            String parsedItemNameStr = PlaceholderProvider.parsePlaceholders(player, itemName);
            parsedItemName = MessagesService.parseMessage(parsedItemNameStr);
        }

        if (copyFrom != null) {
            ItemStack item = copyFrom.item(player);
            if(item == null) {
                throw new IllegalStateException("Item provider returned null for itemId: " + copyFrom.itemId);
            }
            if(parsedDisplayName != null) {
                ItemsService.setDisplayName(item, parsedDisplayName);
            }
            if(!lore.isEmpty()) {
                ItemsService.setLore(item, lore);
            }
            if (parsedItemName != null) {
                ItemsService.setItemName(item, parsedItemName);
            }
            if (item.getAmount() != amount) {
                item.setAmount(amount);
            }
            return item;
        }

        return ItemsService.createItem(material, amount, parsedDisplayName, lore, parsedItemName);
    }
}