package fr.traqueur.crates.api.registries;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

import java.util.Collection;

/**
 * A generic registry interface for managing items identified by unique IDs.
 *
 * @param <ID> The type of the unique identifier for items.
 * @param <T>  The type of items to be registered.
 */
public interface Registry<ID, T> {

    /** A map to hold registry instances */
    ClassToInstanceMap<Registry<?, ?>> INSTANCES = MutableClassToInstanceMap.create();

    /**
     * Get a registry instance for a specific class.
     *
     * @param clazz The class of the registry.
     * @param <T>   The type of the registry.
     * @return The registry instance.
     */
    static <T extends Registry<?, ?>> T get(Class<T> clazz) {
        return INSTANCES.getInstance(clazz);
    }

    /**
     * Register a registry instance for a specific class.
     *
     * @param clazz    The class of the registry.
     * @param instance The registry instance to register.
     * @param <T>      The type of the registry.
     */
    static <T extends Registry<?, ?>> void register(Class<T> clazz, T instance) {
        INSTANCES.putInstance(clazz, instance);
    }


    /**
     * Register an item with a specific ID.
     *
     * @param id   The ID of the item.
     * @param item The item to register.
     */
    void register(ID id, T item);

    /**
     * Get a registered item by its ID.
     *
     * @param id The ID of the item.
     * @return The registered item, or null if not found.
     */
    T getById(ID id);

    /**
     * Get all registered items in this registry.
     *
     * @return A collection of all registered items.
     */
    Collection<T> getAll();

    /**
     * Clear all registered items in this registry.
     */
    void clear();

}
