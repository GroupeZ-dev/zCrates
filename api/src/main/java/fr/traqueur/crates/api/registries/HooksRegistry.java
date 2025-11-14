package fr.traqueur.crates.api.registries;

import fr.traqueur.crates.api.hooks.Hook;
import org.bukkit.plugin.java.JavaPlugin;


public interface HooksRegistry extends Registry<String, Hook> {

    void enableAll();

    void scanPackage(JavaPlugin plugin, String packageName);

}
