package fr.traqueur.crates.api.models;

import java.util.UUID;

public record CrateOpening(
        UUID id,
        UUID playerUuid,
        String crateId,
        String rewardId,
        long timestamp
) {
}