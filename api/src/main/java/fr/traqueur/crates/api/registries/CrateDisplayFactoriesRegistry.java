package fr.traqueur.crates.api.registries;

import fr.traqueur.crates.api.models.placedcrates.CrateDisplayFactory;
import fr.traqueur.crates.api.models.placedcrates.DisplayType;

/**
 * Registry for managing crate display factories.
 */
public interface CrateDisplayFactoriesRegistry extends Registry<DisplayType, CrateDisplayFactory<?>> {

    /**
     * Registers a generic crate display factory for the specified display type.
     *
     * @param type    The display type.
     * @param factory The crate display factory.
     * @param <T>     The type of element that the crate display represents.
     */
    default <T> void registerGeneric(DisplayType type, CrateDisplayFactory<T> factory) {
        this.register(type, factory);
    }

}