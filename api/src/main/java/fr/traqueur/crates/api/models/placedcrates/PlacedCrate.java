package fr.traqueur.crates.api.models.placedcrates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public record PlacedCrate(
        UUID id,
        String crateId,
        String worldName,
        int x,
        int y,
        int z,
        DisplayType displayType,
        String displayValue,
        float yaw
) {

    public Location getLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, x, y, z, yaw, 0);
    }
}