package fr.traqueur.crates.api.managers;

import fr.traqueur.crates.api.CratesPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public sealed interface Manager permits AnimationsManager {

    default CratesPlugin getPlugin() {
        return JavaPlugin.getPlugin(CratesPlugin.class);
    }

}
