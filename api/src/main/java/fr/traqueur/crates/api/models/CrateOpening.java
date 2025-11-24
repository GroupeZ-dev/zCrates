package fr.traqueur.crates.api.models;

import java.util.UUID;

/**
 * Represents a record of a crate opening by a player.
 *
 * @param id         The unique identifier of the crate opening.
 * @param playerUuid The UUID of the player who opened the crate.
 * @param crateId    The identifier of the crate that was opened.
 * @param rewardId   The identifier of the reward obtained from the crate.
 * @param timestamp  The timestamp when the crate was opened.
 */
public record CrateOpening(
        UUID id,
        UUID playerUuid,
        String crateId,
        String rewardId,
        long timestamp
) {
}