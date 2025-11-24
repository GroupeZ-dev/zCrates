package fr.traqueur.crates.settings;

import fr.traqueur.crates.api.settings.Settings;
import fr.traqueur.crates.api.settings.models.DatabaseSettings;

public record PluginSettings(
        boolean debug,
        DatabaseSettings database
) implements Settings {
}
