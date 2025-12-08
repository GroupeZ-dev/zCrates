package fr.traqueur.crates.settings.readers;

import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.registries.AnimationsRegistry;
import fr.traqueur.crates.api.registries.Registry;
import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;

public class AnimationReader implements Reader<Animation> {
    @Override
    public Animation read(String s) throws StructuraException {
        return Registry.get(AnimationsRegistry.class).getById(s);
    }
}
