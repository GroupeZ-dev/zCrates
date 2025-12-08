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
     * @return Algorithm ID
     */
    String id();

    /**
     * Source file where this algorithm was defined
     * @return Source file path
     */
    String sourceFile();

    /**
     * Function that selects a reward based on the context
     * The function receives an AlgorithmContext and returns the selected Reward
     * @return Function that selects a reward
     */
    Function<AlgorithmContext, Reward> selector();

}
