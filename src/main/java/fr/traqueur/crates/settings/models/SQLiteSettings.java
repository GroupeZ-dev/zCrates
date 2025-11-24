package fr.traqueur.crates.settings.models;

import fr.maxlego08.sarah.DatabaseConfiguration;
import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.sarah.SqliteConnection;
import fr.maxlego08.sarah.database.DatabaseType;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.settings.models.DatabaseSettings;

public record SQLiteSettings(String tablePrefix) implements DatabaseSettings {

    private static final CratesPlugin PLUGIN = CratesPlugin.getPlugin(CratesPlugin.class);

    @Override
    public DatabaseConnection connection(boolean debug) {
        DatabaseConfiguration configuration = new DatabaseConfiguration(tablePrefix, null, null, 0, null, null, debug, DatabaseType.SQLITE);
        return new SqliteConnection(configuration, PLUGIN.getDataFolder(), Logger::info);
    }
}
