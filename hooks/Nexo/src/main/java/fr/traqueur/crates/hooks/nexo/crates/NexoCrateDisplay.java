package fr.traqueur.crates.hooks.nexo.crates;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import fr.traqueur.crates.models.placedcrates.BlockCrateDisplay;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class NexoCrateDisplay extends BlockCrateDisplay {

    private final String blockId;

    public NexoCrateDisplay(Location location, String value, float yaw) {
        super(location, value, yaw);
        this.blockId = value;
    }

    @Override
    public void spawn() {
        NexoBlocks.place(this.blockId, this.getLocation());
    }

    @Override
    public void remove() {
        NexoBlocks.remove(this.getLocation());
    }

    @Override
    public boolean matches(Block block) {
        boolean isBlockMechanic = NexoBlocks.isCustomBlock(block);
        if(!isBlockMechanic) {
            return false;
        }
        CustomBlockMechanic blockMechanic = NexoBlocks.customBlockMechanic(block);
        if(blockMechanic == null) {
            return false;
        }
        String nexoBlockId = blockMechanic.getItemID();
        return nexoBlockId.equals(this.blockId);
    }
}