package fr.traqueur.crates.storage.repositories;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import fr.maxlego08.sarah.MigrationManager;
import fr.maxlego08.sarah.RequestHelper;
import fr.maxlego08.sarah.transaction.Transaction;
import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.models.CrateOpening;
import fr.traqueur.crates.api.models.User;
import fr.traqueur.crates.api.settings.Settings;
import fr.traqueur.crates.api.storage.Tables;
import fr.traqueur.crates.api.storage.repositories.SQLRepository;
import fr.traqueur.crates.models.ZUser;
import fr.traqueur.crates.settings.PluginSettings;
import fr.traqueur.crates.storage.dto.CrateOpeningDTO;
import fr.traqueur.crates.storage.dto.UserDTO;
import fr.traqueur.crates.storage.dto.UserKeyDTO;
import fr.traqueur.crates.storage.migrations.CrateOpeningsTableMigration;
import fr.traqueur.crates.storage.migrations.UserKeysTableMigration;
import fr.traqueur.crates.storage.migrations.UserTableMigration;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
            MigrationManager.registerMigration(new CrateOpeningsTableMigration());
            return true;
        }, DB_EXECUTOR);
    }

    @Override
    public CompletableFuture<List<User>> findAll() {
        return CompletableFuture.supplyAsync(() -> {
            List<UserDTO> users = this.requestHelper.selectAll(this.prefix + Tables.USERS_TABLE, UserDTO.class);
            List<UserKeyDTO> allKeys = this.requestHelper.selectAll(this.prefix + Tables.USER_KEYS_TABLE, UserKeyDTO.class);
            List<CrateOpeningDTO> allOpenings = this.requestHelper.selectAll(this.prefix + Tables.CRATE_OPENINGS_TABLE, CrateOpeningDTO.class);

            Map<UUID, Map<String, Integer>> keysByUser = new HashMap<>();
            for (UserKeyDTO keyDTO : allKeys) {
                keysByUser.computeIfAbsent(keyDTO.uuid(), k -> new HashMap<>())
                        .put(keyDTO.keyName(), keyDTO.amount());
            }

            Map<UUID, List<CrateOpening>> openingsByUser = new HashMap<>();
            for (CrateOpeningDTO openingDTO : allOpenings) {
                CrateOpening opening = new CrateOpening(
                        openingDTO.id(),
                        openingDTO.playerUuid(),
                        openingDTO.crateId(),
                        openingDTO.rewardId(),
                        openingDTO.timestamp()
                );
                openingsByUser.computeIfAbsent(openingDTO.playerUuid(), k -> new ArrayList<>())
                        .add(opening);
            }

            return users.stream()
                    .map(dto -> {
                        Map<String, Integer> userKeys = keysByUser.getOrDefault(dto.uuid(), new HashMap<>());
                        List<CrateOpening> userOpenings = openingsByUser.getOrDefault(dto.uuid(), new ArrayList<>());
                        return (User) new ZUser(dto.uuid(), userKeys, userOpenings);
                    })
                    .collect(Collectors.toList());
        }, DB_EXECUTOR);
    }

    @Override
    public CompletableFuture<Void> save(@NotNull User item) {
        return CompletableFuture.runAsync(() -> {
            try (Transaction transaction = this.requestHelper.getConnection().beginTransaction()) {
                UserDTO data = UserDTO.fromModel(item);
                this.requestHelper.upsert(prefix + Tables.USERS_TABLE, UserDTO.class, data);

                Map<String, Integer> keys = item.getAllKeys();
                for (Map.Entry<String, Integer> entry : keys.entrySet()) {
                    if (entry.getValue() > 0) {
                        UserKeyDTO keyDTO = new UserKeyDTO(item.uuid(), entry.getKey(), entry.getValue());
                        this.requestHelper.upsert(prefix + Tables.USER_KEYS_TABLE, UserKeyDTO.class, keyDTO);
                    } else {
                        this.requestHelper.delete(this.prefix + Tables.USER_KEYS_TABLE, table -> {
                            table.where("unique_id", item.uuid());
                            table.where("key_name", entry.getKey());
                        });
                    }
                }

                List<CrateOpening> openings = item.getCrateOpenings();
                this.requestHelper.upsertMultiple(this.prefix + Tables.CRATE_OPENINGS_TABLE, CrateOpeningDTO.class, openings.stream().map(CrateOpeningDTO::fromModel).toList());

                transaction.commit();
            } catch (Exception e) {
                Logger.severe("Failed to save user {}", e, item.uuid());
            }
        }, DB_EXECUTOR);
    }

    @Override
    public CompletableFuture<Void> delete(@NotNull UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            try (Transaction transaction = this.requestHelper.getConnection().beginTransaction()) {
                this.requestHelper.delete(this.prefix + Tables.USER_KEYS_TABLE, table -> {
                    table.where("unique_id", uuid);
                });
                this.requestHelper.delete(this.prefix + Tables.CRATE_OPENINGS_TABLE, table -> {
                    table.where("player_uuid", uuid);
                });
                this.requestHelper.delete(this.prefix + Tables.USERS_TABLE, table -> {
                    table.where(this.getPrimaryKeyColumn(), uuid);
                });

                transaction.commit();
            } catch (Exception e) {
                Logger.severe("Failed to delete user {}", e, uuid);
            }
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

            List<CrateOpeningDTO> openingResults = this.requestHelper.select(this.prefix + Tables.CRATE_OPENINGS_TABLE, CrateOpeningDTO.class, table -> {
                table.where("player_uuid", uuid);
            });

            List<CrateOpening> openings = new ArrayList<>();
            for (CrateOpeningDTO openingDTO : openingResults) {
                openings.add(openingDTO.toModel());
            }

            return new ZUser(uuid, keys, openings);
        }, DB_EXECUTOR);
    }
}
