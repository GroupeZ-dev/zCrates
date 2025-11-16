package fr.traqueur.crates.models;

import fr.traqueur.crates.api.models.User;

import java.util.UUID;

public record ZUser(UUID uuid) implements User {
}
