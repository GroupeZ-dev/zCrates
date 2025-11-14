package fr.traqueur.crates.api.settings.models;


import fr.traqueur.crates.api.models.items.ItemsProvider;
import fr.traqueur.crates.api.placeholders.PlaceholderParser;
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

public record ItemStackWrapper(
        @Options(optional = true) Material material,

        @Options(optional = true) @DefaultInt(1) int amount,

        @Options(optional = true) Delegate copyFrom,

        @Options(optional = true) String displayName,

        @Options(optional = true) String itemName,

        @Options(optional = true) List<String> lore
) implements Loadable {

    public record Delegate(String pluginName, String itemId) implements Loadable {

        public ItemStack item(Player player, String itemId) {
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
            String parsedDisplayNameStr =  PlaceholderParser.parsePlaceholders(player, displayName);
            parsedDisplayName = MessagesService.parseMessage(parsedDisplayNameStr);
        }

        List<Component> lore = new ArrayList<>();
        if (this.lore != null) {
            for (String loreLine : this.lore) {
                String parsedLoreLineStr = PlaceholderParser.parsePlaceholders(player, loreLine);
                Component parsedLoreLine = MessagesService.parseMessage(parsedLoreLineStr);
                lore.add(parsedLoreLine);
            }
        }

        Component parsedItemName = null;
        if (itemName != null && !itemName.isEmpty()) {
            String parsedItemNameStr = PlaceholderParser.parsePlaceholders(player, itemName);
            parsedItemName = MessagesService.parseMessage(parsedItemNameStr);
        }

        if (copyFrom != null) {
            ItemStack item = copyFrom.item(player, itemName);
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