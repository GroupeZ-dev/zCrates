package fr.traqueur.crates.models.conditions;

import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.crates.OpenCondition;
import org.bukkit.entity.Player;

public record PermissionCondition(String permission) implements OpenCondition {

    @Override
    public boolean check(Player player, Crate crate) {
        return player.hasPermission(permission);
    }

    @Override
    public String errorMessageKey() {
        return "no-permission";
    }
}