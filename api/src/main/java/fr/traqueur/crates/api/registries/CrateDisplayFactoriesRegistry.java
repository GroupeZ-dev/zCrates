package fr.traqueur.crates.api.registries;

import fr.traqueur.crates.api.models.placedcrates.CrateDisplayFactory;
import fr.traqueur.crates.api.models.placedcrates.DisplayType;

public interface CrateDisplayFactoriesRegistry extends Registry<DisplayType, CrateDisplayFactory<?>> {

    default <T> void registerGeneric(DisplayType type, CrateDisplayFactory<T> factory) {
        this.register(type, factory);
    }

}