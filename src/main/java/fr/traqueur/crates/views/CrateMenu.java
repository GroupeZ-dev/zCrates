package fr.traqueur.crates.views;

import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.views.buttons.AnimationButton;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class CrateMenu extends ZInventory {

    private final CratesPlugin plugin;
    private final AnimationButton animationButton;

    public CrateMenu(Plugin plugin, String name, String fileName, int size, List<Button> buttons) {
        super(plugin, name, fileName, size, buttons);
        if(!(plugin instanceof CratesPlugin processingPlugin)) {
            throw new IllegalArgumentException("Plugin must be an instance of CratesPlugin");
        }
        this.plugin = processingPlugin;
        if (buttons.stream().noneMatch(b -> b instanceof AnimationButton)
                || buttons.stream().filter(b -> b instanceof AnimationButton).mapToInt(value -> 1).sum() > 1) {
            throw new IllegalArgumentException("CrateMenu must contain exactly one AnimationButton");
        }
        this.animationButton = buttons.stream()
                .filter(b -> b instanceof AnimationButton)
                .map(AnimationButton.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("CrateMenu must contain an AnimationButton"));
    }

    @Override
    public void postOpenInventory(Player player, InventoryEngine inventoryDefault) {
        super.postOpenInventory(player, inventoryDefault);
        CratesManager cratesManager = plugin.getManager(CratesManager.class);
        cratesManager.startAnimation(player, inventoryDefault.getSpigotInventory(), new ArrayList<>(animationButton.getSlots()));
    }

    @Override
    public void closeInventory(Player player, InventoryEngine inventoryDefault) {
        super.closeInventory(player, inventoryDefault);
        CratesManager cratesManager = plugin.getManager(CratesManager.class);
        cratesManager.closeCrate(player);
    }
}
