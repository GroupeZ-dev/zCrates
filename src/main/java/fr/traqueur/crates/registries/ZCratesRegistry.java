package fr.traqueur.crates.registries;

import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.registries.CratesRegistry;
import fr.traqueur.crates.models.ZCrate;
import fr.traqueur.structura.api.Structura;
import fr.traqueur.structura.exceptions.StructuraException;

import java.nio.file.Path;

public class ZCratesRegistry extends CratesRegistry {

    public ZCratesRegistry(CratesPlugin plugin, String resourceFolder) {
        super(plugin, resourceFolder);
    }

    @Override
    protected Crate loadFile(Path file) {
        try {
            Crate crate = Structura.load(file, ZCrate.class);
            this.register(crate.id(), crate);
            Logger.debug("Loaded crate: " + crate.id() + " from file: " + file.getFileName());
            return crate;
        } catch (StructuraException e) {
            Logger.severe("Failed to load item from file: " + file.getFileName(), e);
            return null;
        }

    }
}
