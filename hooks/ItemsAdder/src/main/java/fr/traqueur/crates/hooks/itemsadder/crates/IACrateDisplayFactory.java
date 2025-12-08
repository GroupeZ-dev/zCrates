package fr.traqueur.crates.hooks.itemsadder.crates;

import dev.lone.itemsadder.api.CustomBlock;
import fr.traqueur.crates.api.models.placedcrates.CrateDisplay;
import fr.traqueur.crates.api.models.placedcrates.CrateDisplayFactory;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class IACrateDisplayFactory implements CrateDisplayFactory<Block> {
    @Override
    public CrateDisplay<Block> create(Location location, String value, float yaw) {
        return new IACrateDisplay(location, value, yaw);
    }

    @Override
    public boolean isValidValue(String value) {
        return CustomBlock.getInstance(value) != null;
    }

    @Override
    public List<String> getSuggestions() {
        return new ArrayList<>(CustomBlock.getNamespacedIdsInRegistry());
    }
}
