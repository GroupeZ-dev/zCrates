package fr.traqueur.crates.api.registries;

import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.models.animations.Animation;

public abstract class AnimationsRegistry extends FileBasedRegistry<String, Animation> {

    protected AnimationsRegistry(CratesPlugin plugin, String resourceFolder) {
        super(plugin, resourceFolder, "animations", ".js");
    }
}
