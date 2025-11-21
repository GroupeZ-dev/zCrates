package fr.traqueur.crates.models.algorithms;

import fr.traqueur.crates.api.models.CrateOpening;
import fr.traqueur.crates.api.models.algorithms.AlgorithmContext;
import fr.traqueur.crates.api.models.crates.Reward;

import java.util.List;

/**
 * Implementation of AlgorithmContext wrapper.
 * This is the secure context exposed to JavaScript algorithms.
 */
public record ZAlgorithmContext(
        List<Reward> rewards,
        String crateId,
        List<CrateOpening> history,
        String playerUuid
) implements AlgorithmContext {
}
