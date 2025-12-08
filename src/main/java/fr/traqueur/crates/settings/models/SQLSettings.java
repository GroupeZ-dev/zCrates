package fr.traqueur.crates.settings.models;

import fr.maxlego08.sarah.DatabaseConfiguration;
import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.sarah.HikariDatabaseConnection;
import fr.maxlego08.sarah.database.DatabaseType;
import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.settings.models.DatabaseSettings;
import fr.traqueur.structura.annotations.Options;

public record SQLSettings(DatabaseType type,
                          @Options(optional = true) String tablePrefix,
                          String user,
                          String password,
                          int port,
                          String host,
                          String database) implements DatabaseSettings {
    @Override
    public DatabaseConnection connection(boolean debug) {
        DatabaseConfiguration configuration = new DatabaseConfiguration(tablePrefix, user, password, port, host, database, debug, type);
        return new HikariDatabaseConnection(configuration, Logger::info);
    }
}
