package fr.traqueur.crates.settings;

import fr.traqueur.crates.api.settings.Settings;

public record PluginSettings(
        boolean debug
) implements Settings {
}
