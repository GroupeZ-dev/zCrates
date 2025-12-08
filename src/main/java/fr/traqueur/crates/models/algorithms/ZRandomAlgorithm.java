package fr.traqueur.crates.models.algorithms;

import fr.traqueur.crates.api.models.algorithms.AlgorithmContext;
import fr.traqueur.crates.api.models.algorithms.RandomAlgorithm;
import fr.traqueur.crates.api.models.crates.Reward;

import java.util.function.Function;

public record ZRandomAlgorithm(
            String id,
            String sourceFile,
            Function<AlgorithmContext, Reward> selector
    ) implements RandomAlgorithm {
    }