package fr.traqueur.crates.api.models.algorithms;

import fr.traqueur.crates.api.models.crates.Reward;

import java.util.List;
import java.util.function.Function;

/**
 * Interface for reward selection algorithms.
 * Algorithms can be implemented in JavaScript to provide custom logic
 * for selecting rewards from crates (e.g., pity systems, guaranteed rewards).
 */
public interface RandomAlgorithm {

    /**
     * Unique identifier for this algorithm
     */
    String id();

    /**
     * Source file where this algorithm was defined
     */
    String sourceFile();

    /**
     * Function that selects a reward based on the context
     * The function receives an AlgorithmContext and returns the selected Reward
     */
    Function<AlgorithmContext, Reward> selector();

}
