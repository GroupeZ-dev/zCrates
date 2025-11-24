package fr.traqueur.crates.api.managers;

import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.models.crates.Reward;
import fr.traqueur.crates.api.models.placedcrates.DisplayType;
import fr.traqueur.crates.api.models.placedcrates.PlacedCrate;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Optional;

public non-sealed interface CratesManager extends Manager {

    boolean tryOpenCrate(Player player, Crate crate);

    void openCrate(Player player, Crate crate, Animation animation);

    void openPreview(Player player, Crate crate);

    Optional<Crate> getPreviewingCrate(Player player);

    void closePreview(Player player);

    void startAnimation(Player player, Inventory inventory, List<Integer> slots);

    void stopAllOpening();

    void closeCrate(Player player);

    void ensureInventoriesExist();

    // Placed crates management
    PlacedCrate placeCrate(String crateId, Location location, DisplayType displayType, String displayValue, float yaw);

    void removePlacedCrate(PlacedCrate placedCrate);

    Optional<PlacedCrate> findPlacedCrateByBlock(Block block);

    Optional<PlacedCrate> findPlacedCrateByEntity(Entity entity);

    void loadPlacedCratesFromChunk(Chunk chunk);

    void unloadPlacedCratesFromChunk(Chunk chunk);

    void loadAllPlacedCrates();

    void unloadAllPlacedCrates();

    List<PlacedCrate> getPlacedCratesInWorld(World world);
}
