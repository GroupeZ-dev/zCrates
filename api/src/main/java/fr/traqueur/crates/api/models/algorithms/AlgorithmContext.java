package fr.traqueur.crates.api.models.algorithms;

import fr.traqueur.crates.api.models.CrateOpening;
import fr.traqueur.crates.api.models.Wrapper;
import fr.traqueur.crates.api.models.crates.Reward;

import java.util.List;

/**
 * Context provided to random algorithms when selecting a reward.
 * Contains wrapped objects with helper methods for algorithm development.
 *
 * This follows the same pattern as AnimationContext, using Wrapper objects
 * to expose safe and convenient APIs to JavaScript.
 * @param rewards Wrapped list of possible rewards.
 * @param history Wrapped list of history of crate openings for the player.
 * @param crateId Identifier of the crate being opened.
 * @param playerUuid UUID of the player opening the crate.
 */
public record AlgorithmContext(
        Wrapper<List<Reward>> rewards,
        Wrapper<List<CrateOpening>> history,
        String crateId,
        String playerUuid
) {
}
