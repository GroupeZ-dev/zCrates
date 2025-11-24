package fr.traqueur.crates.api.managers;

import fr.traqueur.crates.api.CratesPlugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Base interface for all manager classes in the zCrates plugin.
 * Managers are responsible for handling specific aspects of the plugin's functionality.
 */
public sealed interface Manager permits CratesManager, UsersManager {

    /** Initializes the manager. This method is called during the plugin's startup sequence. */
    void init();

    /**
     * Gets the main CratesPlugin instance.
     * @return the CratesPlugin instance
     */
    default CratesPlugin getPlugin() {
        return JavaPlugin.getPlugin(CratesPlugin.class);
    }

}
