package fr.traqueur.crates.listeners;

import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.api.models.crates.Crate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

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

}
