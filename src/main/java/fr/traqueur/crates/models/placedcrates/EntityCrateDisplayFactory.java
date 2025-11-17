package fr.traqueur.crates.models.placedcrates;

import fr.traqueur.crates.api.models.placedcrates.CrateDisplay;
import fr.traqueur.crates.api.models.placedcrates.CrateDisplayFactory;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.List;

public class EntityCrateDisplayFactory implements CrateDisplayFactory<Entity> {

    @Override
    public CrateDisplay<Entity> create(Location location, String value, float yaw) {
        return new EntityCrateDisplay(location, value, yaw);
    }

    @Override
    public boolean isValidValue(String value) {
        try {
            EntityType entityType = EntityType.valueOf(value.toUpperCase());
            return entityType.isSpawnable();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public List<String> getSuggestions() {
        return Arrays.stream(EntityType.values())
                .filter(EntityType::isSpawnable)
                .map(EntityType::name)
                .toList();
    }
}