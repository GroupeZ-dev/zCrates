package fr.traqueur.crates.api.registries;

import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.models.algorithms.RandomAlgorithm;

/**
 * Registry for random algorithms used in crates.
 */
public abstract class RandomAlgorithmsRegistry extends FileBasedRegistry<String, RandomAlgorithm> {

    /**
     * Constructor for RandomAlgorithmsRegistry.
     *
     * @param plugin         the CratesPlugin instance
     * @param resourceFolder the resource folder path
     */
    protected RandomAlgorithmsRegistry(CratesPlugin plugin, String resourceFolder) {
        super(plugin, resourceFolder, "algorithms", ".js");
    }
}
