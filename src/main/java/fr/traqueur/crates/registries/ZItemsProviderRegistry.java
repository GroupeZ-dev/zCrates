package fr.traqueur.crates.registries;

import fr.traqueur.crates.api.providers.ItemsProvider;
import fr.traqueur.crates.api.registries.ItemsProvidersRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZItemsProviderRegistry implements ItemsProvidersRegistry {

    private final Map<String, ItemsProvider> storage;

    public ZItemsProviderRegistry() {
        this.storage = new HashMap<>();
    }

    @Override
    public void register(String s, ItemsProvider item) {
        this.storage.put(s, item);
    }

    @Override
    public ItemsProvider getById(String s) {
        return this.storage.get(s);
    }

    @Override
    public List<ItemsProvider> getAll() {
        return new ArrayList<>(this.storage.values());
    }

    @Override
    public void clear() {
        this.storage.clear();
    }
}
