package fr.traqueur.crates.settings.readers;

import fr.traqueur.crates.api.models.algorithms.RandomAlgorithm;
import fr.traqueur.crates.api.registries.RandomAlgorithmsRegistry;
import fr.traqueur.crates.api.registries.Registry;
import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;

public class RandomAlgorithmReader implements Reader<RandomAlgorithm> {
    @Override
    public RandomAlgorithm read(String s) throws StructuraException {
        return Registry.get(RandomAlgorithmsRegistry.class).getById(s);
    }
}
