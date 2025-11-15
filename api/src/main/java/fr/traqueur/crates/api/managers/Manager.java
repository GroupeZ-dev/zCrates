package fr.traqueur.crates.api.managers;

import fr.traqueur.crates.api.CratesPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public sealed interface Manager permits CratesManager {

    default CratesPlugin getPlugin() {
        return JavaPlugin.getPlugin(CratesPlugin.class);
    }

}
