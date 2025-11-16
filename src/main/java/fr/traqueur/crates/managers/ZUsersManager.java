package fr.traqueur.crates.managers;

import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.managers.UsersManager;
import fr.traqueur.crates.api.models.User;
import fr.traqueur.crates.api.storage.repositories.Repository;
import fr.traqueur.crates.listeners.UsersListener;
import fr.traqueur.crates.models.ZUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ZUsersManager implements UsersManager {

    private final Repository<User, UUID> repository;
    private final Map<UUID, User> cachedUsers;

    public ZUsersManager(Repository<User, UUID> repository) {
        this.repository = repository;
        this.cachedUsers = new HashMap<>();
    }

    @Override
    public void init() {
        this.getPlugin().registerListener(new UsersListener(this));
        this.repository.init().thenAccept(created -> {
            if(!created) {
                Logger.severe("Failed to initialize Users repository.");
                return;
            }
            Bukkit.getScheduler().runTask(this.getPlugin(), () -> {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    this.loadUser(onlinePlayer.getUniqueId());
                }
            });
        });
    }

    @Override
    public void loadUser(UUID uuid) {
        repository.get(uuid).thenCompose(user -> {
            if (user != null) {
                return CompletableFuture.completedFuture(user);
            } else {
                User newUser = new ZUser(uuid);
                return repository.save(newUser).thenApply(__ -> newUser);
            }
        }).thenAccept(user -> {
            cachedUsers.put(uuid, user);
        }).exceptionally(ex -> {
            Logger.severe("Failed to load user data for {}: {}", uuid, ex.getMessage());
            return null;
        });
    }

    @Override
    public void unloadUser(UUID uuid) {
        User user = cachedUsers.remove(uuid);
        if (user != null) {
            repository.save(user).exceptionally(ex -> {
                Logger.severe("Failed to save user data for {}: {}", uuid, ex.getMessage());
                return null;
            });
        }
    }
}
