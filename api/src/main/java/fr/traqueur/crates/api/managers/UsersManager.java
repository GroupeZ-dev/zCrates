package fr.traqueur.crates.api.managers;

import java.util.UUID;

public non-sealed interface UsersManager extends Manager {

    void loadUser(UUID uuid);

    void unloadUser(UUID uuid);
}
