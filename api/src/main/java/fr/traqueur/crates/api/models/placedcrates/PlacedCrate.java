package fr.traqueur.crates.api.models.placedcrates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

/**
 * Record representing a placed crate in the game world.
 *
 * @param id           Unique identifier for the placed crate.
 * @param crateId      Identifier of the crate type.
 * @param worldName    Name of the world where the crate is placed.
 * @param x            X coordinate of the crate's location.
 * @param y            Y coordinate of the crate's location.
 * @param z            Z coordinate of the crate's location.
 * @param displayType  Type of display for the crate.
 * @param displayValue Value associated with the display type.
 * @param yaw          Yaw orientation of the crate.
 */
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

    /**
     * Converts the PlacedCrate to a Bukkit Location object.
     *
     * @return Location object representing the crate's location, or null if the world does not exist.
     */
    public Location getLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, x, y, z, yaw, 0);
    }
}