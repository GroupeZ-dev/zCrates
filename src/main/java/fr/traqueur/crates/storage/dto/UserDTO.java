package fr.traqueur.crates.storage.dto;

import fr.maxlego08.sarah.Column;
import fr.traqueur.crates.api.models.User;
import fr.traqueur.crates.api.storage.DTO;
import fr.traqueur.crates.models.ZUser;

import java.util.UUID;

public record UserDTO(@Column(value = "unique_id", primary = true) UUID uuid) implements DTO<User> {
    @Override
    public User toModel() {
        return new ZUser(uuid);
    }

    public static UserDTO fromModel(User user) {
        return new UserDTO(user.uuid());
    }

}
