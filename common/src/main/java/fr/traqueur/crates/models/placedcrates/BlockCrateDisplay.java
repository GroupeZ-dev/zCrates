package fr.traqueur.crates.models.placedcrates;

import fr.traqueur.crates.api.models.placedcrates.CrateDisplay;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockCrateDisplay implements CrateDisplay<Block> {

    private final Location location;
    private final Material material;

    public BlockCrateDisplay(Location location, String value, float yaw) {
        this.location = location;
        this.material = Material.valueOf(value.toUpperCase());
    }

    @Override
    public void spawn() {
        Block block = location.getBlock();
        block.setType(material);
    }

    @Override
    public void remove() {
        location.getBlock().setType(Material.AIR);
    }

    @Override
    public boolean matches(Block block) {
        return block.getLocation().equals(location);
    }

    @Override
    public Location getLocation() {
        return location;
    }
}