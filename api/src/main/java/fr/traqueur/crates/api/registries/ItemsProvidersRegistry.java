package fr.traqueur.crates.api.registries;

import fr.traqueur.crates.api.providers.ItemsProvider;

/**
 * Registry interface for managing ItemsProvider instances within the application.
 */
public interface ItemsProvidersRegistry extends Registry<String, ItemsProvider> {
}
