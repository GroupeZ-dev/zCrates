package fr.traqueur.crates.api.registries;

import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.models.algorithms.RandomAlgorithm;

public abstract class RandomAlgorithmsRegistry extends FileBasedRegistry<String, RandomAlgorithm> {

    protected RandomAlgorithmsRegistry(CratesPlugin plugin, String resourceFolder) {
        super(plugin, resourceFolder, "algorithms", ".js");
    }
}
