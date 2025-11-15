package fr.traqueur.crates.models.rewards;

import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.models.crates.Reward;
import fr.traqueur.crates.api.settings.models.ItemStackWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public record ItemsListReward(String id, ItemStackWrapper displayItem, double weight, List<ItemStackWrapper> items) implements Reward {
    @Override
    public void give(Player player) {
        Logger.debug("Giving item reward {} to player {}", id, player.getName());
        ItemStack[] itemStacks = items.stream().map(item -> item.build(player)).toArray(ItemStack[]::new);
        player.getInventory().addItem(itemStacks).forEach((__, itemStack) -> player.getWorld().dropItemNaturally(player.getLocation(), itemStack));
    }
}
