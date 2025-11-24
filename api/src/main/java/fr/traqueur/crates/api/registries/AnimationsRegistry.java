package fr.traqueur.crates.api.registries;

import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.models.animations.Animation;

/**
 * Registry for managing animations loaded from files.
 */
public abstract class AnimationsRegistry extends FileBasedRegistry<String, Animation> {

    /**
     * Constructs an AnimationsRegistry with the specified plugin and resource folder.
     *
     * @param plugin         The CratesPlugin instance.
     * @param resourceFolder The folder where animation files are located.
     */
    protected AnimationsRegistry(CratesPlugin plugin, String resourceFolder) {
        super(plugin, resourceFolder, "animations", ".js");
    }
}
