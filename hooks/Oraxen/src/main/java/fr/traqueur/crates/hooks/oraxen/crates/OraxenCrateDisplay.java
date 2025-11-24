package fr.traqueur.crates.hooks.oraxen.crates;

import fr.traqueur.crates.models.placedcrates.BlockCrateDisplay;
import io.th0rgal.oraxen.api.OraxenBlocks;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class OraxenCrateDisplay extends BlockCrateDisplay {

    private final String blockId;

    public OraxenCrateDisplay(Location location, String value, float yaw) {
        super(location, value, yaw);
        this.blockId = value;
    }

    @Override
    public void spawn() {
        OraxenBlocks.place(this.blockId, this.getLocation());
    }

    @Override
    public void remove() {
        OraxenBlocks.remove(this.getLocation(), null);
    }

    @Override
    public boolean matches(Block block) {
        var oraxenBlock = OraxenBlocks.getOraxenBlock(block.getLocation());
        if (oraxenBlock == null) {
            return false;
        }
        return oraxenBlock.getItemID().equals(this.blockId);
    }
}