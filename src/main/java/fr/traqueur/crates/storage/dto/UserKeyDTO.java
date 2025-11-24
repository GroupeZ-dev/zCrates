package fr.traqueur.crates.storage.dto;

import fr.maxlego08.sarah.Column;

import java.util.UUID;

public record UserKeyDTO(
        @Column(value = "unique_id", primary = true) UUID uuid,
        @Column(value = "key_name", primary = true) String keyName,
        @Column(value = "amount") int amount
) {
}