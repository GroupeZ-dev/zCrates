package fr.traqueur.crates.registries;

import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.registries.AnimationsRegistry;

import java.nio.file.Path;

public class ZAnimationRegistry extends AnimationsRegistry {

    public ZAnimationRegistry(CratesPlugin plugin, String resourceFolder) {
        super(plugin, resourceFolder);
    }

    @Override
    protected Animation loadFile(Path file) {
        return null;
    }
}
