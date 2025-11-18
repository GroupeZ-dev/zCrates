package fr.traqueur.crates.hooks.itemsadder.crates;

import dev.lone.itemsadder.api.CustomBlock;
import fr.traqueur.crates.models.placedcrates.BlockCrateDisplay;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class IACrateDisplay extends BlockCrateDisplay {

    CustomBlock block;

    public IACrateDisplay(Location location, String value, float yaw) {
        super(location, value, yaw);
        this.block = CustomBlock.getInstance(value);
    }

    @Override
    public void spawn() {
        block.place(this.getLocation());
    }

    @Override
    public void remove() {
        CustomBlock.remove(this.getLocation());
    }

    @Override
    public boolean matches(Block block) {
        CustomBlock cb = CustomBlock.byAlreadyPlaced(block);
        return cb != null && cb.getNamespacedID().equals(this.block.getNamespacedID());
    }
}
