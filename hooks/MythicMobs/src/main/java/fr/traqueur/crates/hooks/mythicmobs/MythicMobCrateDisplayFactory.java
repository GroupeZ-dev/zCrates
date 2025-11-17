package fr.traqueur.crates.hooks.mythicmobs;

import fr.traqueur.crates.api.models.placedcrates.CrateDisplay;
import fr.traqueur.crates.api.models.placedcrates.CrateDisplayFactory;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.List;

public class MythicMobCrateDisplayFactory implements CrateDisplayFactory<Entity> {

    @Override
    public CrateDisplay<Entity> create(Location location, String value, float yaw) {
        return new MythicMobCrateDisplay(location, value, yaw);
    }

    @Override
    public boolean isValidValue(String value) {
        return MythicBukkit.inst().getMobManager().getMythicMob(value).isPresent();
    }

    @Override
    public List<String> getSuggestions() {
        return MythicBukkit.inst().getMobManager().getMobNames().stream().toList();
    }
}