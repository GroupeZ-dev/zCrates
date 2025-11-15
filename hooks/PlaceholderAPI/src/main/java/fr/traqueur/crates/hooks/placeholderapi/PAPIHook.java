package fr.traqueur.crates.hooks.placeholderapi;

import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.annotations.AutoHook;
import fr.traqueur.crates.api.hooks.Hook;
import fr.traqueur.crates.api.providers.PlaceholderProvider;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

@AutoHook("PlaceholderAPI")
public class PAPIHook implements Hook, PlaceholderProvider {

    @Override
    public void onEnable() {
        PlaceholderProvider.Holder.setInstance(this);
        Logger.info("PlaceholderAPI hook enabled and registered as global placeholder parser.");
    }

    @Override
    public String parse(Player player, String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        return PlaceholderAPI.setPlaceholders(player, string);
    }

}
