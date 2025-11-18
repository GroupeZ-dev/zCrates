package fr.traqueur.crates.hooks.oraxen.crates;

import fr.traqueur.crates.api.models.placedcrates.CrateDisplay;
import fr.traqueur.crates.api.models.placedcrates.CrateDisplayFactory;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class OraxenCrateDisplayFactory implements CrateDisplayFactory<Block> {
    @Override
    public CrateDisplay<Block> create(Location location, String value, float yaw) {
        return new OraxenCrateDisplay(location, value, yaw);
    }

    @Override
    public boolean isValidValue(String value) {
        return OraxenBlocks.isOraxenBlock(value);
    }

    @Override
    public List<String> getSuggestions() {
        
        return new ArrayList<>(OraxenBlocks.getBlockIDs());
    }
}