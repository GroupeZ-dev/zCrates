package fr.traqueur.crates.models.keys;

import fr.traqueur.crates.api.models.crates.Key;
import fr.traqueur.crates.api.settings.models.ItemStackWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record PhysicKey(ItemStackWrapper item) implements Key {

    @Override
    public boolean has(Player player) {
        ItemStack item = this.item.build(player);
        return player.getInventory().containsAtLeast(item, item.getAmount());
    }

    @Override
    public void remove(Player player) {
        ItemStack item = this.item.build(player);
        player.getInventory().removeItemAnySlot(item);
    }

    @Override
    public void give(Player player) {
        ItemStack item = this.item.build(player);
        player.getInventory().addItem(item).forEach((__, leftover) -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
    }


}
