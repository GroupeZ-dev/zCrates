package fr.traqueur.crates.api.settings;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import fr.traqueur.structura.api.Loadable;

/**
 * A generic interface for application settings that can be loaded.
 * Provides methods to register and retrieve settings instances.
 */
public interface Settings extends Loadable {

    /** A map to hold instances of settings classes. */
    ClassToInstanceMap<Settings> INSTANCES = MutableClassToInstanceMap.create();

    /**
     * Retrieves the settings instance of the specified class.
     *
     * @param clazz the class of the settings to retrieve
     * @param <T>   the type of the settings
     * @return the settings instance
     */
    static <T extends Settings> T get(Class<T> clazz) {
        return INSTANCES.getInstance(clazz);
    }

    /**
     * Registers a settings instance for the specified class.
     *
     * @param clazz    the class of the settings to register
     * @param instance the settings instance to register
     * @param <T>      the type of the settings
     */
    static <T extends Settings> void register(Class<T> clazz, T instance) {
        INSTANCES.putInstance(clazz, instance);
    }

}
