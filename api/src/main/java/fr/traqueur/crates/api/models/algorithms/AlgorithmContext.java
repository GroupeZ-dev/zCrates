package fr.traqueur.crates.api.models.algorithms;

import fr.traqueur.crates.api.models.CrateOpening;
import fr.traqueur.crates.api.models.crates.Reward;

import java.util.List;

/**
 * Context provided to random algorithms when selecting a reward.
 * Contains all information needed to make an informed decision.
 */
public interface AlgorithmContext {

    /**
     * List of all possible rewards for this crate
     */
    List<Reward> rewards();

    /**
     * ID of the crate being opened
     */
    String crateId();

    /**
     * Complete opening history for this player and this crate
     * Ordered from oldest to newest
     */
    List<CrateOpening> history();

    /**
     * Player's UUID as a string
     */
    String playerUuid();

}
