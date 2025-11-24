package fr.traqueur.crates.api.registries;

import fr.traqueur.crates.api.hooks.Hook;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Registry interface for managing hooks within the application.
 */
public interface HooksRegistry extends Registry<String, Hook> {

    /**
     * Enables all registered hooks.
     */
    void enableAll();

    /**
     * Scans the specified package for hook implementations and registers them.
     *
     * @param plugin      the JavaPlugin instance
     * @param packageName the package name to scan for hooks
     */
    void scanPackage(JavaPlugin plugin, String packageName);

}
