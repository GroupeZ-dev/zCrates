package fr.traqueur.crates.api.registries;

import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.models.crates.Crate;

/**
 * Abstract registry for managing crate configurations stored in files.
 *
 * <p>This class extends {@link FileBasedRegistry} to provide functionality
 * for loading and managing crate definitions from YAML files located in a
 * specified resource folder.</p>
 *
 * <p>Concrete implementations of this class should specify the resource folder
 * where crate configuration files are stored.</p>
 *
 * @see FileBasedRegistry
 * @see Crate
 */
public abstract class CratesRegistry extends FileBasedRegistry<String, Crate> {

    /**
     * Constructs a new {@code CratesRegistry} with the specified plugin and resource folder.
     *
     * @param plugin         the main plugin instance
     * @param resourceFolder the folder where crate configuration files are located
     */
    protected CratesRegistry(CratesPlugin plugin, String resourceFolder) {
        super(plugin, resourceFolder, "crates", ".yml", ".yaml");
    }
}
