package fr.traqueur.crates.api.registries;

import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.models.crates.Crate;

public abstract class CratesRegistry extends FileBasedRegistry<String, Crate> {

    protected CratesRegistry(CratesPlugin plugin, String resourceFolder) {
        super(plugin, resourceFolder, "crates", ".yml", ".yaml");
    }
}
