package fr.traqueur.crates.registries;

import fr.traqueur.crates.api.models.placedcrates.CrateDisplayFactory;
import fr.traqueur.crates.api.models.placedcrates.DisplayType;
import fr.traqueur.crates.api.registries.CrateDisplayFactoriesRegistry;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ZCrateDisplayFactoriesRegistry implements CrateDisplayFactoriesRegistry {

    private final Map<DisplayType, CrateDisplayFactory<?>> storage;

    public ZCrateDisplayFactoriesRegistry() {
        this.storage = new EnumMap<>(DisplayType.class);
    }

    @Override
    public void register(DisplayType type, CrateDisplayFactory<?> factory) {
        this.storage.put(type, factory);
    }

    @Override
    public CrateDisplayFactory<?> getById(DisplayType type) {
        return this.storage.get(type);
    }

    @Override
    public List<CrateDisplayFactory<?>> getAll() {
        return new ArrayList<>(this.storage.values());
    }

    @Override
    public void clear() {
        this.storage.clear();
    }
}