package fr.traqueur.crates.views;

import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.managers.CratesManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CrateMenu extends ZInventory {

    private final CratesPlugin plugin;

    public CrateMenu(Plugin rawPlugin, String name, String fileName, int size, List<Button> buttons) {
        super(rawPlugin, name, fileName, size, buttons);
        if (!(rawPlugin instanceof CratesPlugin processedPlugin)) {
            throw new IllegalArgumentException("Plugin must be an instance of CratesPlugin");
        }
        this.plugin = processedPlugin;
    }

    @Override
    public void postOpenInventory(Player player, InventoryEngine inventoryDefault) {
        super.postOpenInventory(player, inventoryDefault);
        CratesManager cratesManager = plugin.getManager(CratesManager.class);
        cratesManager.startAnimation(player, inventoryDefault.getSpigotInventory());
    }

    @Override
    public void closeInventory(Player player, InventoryEngine inventoryDefault) {
        super.closeInventory(player, inventoryDefault);
        CratesManager cratesManager = plugin.getManager(CratesManager.class);
        cratesManager.closeCrate(player);
    }
}
