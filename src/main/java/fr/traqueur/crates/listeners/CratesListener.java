package fr.traqueur.crates.listeners;

import fr.traqueur.crates.Messages;
import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.placedcrates.PlacedCrate;
import fr.traqueur.crates.api.registries.CratesRegistry;
import fr.traqueur.crates.api.registries.Registry;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

public class CratesListener implements Listener {

    private final CratesManager cratesManager;

    public CratesListener(CratesManager cratesManager) {
        this.cratesManager = cratesManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cratesManager.closeCrate(event.getPlayer());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if(inventory != null && inventory.getHolder() instanceof Crate) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if(inventory.getHolder() instanceof Crate) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        cratesManager.loadPlacedCratesFromChunk(event.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent event) {
        cratesManager.unloadPlacedCratesFromChunk(event.getChunk());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockInteract(PlayerInteractEvent event) {
        // Only process main hand to avoid double execution
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Optional<PlacedCrate> placedCrateOpt = cratesManager.findPlacedCrateByBlock(block);
        if (placedCrateOpt.isEmpty()) {
            return;
        }

        event.setCancelled(true);

        PlacedCrate placedCrate = placedCrateOpt.get();
        Player player = event.getPlayer();

        openPlacedCrate(player, placedCrate);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        // Only process main hand to avoid double execution
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        
        Entity entity = event.getRightClicked();

        Optional<PlacedCrate> placedCrateOpt = cratesManager.findPlacedCrateByEntity(entity);
        if (placedCrateOpt.isEmpty()) {
            return;
        }

        event.setCancelled(true);

        PlacedCrate placedCrate = placedCrateOpt.get();
        Player player = event.getPlayer();

        openPlacedCrate(player, placedCrate);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Optional<PlacedCrate> placedCrateOpt = cratesManager.findPlacedCrateByBlock(block);
        if (placedCrateOpt.isPresent()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Optional<PlacedCrate> placedCrateOpt = cratesManager.findPlacedCrateByEntity(entity);
        if (placedCrateOpt.isPresent()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        Optional<PlacedCrate> placedCrateOpt = cratesManager.findPlacedCrateByEntity(entity);
        if (placedCrateOpt.isPresent()) {
            event.setCancelled(true);
        }
    }

    private void openPlacedCrate(Player player, PlacedCrate placedCrate) {
        CratesRegistry cratesRegistry = Registry.get(CratesRegistry.class);
        Crate crate = cratesRegistry.getById(placedCrate.crateId());
        if (crate == null) {
            return;
        }

        // Check if player has the key
        if (!crate.key().has(player)) {
            Messages.NO_KEY.send(player);
            return;
        }

        // Consume the key
        crate.key().remove(player);

        cratesManager.openCrate(player, crate, crate.animation());
    }
}

