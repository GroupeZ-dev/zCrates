package fr.traqueur.crates.api.managers;

import fr.traqueur.crates.api.models.User;

import java.util.UUID;

public non-sealed interface UsersManager extends Manager {

    void loadUser(UUID uuid);

    void unloadUser(UUID uuid);

    User getUser(UUID uuid);
}
