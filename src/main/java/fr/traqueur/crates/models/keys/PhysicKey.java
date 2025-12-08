package fr.traqueur.crates.models.keys;

import fr.traqueur.crates.api.models.crates.Key;
import fr.traqueur.crates.api.settings.models.ItemStackWrapper;
import fr.traqueur.crates.api.serialization.Keys;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Optional;

public record PhysicKey(String name, ItemStackWrapper item) implements Key {

    @Override
    public boolean has(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.hasItemMeta()) {
                Optional<String> keyName = Keys.KEY_NAME.get(itemStack.getItemMeta().getPersistentDataContainer());
                if (keyName.isPresent() && keyName.get().equals(this.name)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void remove(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.hasItemMeta()) {
                Optional<String> keyName = Keys.KEY_NAME.get(itemStack.getItemMeta().getPersistentDataContainer());
                if (keyName.isPresent() && keyName.get().equals(this.name)) {
                    itemStack.setAmount(itemStack.getAmount() - 1);
                    return;
                }
            }
        }
    }

    @Override
    public void give(Player player) {
        ItemStack item = this.item.build(player);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            Keys.KEY_NAME.set(container, this.name);
            item.setItemMeta(itemMeta);
        }
        player.getInventory().addItem(item).forEach((__, leftover) -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
    }

    @Override
    public int count(Player player) {
        int count = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.hasItemMeta()) {
                Optional<String> keyName = Keys.KEY_NAME.get(itemStack.getItemMeta().getPersistentDataContainer());
                if (keyName.isPresent() && keyName.get().equals(this.name)) {
                    count += itemStack.getAmount();
                }
            }
        }
        return count;
    }


}
