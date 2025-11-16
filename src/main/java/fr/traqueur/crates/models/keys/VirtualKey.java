package fr.traqueur.crates.models.keys;

import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.managers.UsersManager;
import fr.traqueur.crates.api.models.User;
import fr.traqueur.crates.api.models.crates.Key;
import org.bukkit.entity.Player;

public record VirtualKey(String name) implements Key {

    private static final CratesPlugin PLUGIN = CratesPlugin.getPlugin(CratesPlugin.class);

    @Override
    public boolean has(Player player) {
        UsersManager usersManager = PLUGIN.getManager(UsersManager.class);
        User user = usersManager.getUser(player.getUniqueId());
        if (user == null) {
            return false;
        }
        return user.hasKey(this.name);
    }

    @Override
    public void remove(Player player) {
        UsersManager usersManager = PLUGIN.getManager(UsersManager.class);
        User user = usersManager.getUser(player.getUniqueId());
        if (user != null) {
            user.removeKeys(this.name, 1);
        }
    }

    @Override
    public void give(Player player) {
        UsersManager usersManager = PLUGIN.getManager(UsersManager.class);
        User user = usersManager.getUser(player.getUniqueId());
        if (user != null) {
            user.addKeys(this.name, 1);
        }
    }
}
