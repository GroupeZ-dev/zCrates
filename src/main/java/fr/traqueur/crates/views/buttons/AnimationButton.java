package fr.traqueur.crates.views.buttons;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AnimationButton extends Button {

    @Override
    public void onRender(Player player, InventoryEngine inventoryEngine) {
        for (Integer slot : slots) {
            inventoryEngine.addItem(slot, ItemStack.of(Material.AIR));
        }
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }
}
