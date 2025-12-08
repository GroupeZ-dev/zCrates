package fr.traqueur.crates.storage.dto;

import fr.maxlego08.sarah.Column;
import fr.traqueur.crates.api.models.CrateOpening;
import fr.traqueur.crates.api.storage.DTO;

import java.util.UUID;

public record CrateOpeningDTO(
        @Column(value = "id", primary = true) UUID id,
        @Column(value = "player_uuid") UUID playerUuid,
        @Column(value = "crate_id") String crateId,
        @Column(value = "reward_id") String rewardId,
        @Column(value = "timestamp") long timestamp
) implements DTO<CrateOpening> {

    @Override
    public CrateOpening toModel() {
        return new CrateOpening(id, playerUuid, crateId, rewardId, timestamp);
    }

    public static CrateOpeningDTO fromModel(CrateOpening item) {
        return new CrateOpeningDTO(
                item.id(),
                item.playerUuid(),
                item.crateId(),
                item.rewardId(),
                item.timestamp()
        );
    }

}