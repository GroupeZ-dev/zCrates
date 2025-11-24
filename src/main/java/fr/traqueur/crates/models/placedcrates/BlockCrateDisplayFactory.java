package fr.traqueur.crates.models.placedcrates;

import fr.traqueur.crates.api.models.placedcrates.CrateDisplay;
import fr.traqueur.crates.api.models.placedcrates.CrateDisplayFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.List;

public class BlockCrateDisplayFactory implements CrateDisplayFactory<Block> {

    @Override
    public CrateDisplay<Block> create(Location location, String value, float yaw) {
        return new BlockCrateDisplay(location, value, yaw);
    }

    @Override
    public boolean isValidValue(String value) {
        try {
            Material material = Material.valueOf(value.toUpperCase());
            return material.isBlock();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public List<String> getSuggestions() {
        return Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .map(Material::name)
                .toList();
    }
}