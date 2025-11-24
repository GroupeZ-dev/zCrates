package fr.traqueur.crates.hooks.nexo.crates;

import com.nexomc.nexo.api.NexoBlocks;
import fr.traqueur.crates.api.models.placedcrates.CrateDisplay;
import fr.traqueur.crates.api.models.placedcrates.CrateDisplayFactory;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NexoCrateDisplayFactory implements CrateDisplayFactory<Block> {
    @Override
    public CrateDisplay<Block> create(Location location, String value, float yaw) {
        return new NexoCrateDisplay(location, value, yaw);
    }

    @Override
    public boolean isValidValue(String value) {
        return NexoBlocks.isCustomBlock(value);
    }

    @Override
    public List<String> getSuggestions() {
        return Arrays.asList(NexoBlocks.blockIDs());
    }
}