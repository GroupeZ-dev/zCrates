package fr.traqueur.crates.storage.repositories;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import fr.maxlego08.sarah.MigrationManager;
import fr.maxlego08.sarah.RequestHelper;
import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.models.User;
import fr.traqueur.crates.api.settings.Settings;
import fr.traqueur.crates.api.storage.Tables;
import fr.traqueur.crates.api.storage.repositories.SQLRepository;
import fr.traqueur.crates.models.ZUser;
import fr.traqueur.crates.settings.PluginSettings;
import fr.traqueur.crates.storage.dto.UserDTO;
import fr.traqueur.crates.storage.dto.UserKeyDTO;
import fr.traqueur.crates.storage.migrations.UserKeysTableMigration;
import fr.traqueur.crates.storage.migrations.UserTableMigration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class UserRepository extends SQLRepository<User, UserDTO, UUID> {

    private static final ExecutorService DB_EXECUTOR = Executors.newFixedThreadPool(3,
            new ThreadFactoryBuilder()
                    .setNameFormat("UserRepository-%d")
                    .setUncaughtExceptionHandler((t, e) -> Logger.severe("Uncaught exception in thread {}", e, t.getName()))
                    .build());

    private final String prefix;

    public UserRepository(RequestHelper requestHelper) {
        super(requestHelper);
        this.prefix = Settings.get(PluginSettings.class).database().tablePrefix();
    }

    @Override
    public CompletableFuture<Boolean> createTable() {
        return CompletableFuture.supplyAsync(() -> {
            MigrationManager.registerMigration(new UserTableMigration());
            MigrationManager.registerMigration(new UserKeysTableMigration());
            return true;
        }, DB_EXECUTOR);
    }

    @Override
    public CompletableFuture<List<User>> findAll() {
        return CompletableFuture.supplyAsync(() -> {
            List<UserDTO> users = this.requestHelper.selectAll(this.prefix + Tables.USERS_TABLE, UserDTO.class);
            List<UserKeyDTO> allKeys = this.requestHelper.selectAll(this.prefix + Tables.USER_KEYS_TABLE, UserKeyDTO.class);

            Map<UUID, Map<String, Integer>> keysByUser = new HashMap<>();
            for (UserKeyDTO keyDTO : allKeys) {
                keysByUser.computeIfAbsent(keyDTO.uuid(), k -> new HashMap<>())
                        .put(keyDTO.keyName(), keyDTO.amount());
            }

            return users.stream()
                    .map(dto -> {
                        Map<String, Integer> userKeys = keysByUser.getOrDefault(dto.uuid(), new HashMap<>());
                        return (User) new ZUser(dto.uuid(), userKeys);
                    })
                    .collect(Collectors.toList());
        }, DB_EXECUTOR);
    }

    @Override
    public CompletableFuture<Void> save(@NotNull User item) {
        return CompletableFuture.runAsync(() -> {
            UserDTO data = UserDTO.fromModel(item);
            this.save(prefix, Tables.USERS_TABLE, data);

            Map<String, Integer> keys = item.getAllKeys();
            for (Map.Entry<String, Integer> entry : keys.entrySet()) {
                if (entry.getValue() > 0) {
                    UserKeyDTO keyDTO = new UserKeyDTO(item.uuid(), entry.getKey(), entry.getValue());
                    this.save(prefix, Tables.USER_KEYS_TABLE, keyDTO);
                } else {
                    this.requestHelper.delete(this.prefix + Tables.USER_KEYS_TABLE, table -> {
                        table.where("unique_id", item.uuid());
                        table.where("key_name", entry.getKey());
                    });
                }
            }
        }, DB_EXECUTOR);
    }

    @Override
    public CompletableFuture<Void> delete(@NotNull UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            this.requestHelper.delete(this.prefix + Tables.USER_KEYS_TABLE, table -> {
                table.where("unique_id", uuid);
            });
            this.requestHelper.delete(this.prefix + Tables.USERS_TABLE, table -> {
                table.where(this.getPrimaryKeyColumn(), uuid);
            });
        }, DB_EXECUTOR);
    }

    @Override
    public CompletableFuture<User> get(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            List<UserDTO> result = this.requestHelper.select(this.prefix + Tables.USERS_TABLE, UserDTO.class, table -> {
                table.where(this.getPrimaryKeyColumn(), uuid);
            });

            if (result.isEmpty()) {
                return null;
            }

            List<UserKeyDTO> keyResults = this.requestHelper.select(this.prefix + Tables.USER_KEYS_TABLE, UserKeyDTO.class, table -> {
                table.where("unique_id", uuid);
            });

            Map<String, Integer> keys = new HashMap<>();
            for (UserKeyDTO keyDTO : keyResults) {
                keys.put(keyDTO.keyName(), keyDTO.amount());
            }

            return new ZUser(uuid, keys);
        }, DB_EXECUTOR);
    }
}
