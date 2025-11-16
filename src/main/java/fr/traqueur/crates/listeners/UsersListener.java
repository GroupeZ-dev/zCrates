package fr.traqueur.crates.listeners;

import fr.traqueur.crates.api.managers.UsersManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UsersListener implements Listener {

    private final UsersManager usersManager;

    public UsersListener(UsersManager usersManager) {
        this.usersManager = usersManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        usersManager.loadUser(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        usersManager.unloadUser(event.getPlayer().getUniqueId());
    }

}
